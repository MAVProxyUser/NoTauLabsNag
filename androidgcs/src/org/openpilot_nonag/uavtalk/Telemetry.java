/**
 ******************************************************************************
 * @file       Telemetry.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      Port of Telemetry.cpp from the GCS.  Handles transactions on the
 *             UAVTalk channel.
 * @see        The GNU Public License (GPL) Version 3
 *
 *****************************************************************************/
/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.openpilot_nonag.uavtalk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import junit.framework.Assert;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

public class Telemetry {
	/**
	 * Telemetry provides a messaging handler to handle all the object updates
	 * and transfer requests. This handler can either be attached to a new loop
	 * attached to the thread started by the telemetry service.
	 */

	private final String TAG = "Telemetry";
	public static int LOGLEVEL = 0;
	public static boolean DEBUG = LOGLEVEL > 2;
	public static boolean WARN  = LOGLEVEL > 1;
	public static boolean ERROR = LOGLEVEL > 0;

	public class TelemetryStats {
		public int txBytes;
		public int rxBytes;
		public int txObjectBytes;
		public int rxObjectBytes;
		public int rxObjects;
		public int txObjects;
		public int txErrors;
		public int rxErrors;
		public int txRetries;
	};

	class ObjectTimeInfo {
		UAVObject obj;
		int updatePeriodMs;
		/** Update period in ms or 0 if no periodic updates are needed */
		int timeToNextUpdateMs;
		/** Time delay to the next update */
	};

	class ObjectQueueInfo {
		UAVObject obj;
		int event;
		boolean allInstances;

		@Override
		public boolean equals(Object e) {
			try {
				ObjectQueueInfo o = (ObjectQueueInfo) e;
				return o.obj.getObjID() == obj.getObjID() && o.event == event
						&& o.allInstances == allInstances;
			} catch (Exception err) {

			}
			;
			return false;
		}
	};

	class ObjectTransactionInfo {
		UAVObject obj;
		boolean allInstances;
		boolean objRequest;
		int retriesRemaining;
		boolean acked;
	};

	/**
	 * Events generated by objects. Not enum because used in mask.
	 */
	private static final int EV_UNPACKED = 0x01;
	/** Object data updated by unpacking */
	private static final int EV_UPDATED = 0x02;
	/** Object data updated by changing the data structure */
	private static final int EV_UPDATED_MANUAL = 0x04;
	/** Object update event manually generated */
	private static final int EV_UPDATE_REQ = 0x08;

	/** Request to update object data */

