/**
 ******************************************************************************
 * @file       UAVTalk.java
 * @author     The OpenPilot Team, http://www.openpilot.org Copyright (C) 2012.
 * @brief      The protocol layer implementation of UAVTalk.  Serializes objects
 *             for transmission (which is done in the object itself which is aware
 *             of byte packing) wraps that in the UAVTalk packet.  Parses UAVTalk
 *             packets and updates the UAVObjectManager.
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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.HashMap;

import junit.framework.Assert;
import android.util.Log;

public class UAVTalk {

	static final String TAG = "UAVTalk";
	public static int LOGLEVEL = 4;
	public static boolean VERBOSE = LOGLEVEL > 3;
	public static boolean WARN = LOGLEVEL > 2;
	public static boolean DEBUG = LOGLEVEL > 1;
	public static boolean ERROR = LOGLEVEL > 0;

	private Thread inputProcessingThread = null;

	/**
	 * A reference to the thread for processing the incoming stream.  Currently this method is ONLY
	 * used for unit testing
	 */
	public Thread getInputProcessThread() {
		if (inputProcessingThread == null)

			inputProcessingThread = new Thread() {
				@Override
				public void run() {
					while(true) {
						try {
							if (!processInputStream()) {
								break;
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
		return inputProcessingThread;
	}

	/**
	 * Constants
	 */
	private static final int SYNC_VAL = 0x3C;

	private static final short crc_table[] = { 0x00, 0x07, 0x0e, 0x09, 0x1c,
			0x1b, 0x12, 0x15, 0x38, 0x3f, 0x36, 0x31, 0x24, 0x23, 0x2a, 0x2d,
			0x70, 0x77, 0x7e, 0x79, 0x6c, 0x6b, 0x62, 0x65, 0x48, 0x4f, 0x46,
			0x41, 0x54, 0x53, 0x5a, 0x5d, 0xe0, 0xe7, 0xee, 0xe9, 0xfc, 0xfb,
			0xf2, 0xf5, 0xd8, 0xdf, 0xd6, 0xd1, 0xc4, 0xc3, 0xca, 0xcd, 0x90,
			0x97, 0x9e, 0x99, 0x8c, 0x8b, 0x82, 0x85, 0xa8, 0xaf, 0xa6, 0xa1,
			0xb4, 0xb3, 0xba, 0xbd, 0xc7, 0xc0, 0xc9, 0xce, 0xdb, 0xdc, 0xd5,
			0xd2, 0xff, 0xf8, 0xf1, 0xf6, 0xe3, 0xe4, 0xed, 0xea, 0xb7, 0xb0,
			0xb9, 0xbe, 0xab, 0xac, 0xa5, 0xa2, 0x8f, 0x88, 0x81, 0x86, 0x93,
			0x94, 0x9d, 0x9a, 0x27, 0x20, 0x29, 0x2e, 0x3b, 0x3c, 0x35, 0x32,
			0x1f, 0x18, 0x11, 0x16, 0x03, 0x04, 0x0d, 0x0a, 0x57, 0x50, 0x59,
			0x5e, 0x4b, 0x4c, 0x45, 0x42, 0x6f, 0x68, 0x61, 0x66, 0x73, 0x74,
			0x7d, 0x7a, 0x89, 0x8e, 0x87, 0x80, 0x95, 0x92, 0x9b, 0x9c, 0xb1,
			0xb6, 0xbf, 0xb8, 0xad, 0xaa, 0xa3, 0xa4, 0xf9, 0xfe, 0xf7, 0xf0,
			0xe5, 0xe2, 0xeb, 0xec, 0xc1, 0xc6, 0xcf, 0xc8, 0xdd, 0xda, 0xd3,
			0xd4, 0x69, 0x6e, 0x67, 0x60, 0x75, 0x72, 0x7b, 0x7c, 0x51, 0x56,
			0x5f, 0x58, 0x4d, 0x4a, 0x43, 0x44, 0x19, 0x1e, 0x17, 0x10, 0x05,
			0x02, 0x0b, 0x0c, 0x21, 0x26, 0x2f, 0x28, 0x3d, 0x3a, 0x33, 0x34,
			0x4e, 0x49, 0x40, 0x47, 0x52, 0x55, 0x5c, 0x5b, 0x76, 0x71, 0x78,
			0x7f, 0x6a, 0x6d, 0x64, 0x63, 0x3e, 0x39, 0x30, 0x37, 0x22, 0x25,
			0x2c, 0x2b, 0x06, 0x01, 0x08, 0x0f, 0x1a, 0x1d, 0x14, 0x13, 0xae,
			0xa9, 0xa0, 0xa7, 0xb2, 0xb5, 0xbc, 0xbb, 0x96, 0x91, 0x98, 0x9f,
			0x8a, 0x8d, 0x84, 0x83, 0xde, 0xd9, 0xd0, 0xd7, 0xc2, 0xc5, 0xcc,
			0xcb, 0xe6, 0xe1, 0xe8, 0xef, 0xfa, 0xfd, 0xf4, 0xf3 };

	enum RxStateType {
		STATE_SYNC, STATE_TYPE, STATE_SIZE, STATE_OBJID, STATE_INSTID, STATE_DATA, STATE_CS, STATE_ERROR, STATE_COMPLETE
	};

	static final int TYPE_MASK = 0xF8;
	static final int TYPE_VER = 0x20;
	//! Packet contains an object
	static final int TYPE_OBJ = (TYPE_VER | 0x00);
	//! Packet is a request for an object
	static final int TYPE_OBJ_REQ = (TYPE_VER | 0x01);
	//! Packet is an object with a request for an ack
	static final int TYPE_OBJ_ACK = (TYPE_VER | 0x02);
	//! Packet is an ack for an object
	static final int TYPE_ACK = (TYPE_VER | 0x03);
	static final int TYPE_NACK = (TYPE_VER | 0x04);

	static final int HEADER_LENGTH = 10; // sync(1), type (1), size(2),
												// object ID (4), instance ID(2)

	static final int CHECKSUM_LENGTH = 1;

	static final int MAX_PAYLOAD_LENGTH = 256;

	static final int MAX_PACKET_LENGTH = (HEADER_LENGTH	+ MAX_PAYLOAD_LENGTH + CHECKSUM_LENGTH);

	static final int ALL_INSTANCES = 0xFFFF;
	static final int TX_BUFFER_SIZE = 2 * 1024;

	/**
	 * Private data
	 */
	InputStream inStream;
	OutputStream outStream;
	UAVObjectManager objMngr;

	static class Transaction {
        public int respType;
        public long respObjId;
        public long respInstId;
	}
		 
	Map<Long, Map<Long, Transaction>> transMap = new HashMap<Long, Map<Long, Transaction>>(); 
	
	// Variables used by the receive state machine
	ByteBuffer rxTmpBuffer /* 4 */;
	ByteBuffer rxBuffer;
	int rxType;
	long rxObjId;
	long rxInstId;
	int rxLength;
	int rxPacketLength;

	int rxCSPacket, rxCS;
	int rxCount;
	int packetSize;
	RxStateType rxState;
	ComStats stats = new ComStats();
	
	//! Currently only one UAVTalk transaction is permitted at a time.  If this is null none are in process
	//! otherwise points to the pending object
	UAVObject respObj;

	/**
	 * Comm stats
	 */
	public class ComStats {
		public int txBytes = 0;
        public int txObjectBytes = 0;
        public int txObjects = 0;
        public int txErrors = 0;
		public int rxBytes = 0;
		public int rxObjectBytes = 0;
		public int rxObjects = 0;
		public int rxErrors = 0;
	}
	
	public static String toHex(long l) {
        return String.format("%08X", l);
	}

	/**
	 * Constructor
	 */
	public UAVTalk(InputStream inStream, OutputStream outStream,
			UAVObjectManager objMngr) {
		this.objMngr = objMngr;
		this.inStream = inStream;
		this.outStream = outStream;

		rxState = RxStateType.STATE_SYNC;
		rxPacketLength = 0;

		// mutex = new QMutex(QMutex::Recursive);

		resetStats();
		rxTmpBuffer = ByteBuffer.allocate(4);
		rxTmpBuffer.order(ByteOrder.LITTLE_ENDIAN);
		rxBuffer = ByteBuffer.allocate(MAX_PAYLOAD_LENGTH);
		rxBuffer.order(ByteOrder.LITTLE_ENDIAN);

		// TOOD: Callback connect(io, SIGNAL(readyRead()), this,
		// SLOT(processInputStream()));
	}

	/**
	 * Reset the statistics counters
	 */
	public void resetStats() {
		// QMutexLocker locker(mutex);
		stats = new ComStats();
	}

	/**
	 * Get the statistics counters
	 */
	public ComStats getStats() {
		// QMutexLocker locker(mutex);
		return stats;
	}

	/**
	 * Send the specified object through the telemetry link. \param[in] obj
	 * Object to send \param[in] acked Selects if an ack is required \param[in]
	 * allInstances If set true then all instances will be updated \return
	 * Success (true), Failure (false)
	 * @throws IOException
	 */
	public boolean sendObject(UAVObject obj, boolean acked, boolean allInstances) throws IOException {
	    long instId = 0;

	    if (allInstances) {
	        instId = ALL_INSTANCES;
	    } else if (obj != null) {
	        instId = obj.getInstID();
	    }
	    boolean success = false;
		if (acked) {
			success = objectTransaction(TYPE_OBJ_ACK, obj.getObjID(), instId, obj);
		} else {
			success = objectTransaction(TYPE_OBJ, obj.getObjID(), instId, obj);
		}
		return success;
	}

	/**
	 * Request an update for the specified object, on success the object data
	 * would have been updated by the GCS. \param[in] obj Object to update
	 * \param[in] allInstances If set true then all instances will be updated
	 * \return Success (true), Failure (false)
	 * @throws IOException
	 */
	public boolean sendObjectRequest(UAVObject obj, boolean allInstances) throws IOException {
	    long instId = 0;

	    if (allInstances) {
	        instId = ALL_INSTANCES;
	    } else if (obj != null) {
	        instId = obj.getInstID();
	    }
		return objectTransaction(TYPE_OBJ_REQ, obj.getObjID(), instId, obj);
	}

	/**
	 * UAVTalk takes care of it's own transactions but if the caller knows
	 * it wants to give up on one (after a timeout) then it can cancel it
	 * @return True if that object was pending, False otherwise
	 */
	public boolean cancelPendingTransaction(UAVObject obj) {
		synchronized (transMap) {
			Transaction trans = findTransaction(obj.getObjID(), obj.getInstID()); 
			if (trans != null) {
				closeTransaction(trans);
				if (transactionListener != null) {
					Log.d(TAG,"Canceling transaction: " + toHex(obj.getObjID()) + " " + obj.getName());
					transactionListener.TransactionFailed(obj);
				}
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * Execute the requested transaction on an object. \param[in] obj Object
	 * \param[in] type Transaction type TYPE_OBJ: send object, TYPE_OBJ_REQ:
	 * request object update TYPE_OBJ_ACK: send object with an ack \param[in]
	 * allInstances If set true then all instances will be updated \return
	 * Success (true), Failure (false)
	 * @throws IOException
	 */
	private boolean objectTransaction(int type, long objId, long instId, UAVObject obj) throws IOException {
		if (type == TYPE_OBJ_ACK || type == TYPE_OBJ_REQ) {
			if (transmitObject(type, objId, instId, obj)) {
	            openTransaction(type, objId, instId);
    	        return true;
			}
			else {
				return false;
			}
		} else if (type == TYPE_OBJ) {
			return transmitObject(type, objId, instId, obj);
		} else {
			return false;
		}
	}

	/**
	 * Process any data in the queue
	 * @throws IOException
	 */
	public boolean processInputStream() throws IOException {
		int val;

		//inStream.wait();
		val = inStream.read();

		if (VERBOSE) Log.v(TAG, "Read: " + val);

		if (val == -1) {
			return false;
		}

		processInputByte(val);
		if (rxState == RxStateType.STATE_COMPLETE) {
			synchronized(rxState) {
				rxBuffer.position(0);
				receiveObject(rxType, rxObjId, rxInstId, rxBuffer);
				stats.rxObjectBytes += rxLength;
				stats.rxObjects++;
			}
		}
		return true;
	}

	/**
	 * Process an byte from the telemetry stream. \param[in] rxbyte Received
	 * byte \return Success (true), Failure (false)
	 * @throws IOException
	 */
	public boolean processInputByte(int rxbyte) throws IOException {
		Assert.assertNotNull(objMngr);

	    if (rxState == RxStateType.STATE_COMPLETE || rxState == RxStateType.STATE_ERROR) {
	        rxState = RxStateType.STATE_SYNC;
	    }

		// Only need to synchronize this method on the state machine state
		// Update stats
		stats.rxBytes++;

 		// update packet byte count
		rxPacketLength++;

		// Receive state machine
		switch (rxState) {
		case STATE_SYNC:

        	if (rxbyte != SYNC_VAL) {
		        // continue until sync byte is matched
    	    	//stats.rxSyncErrors++;
        		break;
	        }

			// Initialize and update CRC
			rxCS = updateCRC(0, rxbyte);

			rxPacketLength = 1;

	        // case local byte counter, don't forget to zero it after use.
    		rxCount = 0;

			rxState = RxStateType.STATE_TYPE;
			break;

		case STATE_TYPE:

			// Update CRC
			rxCS = updateCRC(rxCS, rxbyte);

			if ((rxbyte & TYPE_MASK) != TYPE_VER) {
				if (ERROR) Log.e(TAG, "Unknown UAVTalk type:" + rxbyte);
	            //stats.rxErrors++;
				rxState = RxStateType.STATE_SYNC;
				break;
			}

			rxType = rxbyte;

			if (VERBOSE) Log.v(TAG, "Received packet type: " + rxType);
			packetSize = 0;

			rxState = RxStateType.STATE_SIZE;
			break;

		case STATE_SIZE:

			// Update CRC
			rxCS = updateCRC(rxCS, rxbyte);

			if (rxCount == 0) {
				packetSize += rxbyte;
				rxCount++;
				break;
			}
			packetSize += (rxbyte << 8) & 0xff00;
	        rxCount     = 0;

			if (packetSize < HEADER_LENGTH
					|| packetSize > HEADER_LENGTH + MAX_PAYLOAD_LENGTH) {
				// incorrect packet size
				//stats.rxErrors++;
				rxState = RxStateType.STATE_SYNC;
				break;
			}

			rxState = RxStateType.STATE_OBJID;
			rxTmpBuffer.position(0);
			break;

		case STATE_OBJID:

			// Update CRC
			rxCS = updateCRC(rxCS, rxbyte);

			rxTmpBuffer.put(rxCount++, (byte) (rxbyte & 0xff));
			if (rxCount < 4) {
				break;
			}
	        rxCount     = 0;

			// Search for object, if not found reset state machine
			rxObjId = rxTmpBuffer.getInt(0);
			// Because java treats ints as only signed we need to do this manually
			if (rxObjId < 0) {
				rxObjId = 0x100000000l + rxObjId;
			}
			
	        // Message always contain an instance ID
			rxInstId = 0;
			rxState = RxStateType.STATE_INSTID;

			break;

		case STATE_INSTID:

			// Update CRC
			rxCS = updateCRC(rxCS, rxbyte);

			rxTmpBuffer.put(rxCount++, (byte) (rxbyte & 0xff));
			if (rxCount < 2) {
				break;
			}
			rxCount = 0;


			rxInstId = rxTmpBuffer.getShort(0);

	        // Search for object, if not found reset state machine
			{
				UAVObject rxObj = objMngr.getObject(rxObjId);
				if (rxObj == null) {
					if (WARN) Log.w(TAG, "Unknown ID: " + toHex(rxObjId));
					stats.rxErrors++;
					rxState = RxStateType.STATE_ERROR;
					break;
				}

				// Determine data length
				if (rxType == TYPE_OBJ_REQ || rxType == TYPE_ACK || rxType == TYPE_NACK) {
					rxLength = 0;
				} else {
	                if (rxObj != null) {
						rxLength = rxObj.getNumBytes();
					} else {
						rxLength = packetSize - rxPacketLength;
					}
				}
				
				// Check length
				if (rxLength >= MAX_PAYLOAD_LENGTH) {
					if (WARN) Log.w(TAG, "Greater than max payload length");
					stats.rxErrors++;
					rxState = RxStateType.STATE_ERROR;
					break;
				}

        		// Check the lengths match
        		if ((rxPacketLength + rxLength) != packetSize) {
            		// packet error - mismatched packet size
            		if (WARN) Log.w(TAG, "Mismatched packet size");
            		stats.rxErrors++;
            		rxState = RxStateType.STATE_ERROR;
            		break;
        		}
			}

			// If there is a payload get it, otherwise receive checksum
			if (rxLength > 0) {
				rxState = RxStateType.STATE_DATA;
			}
			else {
				rxState = RxStateType.STATE_CS;
			}
			break;

		case STATE_DATA:

			// Update CRC
			rxCS = updateCRC(rxCS, rxbyte);

			rxBuffer.put(rxCount++, (byte) (rxbyte & 0xff));
			if (rxCount < rxLength) {
				break;
			}
			rxCount = 0;

			rxState = RxStateType.STATE_CS;
			break;

		case STATE_CS:

			// The CRC byte
			rxCSPacket = rxbyte;

			if (rxCS != rxCSPacket) { // packet error - faulty CRC
				if (WARN) Log.w(TAG,"Bad crc");
				stats.rxErrors++;
				rxState = RxStateType.STATE_SYNC;
				break;
			}

			if (rxPacketLength != (packetSize + 1)) { // packet error -
				// mismatched packet
				// size
				if (WARN) Log.w(TAG,"Bad size");
				stats.rxErrors++;
				rxState = RxStateType.STATE_SYNC;
				break;
			}

			rxState = RxStateType.STATE_COMPLETE;
			break;

		default:
			if (WARN) Log.w(TAG, "Bad state");
			rxState = RxStateType.STATE_ERROR;
			stats.rxErrors++;
		}

		// Done
		return true;
	}

	/**
	 * Receive an object. This function process objects received through the
	 * telemetry stream. \param[in] type Type of received message (TYPE_OBJ,
	 * TYPE_OBJ_REQ, TYPE_OBJ_ACK, TYPE_ACK) \param[in] obj Handle of the
	 * received object \param[in] instId The instance ID of UAVOBJ_ALL_INSTANCES
	 * for all instances. \param[in] data Data buffer \param[in] length Buffer
	 * length \return Success (true), Failure (false)
	 * @throws IOException
	 */
	public boolean receiveObject(int type, long objId, long instId, ByteBuffer data) throws IOException {

		if (DEBUG) Log.d(TAG, "Received object : " + toHex(objId));
		assert (objMngr != null);

		UAVObject obj = null;
		boolean error = false;
		boolean allInstances = (instId == ALL_INSTANCES ? true : false);

		// Process message type
		switch (type) {
		case TYPE_OBJ:
			// All instances, not allowed for OBJ messages
			if (!allInstances) {
				if (DEBUG) Log.d(TAG, "Received object: " + objMngr.getObject(objId).getName());

				// Get object and update its data
				obj = updateObject(objId, instId, data);

				if (obj != null) {
                	// Check if this object acks a pending OBJ_REQ message
                	// any OBJ message can ack a pending OBJ_REQ message
                	// even one that was not sent in response to the OBJ_REQ message
	                updateAck(type, objId, instId, obj);
				} else {
					error = true;
				}
			} else {
				error = true;
			}
			break;
		
		case TYPE_OBJ_ACK:
			// All instances, not allowed for OBJ_ACK messages
			if (!allInstances) {
				 if (DEBUG) Log.d(TAG,"Received object ack: " + objMngr.getObject(objId).getName());
				// Get object and update its data
				obj = updateObject(objId, instId, data);
				// Transmit ACK
				if (obj != null) {
					error = !transmitObject(TYPE_ACK, objId, instId,obj);
				} else {
					error = true;
				}
			} else {
				error = true;
			}
			break;
		
		case TYPE_OBJ_REQ:
			// Get object, if all instances are requested get instance 0 of the object
			if (DEBUG) Log.d(TAG,"Received object request: " + objMngr.getObject(objId).getName());
			if (allInstances) {
				obj = objMngr.getObject(objId);
			} else {
				obj = objMngr.getObject(objId, instId);
			}
			// If object was found transmit it
			if (obj != null) {
				error = !transmitObject(TYPE_OBJ, objId, instId, obj);
			} else {
				error = true;
			}
	        if (error) {
    	        // failed to send object, transmit NACK
        	    transmitObject(TYPE_NACK, objId, instId, null);
        	}
			break;
	    
		case TYPE_ACK:
			// All instances, not allowed for ACK messages
			if (!allInstances) {
				if (DEBUG) Log.d(TAG,"Received ack: " + objMngr.getObject(objId).getName());
				// Get object
				obj = objMngr.getObject(objId, instId);
				// Check if an ack is pending
				if (obj != null) {
					updateAck(type, objId, instId, obj);
				} else {
					error = true;
				}
			}
			break;

	    case TYPE_NACK:
        	if (DEBUG) Log.d(TAG, "Received nak: " + objMngr.getObject(objId).getName());
        	// All instances, not allowed for NACK messages
	        if (!allInstances) {
	            // Get object
	            obj = objMngr.getObject(objId, instId);
	            // Check if object exists:
	            if (obj != null) {
	              	// Check if a NACK is pending
                	updateNack(objId, instId, obj);
	            } else {
	             	error = true;
	            }
	        }
	        break;
		
		default:
			error = true;
		}
		// Done
		return !error;
	}

	/**
	 * Update the data of an object from a byte array (unpack). If the object
	 * instance could not be found in the list, then a new one is created.
	 */
	public synchronized UAVObject updateObject(long objId, long instId, ByteBuffer data) {
		assert (objMngr != null);

		// Get object
		UAVObject obj = objMngr.getObject(objId, instId);

		// If the instance does not exist create it
		if (obj == null) {
			// Get the object type
			UAVObject tobj = objMngr.getObject(objId);
			if (tobj == null) {
				// TODO: Return a NAK since we don't know this object
				return null;
			}
			// Make sure this is a data object
			UAVDataObject dobj = null;
			try {
				dobj = (UAVDataObject) tobj;
			} catch (Exception e) {
				// Failed to cast to a data object
				return null;
			}

			// Create a new instance, unpack and register
			UAVDataObject instobj = dobj.clone(instId);
			try {
				if (!objMngr.registerObject(instobj)) {
					return null;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (DEBUG) Log.d(TAG, "Unpacking new object");
			instobj.unpack(data);
			return instobj;
		} else {
			// Unpack data into object instance
			if (DEBUG) Log.d(TAG, "Unpacking existing object: " + obj.getName());
			obj.unpack(data);
			return obj;
		}
	}

	/**
	 * Check if a transaction is pending that this acked object corresponds to
	 * and if yes complete it.
	 */
	private synchronized void updateAck(int type, long objId, long instId, UAVObject obj) {
		Assert.assertNotNull(obj);
	    Transaction trans = findTransaction(objId, instId);
	    if (trans != null && trans.respType == type) {
            if (DEBUG) Log.d(TAG, "Transaction acked: " + obj.getName());
        	if (trans.respInstId == ALL_INSTANCES) {
            	if (instId == 0) {
	                // last instance received, complete transaction
    	            closeTransaction(trans);
					// Notify listener
					if (transactionListener != null) {
						transactionListener.TransactionSucceeded(obj);
					}
				} else {
	                // TODO extend timeout?
				}
			}
			else {
	            closeTransaction(trans);
				// Notify listener
				if (transactionListener != null) {
					transactionListener.TransactionSucceeded(obj);
				}
			}
		}
	}


	/**
	 * Called when an object is received to check if this completes
	 * a UAVTalk transaction
	 */
	private void updateNack(long objId, long instId, UAVObject obj) {
		// Check if this is not a possible candidate
		Assert.assertNotNull(obj);

		boolean succeeded = false;

		// The lock on UAVTalk must be release before the transaction succeeded signal is sent
		// because otherwise if a transaction timeout occurs at the same time we can get a
		// deadlock:
		// 1. processInputStream -> updateObjReq (locks uavtalk) -> tranactionCompleted (locks transInfo)
		// 2. transactionTimeout (locks transInfo) -> sendObjectRequest -> ? -> setupTransaction (locks uavtalk)
		synchronized(this) {
		    Transaction trans = findTransaction(objId, instId);
    		if (trans != null) {
                if (DEBUG) Log.d(TAG, "Transaction nacked: " + obj.getName());
        		closeTransaction(trans);
				succeeded = true;
		    }
		}

		// Notify listener
		if (succeeded && transactionListener != null) {
			transactionListener.TransactionSucceeded(obj);
		}
	}

	/**
	 * Send an object through the telemetry link.
	 * @param[in] obj Object to send
	 * @param[in] type Transaction type
	 * @param[in] allInstances True is all instances of the object are to be sent
	 * @return Success (true), Failure (false)
	 * @throws IOException
	 */
	private boolean transmitObject(int type, long objId, long instId, UAVObject obj) throws IOException {
	    // Important note : obj can be null (when type is NACK for example) so protect all obj dereferences.

	    // If all instances are requested on a single instance object it is an error
    	if ((obj != null) && (instId == ALL_INSTANCES) && obj.isSingleInstance()) {
        	instId = 0;
    	}
    	boolean allInstances = (instId == ALL_INSTANCES);

        if (DEBUG) Log.d(TAG, "Transmitting " + getTypeString(type) + " " + toHex(objId) + " " + instId + " " + (obj != null ? obj.toStringBrief() : ""));

		// Process message type
		boolean ret = false;
		if (type == TYPE_OBJ || type == TYPE_OBJ_ACK) {
			if (allInstances) {
    	        // Send all instances in reverse order
	            // This allows the receiver to detect when the last object has been received (i.e. when instance 0 is received)
	            ret = true;
				int numInst = objMngr.getNumInstances(obj.getObjID());
				for (int n = 0; n < numInst; ++n) {
                	int i = numInst - n - 1;
					// TODO: This code is buggy probably.  We should send each request
					// and wait for an ack in the case of an TYPE_OBJ_ACK
					Assert.assertNotSame(type, TYPE_OBJ_ACK); // catch any buggy calls

					UAVObject o = objMngr.getObject(obj.getObjID(), i);
					if (!transmitSingleObject(type, objId, i, o)) {
						ret = false;
						break;
					}
				}
			} else {
				ret = transmitSingleObject(type, objId, instId, obj);
			}
		} else if (type == TYPE_OBJ_REQ) {
			ret = transmitSingleObject(TYPE_OBJ_REQ, objId, instId, obj);
		} else if (type == TYPE_ACK) {
			if (!allInstances) {
				ret = transmitSingleObject(TYPE_ACK, objId, instId, obj);
			}
		}
		
		if (!ret) {
            Log.e(TAG, "Failed transmitting " + getTypeString(type) + " " + toHex(objId) + " " + instId + " " + (obj != null ? obj.getName() : ""));
		}
		
		return ret;
	}

	/**
	 * Send an object through the telemetry link.
	 * @throws IOException
	 * @param[in] obj Object handle to send
	 * @param[in] type Transaction type \return Success (true), Failure (false)
	 */
	private boolean transmitSingleObject(int type, long objId, long instId, UAVObject obj) throws IOException {
		int length = 0;

		assert (objMngr != null && outStream != null);

	    // IMPORTANT : obj can be null (when type is NACK for example)
	
		ByteBuffer bbuf = ByteBuffer.allocate(MAX_PACKET_LENGTH);
		bbuf.order(ByteOrder.LITTLE_ENDIAN);

		// Setup type and object id fields
		bbuf.put((byte) (SYNC_VAL & 0xff));
		bbuf.put((byte) (type & 0xff));
		bbuf.putShort((short) (length + HEADER_LENGTH));
		bbuf.putInt((int) objId);
		bbuf.putShort((short) (instId & 0xffff));
	
		// Determine data length
		if (type == TYPE_OBJ_REQ || type == TYPE_ACK || type == TYPE_NACK) {
			length = 0;
		} else {
			length = obj.getNumBytes();
		}

		// Check length
		if (length >= MAX_PAYLOAD_LENGTH) {
	        ++stats.txErrors;
			return false;
		}

		// Copy data (if any)
		if (length > 0)
			try {
				if (obj.pack(bbuf) == 0) {
			        ++stats.txErrors;
					return false;
				}
			} catch (Exception e) {
		        ++stats.txErrors;
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}

		// Calculate checksum
		bbuf.put((byte) (updateCRC(0, bbuf.array(), bbuf.position()) & 0xff));

		int packlen = bbuf.position();
		bbuf.position(0);
		byte[] dst = new byte[packlen];
		bbuf.get(dst, 0, packlen);

		outStream.write(dst);

		// Update stats
		++stats.txObjects;
		stats.txBytes += bbuf.position();
		stats.txObjectBytes += length;

		// Done
		return true;
	}

	private Transaction findTransaction(long objId, long instId) {
	    // Lookup the transaction in the transaction map
	    Map<Long, Transaction> objTransactions = transMap.get(objId);
	    if (objTransactions != null) {
	        Transaction trans = objTransactions.get(instId);
	        if (trans == null) {
	            // see if there is an ALL_INSTANCES transaction
	            trans = objTransactions.get(ALL_INSTANCES);
	        }
	        return trans;
	    }
	    return null;
	}
	
	private synchronized void openTransaction(int type, long objId, long instId) {
		Transaction trans = new Transaction();
		
	    trans.respType = (type == TYPE_OBJ_REQ) ? TYPE_OBJ : TYPE_ACK;
	    trans.respObjId  = objId;
    	trans.respInstId = instId;

		Map<Long, Transaction> objTransactions = transMap.get(trans.respObjId);
		if (objTransactions == null) {
			objTransactions = new HashMap<Long, Transaction>();
			transMap.put(trans.respObjId, objTransactions); 
		}
		objTransactions.put(instId, trans);
	}

	private void closeTransaction(Transaction trans) {
	    Map<Long, Transaction> objTransactions = transMap.get(trans.respObjId);
	    if (objTransactions != null) {
	        objTransactions.remove(trans.respInstId);
	        // Keep the map even if it is empty
	        // There are at most 100 different object IDs...
	    }
	}
	
	private void closeAllTransactions() {
/*	
	    foreach(quint32 objId, transMap.keys()) {
	        QMap<quint32, Transaction *> *objTransactions = transMap.value(objId);
	        foreach(quint32 instId, objTransactions->keys()) {
	            Transaction *trans = objTransactions->value(instId);
	
	            qWarning() << "UAVTalk - closing active transaction for object" << trans->respObjId;
	            objTransactions->remove(instId);
	            delete trans;
	        }
	        transMap.remove(objId);
	        delete objTransactions;
	    }
*/	    
	}

	/**
	 * Update the crc value with new data.
	 *
	 * Generated by pycrc v0.7.5, http://www.tty1.net/pycrc/ using the
	 * configuration: Width = 8 Poly = 0x07 XorIn = 0x00 ReflectIn = False
	 * XorOut = 0x00 ReflectOut = False Algorithm = table-driven
	 *
	 * \param crc The current crc value. \param data Pointer to a buffer of \a
	 * data_len bytes. \param length Number of bytes in the \a data buffer.
	 * \return The updated crc value.
	 */
	int updateCRC(int crc, int data) {
		return crc_table[crc ^ (data & 0xff)];
	}

	int updateCRC(int crc, byte[] data, int length) {
		for (int i = 0; i < length; i++)
			crc = updateCRC(crc, data[i]);
		return crc;
	}

	private OnTransactionCompletedListener transactionListener = null;

	abstract class OnTransactionCompletedListener {
    	abstract void TransactionSucceeded(UAVObject data);
    	abstract void TransactionFailed(UAVObject data);
    };

    void setOnTransactionCompletedListener(OnTransactionCompletedListener onTransactionListener) {
    	this.transactionListener = onTransactionListener;
    }


    public final static String getTypeString(int type) {
        switch(type) {
            case TYPE_OBJ:
                return "object";
            case TYPE_OBJ_ACK:
                return "object (acked)";
            case TYPE_OBJ_REQ:
                return "object request";
            case TYPE_ACK:
                return "ack";
            case TYPE_NACK:
                return "nack";
        }           
        return "unknown type";
    }

}
