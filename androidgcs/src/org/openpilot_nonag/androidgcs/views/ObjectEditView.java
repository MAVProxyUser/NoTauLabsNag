package org.openpilot_nonag.androidgcs.views;

import java.util.ArrayList;
import java.util.List;

import org.openpilot_nonag.androidgcs.util.SmartSave;
import org.openpilot_nonag.uavtalk.UAVObjectField;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class ObjectEditView extends LinearLayout {

	private static String TAG = ObjectEditView.class.getSimpleName();
	String objectName;
	public List<View> fields;
	private SmartSave smartSave;

	public ObjectEditView(Context context) {
		super(context);
		initObjectEditView();
	}

	public ObjectEditView(Context context, AttributeSet ats, int defaultStyle) {
		super(context, ats);
		initObjectEditView();
	}

	public ObjectEditView(Context context, AttributeSet ats) {
		super(context, ats);
		initObjectEditView();
	}

	public void setSmartSave(SmartSave s) {
		smartSave = s;
	}

	public void initObjectEditView() {
		// Set orientation of layout to vertical
		setOrientation(LinearLayout.VERTICAL);
		fields = new ArrayList<View>();
	}

	public void setName(String name) {
		objectName = name;
	}

	public void addField(UAVObjectField field) {
		for (int i = 0; i < field.getNumElements(); i++)
			addRow(getContext(), field, i);
	}


	public void addRow(Context context, UAVObjectField field, int idx) {

		Log.d(TAG,"field = " + field.getTypeAsString());
		View fieldValue = null;
		switch(field.getType())
		{
		case FLOAT32:
			fieldValue = new NumericalFieldView(context, null, field, idx);
			((NumericalFieldView)fieldValue).setValue(Double.parseDouble(field.getValue(idx).toString()));
			((NumericalFieldView)fieldValue).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL);
			if (smartSave != null)
				smartSave.addControlMapping((NumericalFieldView)fieldValue, field.getName(), idx);
			break;
		case INT8:
		case INT16:
		case INT32:
			fieldValue = new NumericalFieldView(context, null, field, idx);
			((NumericalFieldView)fieldValue).setValue(Double.parseDouble(field.getValue(idx).toString()));
			((NumericalFieldView)fieldValue).setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
			if (smartSave != null)
				smartSave.addControlMapping((NumericalFieldView)fieldValue, field.getName(), idx);
			break;
		case UINT8:
		case UINT16:
		case UINT32:
			fieldValue = new NumericalFieldView(context, null, field, idx);
			((NumericalFieldView)fieldValue).setValue(Double.parseDouble(field.getValue(idx).toString()));
			((NumericalFieldView)fieldValue).setInputType(InputType.TYPE_CLASS_NUMBER);
			if (smartSave != null)
				smartSave.addControlMapping((NumericalFieldView)fieldValue, field.getName(), idx);
			break;
		case ENUM:
			fieldValue = new EnumFieldView(context, null, field, idx);
			if (smartSave != null)
				smartSave.addControlMapping((EnumFieldView)fieldValue, field.getName(), idx);
			break;
		case BITFIELD:
			fieldValue = new TextView(context);
			((TextView)fieldValue).setText("Unsupported type: Bitfield");
			break;
		case STRING:
			fieldValue = new TextView(context);
			((TextView)fieldValue).setText("Unsupported type: Bitfield");
		}

		addView(fieldValue);
		fields.add(fieldValue);
	}

}