	/**
	 * Constructor
	 */
	public Telemetry(UAVTalk utalkIn, UAVObjectManager objMngr, Looper l) {
		this.utalk = utalkIn;
		this.objMngr = objMngr;

		// Create a handler for object messages
		handler = new ObjectUpdateHandler(l);

		// Process all objects in the list
		List<List<UAVObject>> objs = objMngr.getObjects();
		ListIterator<List<UAVObject>> li = objs.listIterator();
		while (li.hasNext())
			registerObject(li.next().get(0)); // we only need to register one
		// instance per object type

		// Listen to new object creations
		objMngr.addNewInstanceObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				newInstance((UAVObject) data);
			}
		});
		objMngr.addNewObjectObserver(new Observer() {
			@Override
			public void update(Observable observable, Object data) {
				newObject((UAVObject) data);
			}
		});

		// Listen to transaction completions from uavtalk
		utalk.setOnTransactionCompletedListener(utalk.new OnTransactionCompletedListener() {
			@Override
			void TransactionSucceeded(UAVObject data) {
				transactionCompleted(data, true);
			}

			@Override
			void TransactionFailed(UAVObject data) {
				if (DEBUG)
					Log.d(TAG, "TransactionFailed(" + data.getName() + ")");

				transactionCompleted(data, false);
			}

		});

		// Get GCS stats object
		gcsStatsObj = objMngr.getObject("GCSTelemetryStats");

		// Setup transaction timer
		transPending = false;
		// Setup and start the periodic timer
		timeToNextUpdateMs = 0;
		updateTimerSetPeriod(1000);
		// Setup and start the stats timer
		txErrors = 0;
		txRetries = 0;
	}

	synchronized void updateTimerSetPeriod(int periodMs) {
		if (updateTimer != null) {
			updateTimer.cancel();
			updateTimer = null;
		}
		if (updateTimerTask != null) {
			updateTimerTask.cancel();
			updateTimerTask = null;
		}
		updateTimer = new Timer();
		updateTimerTask = new TimerTask() {
			@Override
			public void run() {
				try {
					processPeriodicUpdates();
				} catch (IOException e) {
					updateTimerTask.cancel();
					updateTimer.cancel();
				}
			}
		};
		updateTimer.schedule(updateTimerTask, periodMs, periodMs);

	}

	/**
	 * Register a new object for periodic updates (if enabled)
	 */
	private void registerObject(UAVObject obj) {
		// Setup object for periodic updates
		addObject(obj);

		// Setup object for telemetry updates
		updateObject(obj);
	}

	/**
	 * Add an object in the list used for periodic updates
	 */
	private void addObject(UAVObject obj) {

		synchronized (objList) {
			// Check if object type is already in the list
			ListIterator<ObjectTimeInfo> li = objList.listIterator();
			while (li.hasNext()) {
				ObjectTimeInfo n = li.next();
				if (n.obj.getObjID() == obj.getObjID()) {
					// Object type (not instance!) is already in the list, do
					// nothing
					return;
				}
			}

			// If this point is reached, then the object type is new, let's add it
			ObjectTimeInfo timeInfo = new ObjectTimeInfo();
			timeInfo.obj = obj;
			timeInfo.timeToNextUpdateMs = 0;
			timeInfo.updatePeriodMs = 0;
			objList.add(timeInfo);
		}
	}

	/**
	 * Update the object's timers
	 */
	private synchronized void setUpdatePeriod(UAVObject obj, int periodMs) {
		// Find object type (not instance!) and update its period
		ListIterator<ObjectTimeInfo> li = objList.listIterator();
		while (li.hasNext()) {
			ObjectTimeInfo n = li.next();
			if (n.obj.getObjID() == obj.getObjID()) {
				n.updatePeriodMs = periodMs;
				n.timeToNextUpdateMs = (int) (periodMs * (new java.util.Random())
						.nextDouble()); // avoid bunching of updates
			}
		}
	}

	final Observer unpackedObserver = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			handler.unpacked((UAVObject) data);
		}
	};

	final Observer updatedAutoObserver = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			handler.updatedAuto((UAVObject) data);
		}
	};

	final Observer updatedManualObserver = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			handler.updatedManual((UAVObject) data);
		}
	};

	final Observer updatedRequestedObserver = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			handler.updateRequested((UAVObject) data);
		}
	};

	/**
	 * Connect to all instances of an object depending on the event mask
	 * specified
	 */
	private void connectToObjectInstances(UAVObject obj,
			int eventMask) {
		List<UAVObject> objs = objMngr.getObjectInstances(obj.getObjID());
		ListIterator<UAVObject> li = objs.listIterator();
		while (li.hasNext()) {
			obj = li.next();

			// Disconnect all previous observers from telemetry. This is
			// imortant as this can
			// be called multiple times
			obj.removeUnpackedObserver(unpackedObserver);
			obj.removeUpdatedAutoObserver(updatedAutoObserver);
			obj.removeUpdatedManualObserver(updatedManualObserver);
			obj.removeUpdateRequestedObserver(updatedRequestedObserver);

			// Connect only the selected events
			if ((eventMask & EV_UNPACKED) != 0)
				obj.addUnpackedObserver(unpackedObserver);
			if ((eventMask & EV_UPDATED) != 0)
				obj.addUpdatedAutoObserver(updatedAutoObserver);
			if ((eventMask & EV_UPDATED_MANUAL) != 0)
				obj.addUpdatedManualObserver(updatedManualObserver);
			if ((eventMask & EV_UPDATE_REQ) != 0)
				obj.addUpdateRequestedObserver(updatedRequestedObserver);
		}
	}

	/**
	 * Update an object based on its metadata properties
	 */
	private void updateObject(UAVObject obj) {

		synchronized(obj) {
			// Get metadata
			UAVObject.Metadata metadata = obj.getMetadata();

			// Setup object depending on update mode
			int eventMask;
			if (metadata.GetGcsTelemetryUpdateMode() == UAVObject.UpdateMode.UPDATEMODE_PERIODIC) {
				// Set update period
				setUpdatePeriod(obj, metadata.gcsTelemetryUpdatePeriod);
				// Connect signals for all instances
				eventMask = EV_UPDATED_MANUAL | EV_UPDATE_REQ;
				if (obj.isMetadata())
					eventMask |= EV_UNPACKED; // we also need to act on remote
				// updates (unpack events)

				connectToObjectInstances(obj, eventMask);
			} else if (metadata.GetGcsTelemetryUpdateMode() == UAVObject.UpdateMode.UPDATEMODE_ONCHANGE) {
				// Set update period
				setUpdatePeriod(obj, 0);
				// Connect signals for all instances
				eventMask = EV_UPDATED | EV_UPDATED_MANUAL | EV_UPDATE_REQ;
				if (obj.isMetadata())
					eventMask |= EV_UNPACKED; // we also need to act on remote
				// updates (unpack events)

				connectToObjectInstances(obj, eventMask);
			} else if (metadata.GetGcsTelemetryUpdateMode() == UAVObject.UpdateMode.UPDATEMODE_THROTTLED) {
				// TODO
			} else if (metadata.GetGcsTelemetryUpdateMode() == UAVObject.UpdateMode.UPDATEMODE_MANUAL) {
				// Set update period
				setUpdatePeriod(obj, 0);
				// Connect signals for all instances
				eventMask = EV_UPDATED_MANUAL | EV_UPDATE_REQ;
				if (obj.isMetadata())
					eventMask |= EV_UNPACKED; // we also need to act on remote
				// updates (unpack events)

				connectToObjectInstances(obj, eventMask);
			}
		}
	}

	/**
	 * Check is any objects are pending for periodic updates TODO: Clean-up
	 *
	 * @throws IOException
	 */
	private void processPeriodicUpdates() throws IOException {

		if (DEBUG)
			Log.d(TAG, "processPeriodicUpdates()");
		// Stop timer

		updateTimer.cancel();

		// Iterate through each object and update its timer, if zero then
		// transmit object.
		// Also calculate smallest delay to next update (will be used for
		// setting timeToNextUpdateMs)
		int minDelay = MAX_UPDATE_PERIOD_MS;
		ObjectTimeInfo objinfo;
		int elapsedMs = 0;
		long startTime;
		int offset;
		ListIterator<ObjectTimeInfo> li = objList.listIterator();
		while (li.hasNext()) {
			objinfo = li.next();
			// If object is configured for periodic updates
			if (objinfo.updatePeriodMs > 0) {
				objinfo.timeToNextUpdateMs -= timeToNextUpdateMs;
				// Check if time for the next update
				if (objinfo.timeToNextUpdateMs <= 0) {
					// Reset timer
					offset = (-objinfo.timeToNextUpdateMs)
							% objinfo.updatePeriodMs;
					objinfo.timeToNextUpdateMs = objinfo.updatePeriodMs
							- offset;
					// Send object
					startTime = System.currentTimeMillis();

					if (DEBUG) Log.d(TAG, "Manual update: " + objinfo.obj.getName());
					handler.updatedManual(objinfo.obj);
					// enqueueObjectUpdates(objinfo.obj, EV_UPDATED_MANUAL,
					// true, false);
					elapsedMs = (int) (System.currentTimeMillis() - startTime);
					// Update timeToNextUpdateMs with the elapsed delay of
					// sending the object;
					timeToNextUpdateMs += elapsedMs;
				}
				// Update minimum delay
				if (objinfo.timeToNextUpdateMs < minDelay) {
					minDelay = objinfo.timeToNextUpdateMs;
				}
			}
		}

		// Check if delay for the next update is too short
		if (minDelay < MIN_UPDATE_PERIOD_MS) {
			minDelay = MIN_UPDATE_PERIOD_MS;
		}

		// Done
		timeToNextUpdateMs = minDelay;

		// Restart timer
		updateTimerSetPeriod(timeToNextUpdateMs);
	}

	public TelemetryStats getStats() {
		// Get UAVTalk stats
		UAVTalk.ComStats utalkStats = utalk.getStats();

		// Update stats
		TelemetryStats stats = new TelemetryStats();
		stats.txBytes = utalkStats.txBytes;
		stats.rxBytes = utalkStats.rxBytes;
		stats.txObjectBytes = utalkStats.txObjectBytes;
		stats.rxObjectBytes = utalkStats.rxObjectBytes;
		stats.rxObjects = utalkStats.rxObjects;
		stats.txObjects = utalkStats.txObjects;
		stats.txErrors = utalkStats.txErrors + txErrors;
		stats.rxErrors = utalkStats.rxErrors;
		stats.txRetries = txRetries;

		// Done
		return stats;
	}

	public void resetStats() {
		utalk.resetStats();
		txErrors = 0;
		txRetries = 0;
	}

	private void newObject(UAVObject obj) {
		registerObject(obj);
	}

	private void newInstance(UAVObject obj) {
		registerObject(obj);
	}

	/**
	 * Stop all the telemetry timers
	 */
	public void stopTelemetry() {
		if (updateTimerTask != null)
			updateTimerTask.cancel();
		updateTimerTask = null;
		if (updateTimer != null)
			updateTimer.cancel();
		updateTimer = null;
	}

	/**
	 * Private variables
	 */
	private final UAVObjectManager objMngr;
	private final UAVTalk utalk;
	private UAVObject gcsStatsObj;
	private final List<ObjectTimeInfo> objList = new ArrayList<ObjectTimeInfo>();
	private ObjectTransactionInfo transInfo = new ObjectTransactionInfo();
	private boolean transPending;

	private Timer updateTimer;
	private TimerTask updateTimerTask;

	private int timeToNextUpdateMs;
	private int txErrors;
	private int txRetries;

	/**
	 * Private constants
	 */
	private static final int REQ_TIMEOUT_MS = 250;
	private static final int MAX_RETRIES = 2;
	private static final int MAX_UPDATE_PERIOD_MS = 1000;
	private static final int MIN_UPDATE_PERIOD_MS = 1;

	static private ObjectUpdateHandler handler;

	//! Accessor for the object updated handler
	ObjectUpdateHandler getHandler() { return handler; }

	/**
	 * Handler which posts all the messages for individual object updates
	 */
	public class ObjectUpdateHandler extends Handler {

		// ! This can only be created while attaching to a particular looper
		ObjectUpdateHandler(Looper l) {
			super(l);
		}

		Queue<ObjectQueueInfo> objQueue = new ConcurrentLinkedQueue<ObjectQueueInfo>();

		// ! Generic enqueue
		void enqueueObjectUpdates(UAVObject obj, int event,
				boolean allInstances, boolean priority) {

			if (DEBUG) Log.d(TAG, "Enqueing update " + obj.getName() + " event " + event);

			ObjectQueueInfo objInfo = new ObjectQueueInfo();
			objInfo.obj = obj;
			objInfo.event = event;
			objInfo.allInstances = allInstances;

			// For now maintain a list of objects in the queue so we don't add duplicates
			// later we should make the runnables static to each class so we can use removeCallback
			synchronized(objQueue) {
				if (objQueue.contains(objInfo)) {
					if (WARN) Log.w(TAG, "Found previously scheduled queue element: " + objInfo.obj.getName());
				} else {
					objQueue.add(objInfo);
					post(new ObjectRunnable(objInfo));
				}
			}
		}

		public boolean removeActivatedQueue(ObjectQueueInfo objInfo) {
			synchronized(objQueue) {
				if (objQueue.remove(objInfo)) {
					if (WARN) Log.w(TAG, "Unable to find queue element to remove");
					return false;
				}
			}
			return true;
		}

		// ! Enqueue an unpacked event
		void unpacked(UAVObject obj) {
			enqueueObjectUpdates(obj, EV_UNPACKED, false, true);
		}

		// ! Enqueue an updated auto event
		void updatedAuto(UAVObject obj) {
			enqueueObjectUpdates(obj, EV_UPDATED, false, true);
		}

		// ! Enqueue an updated manual event
		void updatedManual(UAVObject obj) {
			enqueueObjectUpdates(obj, EV_UPDATED_MANUAL, false, true);
		}

		// ! Enqueue an update requested event
		void updateRequested(UAVObject obj) {
			enqueueObjectUpdates(obj, EV_UPDATE_REQ, false, true);
		}

	}

	/**
	 * Perform an update on an object where on an event based on the contents provided
	 * to the constructors.  This update will also set a timeout for transaction failure.
	 */
	class ObjectRunnable implements Runnable {

		// ! Transaction information to perform
		private final ObjectQueueInfo objInfo;

		ObjectRunnable(ObjectQueueInfo info) {
			Assert.assertNotNull(info);
			objInfo = info;
		}

		// ! Perform the transaction on the looper thread
		@Override
		public void run() {
			if (DEBUG) Log.d(TAG, "Object transaction running.  Event:" + objInfo.event);
			// 1. Check GCS is connected, throw this out if not
			// 2. Set up a transaction which includes multiple retries, whether
			// to wait for ack etc
			// 3. Send UAVTalk message
			// 4. Based on transaction type either wait for update or end

			// 1. Check if a connection has been established, only process
			// GCSTelemetryStats updates
			// (used to establish the connection)
			gcsStatsObj = objMngr.getObject("GCSTelemetryStats");
			if (((String) gcsStatsObj.getField("Status").getValue()).compareTo("Connected") != 0) {
				if (objInfo.obj.getObjID() != objMngr.getObject("GCSTelemetryStats").getObjID()) {
					if (DEBUG)
						Log.d(TAG, "transactionCompleted(false) due to receiving object not GCSTelemetryStats while not connected.");
					objInfo.obj.transactionCompleted(false);
					return;
				}
			}

			// If this is a metaobject then make necessary telemetry updates
			// (this is why we catch unpack)
			if (objInfo.obj.isMetadata()) {
				UAVMetaObject metaobj = (UAVMetaObject) objInfo.obj;
				updateObject(metaobj.getParentObject());
			}

			// 2. Setup transaction (skip if unpack event)
			ObjectTransactionInfo newTrans = new ObjectTransactionInfo();
			boolean newTransactionPending = false;
			if (objInfo.event != EV_UNPACKED) {
				UAVObject.Metadata metadata = objInfo.obj.getMetadata();
				newTrans.obj = objInfo.obj;
				newTrans.allInstances = objInfo.allInstances;
				newTrans.retriesRemaining = MAX_RETRIES;
				newTrans.acked = metadata.GetGcsTelemetryAcked();
				if (objInfo.event == EV_UPDATED || objInfo.event == EV_UPDATED_MANUAL) {
					newTrans.objRequest = false;
				} else if (objInfo.event == EV_UPDATE_REQ) {
					newTrans.objRequest = true;
				}

				// Determine if this will schedule a new transaction
				newTransactionPending = (newTrans.objRequest || newTrans.acked);

				synchronized (transInfo) {

					// If there is a transaction pending and this would set up a new one reschedule it
					if (transPending && newTransactionPending) {
						if (WARN) Log.w(TAG, "Postponing transaction for" + newTrans.obj.getName() + " existing transaction for " + transInfo.obj.getName());
						handler.postDelayed(this, 100);
						return;
					}

					if (DEBUG) Log.d(TAG, "Process Object transaction for " + newTrans.obj.getName());

					// Remove this one from the list of pending transactions
					handler.removeActivatedQueue(objInfo);

					try {

						// 3. Execute transaction by sending the appropriate UAVTalk command
						if (newTrans.objRequest) {
							if (DEBUG) Log.d(TAG, "Sending object request " + newTrans.obj.getName());
							utalk.sendObjectRequest(newTrans.obj, newTrans.allInstances);
						} else {
							if (DEBUG) Log.d(TAG, "Sending object " + newTrans.obj.getName());
							utalk.sendObject(newTrans.obj, newTrans.acked, newTrans.allInstances);
						}

					} catch (IOException e) {
						if (ERROR) Log.e(TAG, "Unable to send UAVTalk message");
						e.printStackTrace();
					}

					// Store this as the active transaction.  However in the case
					// of transPending && !newTransactionPending we need ot not
					// override the previous pending transaction
					if (!transPending && newTransactionPending) {
						transPending = newTransactionPending;
						transInfo = newTrans;

						// Post a timeout timer if a response is epxected
						handler.postDelayed(transactionTimeout, REQ_TIMEOUT_MS);
					}

				}
			}
		}
	}


	/**
	 * Runnable posted to handle a timeout of a transaction.  Tracks the number of retry attempts
	 * retries that many, and finally sends a transaction failed signal.
	 */
	final Runnable transactionTimeout = new Runnable() {
		@Override
		public void run() {
			// Lock on the transaction
			synchronized (transInfo) {

				// Proceed only if there is a pending transaction
				if (!transPending) {
					if (WARN) Log.w(TAG,"Transaction completed but timeout still called.  Probable race condition");
					return;
				}

				if (DEBUG) Log.d(TAG, "Telemetry: transaction timeout.");

				// Check if more retries are pending
				if (transInfo.retriesRemaining > 0) {
					--transInfo.retriesRemaining;

					// Repeat whatever is required for this transaction type
					// (transInfo.objRequest) {
					if (DEBUG) Log.d(TAG, "Sending object request");

					try {
						// Execute transaction by sending the appropriate UAVTalk command
						if (transInfo.objRequest) {
							if (DEBUG) Log.d(TAG, "Sending object request" + transInfo.obj.getName());
							utalk.sendObjectRequest(transInfo.obj, transInfo.allInstances);
						} else {
							if (DEBUG) Log.d(TAG, "Sending object " + transInfo.obj.getName());
							utalk.sendObject(transInfo.obj, transInfo.acked, transInfo.allInstances);
						}
					} catch (IOException e) {
						if (ERROR) Log.e(TAG, "Unable to send UAVTalk message");
						e.printStackTrace();
					}

					handler.postDelayed(transactionTimeout, REQ_TIMEOUT_MS);

					++txRetries;
				} else {
					if (ERROR) Log.e(TAG, "Transaction failed for: " + transInfo.obj.getName());

					// Terminate transaction. This triggers UAVTalk to send a transaction
					// failed signal which will make the next queue entry be processed
					// Note this is UAVTalk listener TransactionFailed function
					// object specific transaction failed.
					utalk.cancelPendingTransaction(transInfo.obj);
					++txErrors;
				}
			}
		}
	};


	/**
	 * Called when a transaction is successfully completed (UAVTalk event) and maps that to
	 * the appropriate object event as well as canceling the pending transaction and timeout
	 */
	private void transactionCompleted(UAVObject obj, boolean result) {

		if (DEBUG) Log.d(TAG, "UAVTalk transactionCompleted");

		// Check if there is a pending transaction and the objects match
		synchronized(transInfo) {
			if (transPending && transInfo.obj.getObjID() == obj.getObjID()) {
				if (DEBUG) Log.d(TAG, "Telemetry: transaction completed for " + obj.getName());

				// Cancel timeout and complete transaction
				handler.removeCallbacks(transactionTimeout);
				transPending = false;

				//Send signal
				obj.transactionCompleted(result);
			} else {
				if (ERROR) Log.e(TAG, "Error: received a transaction completed when did not expect it. " + obj.getName());
				transPending = false;
			}
		}
	}

}

