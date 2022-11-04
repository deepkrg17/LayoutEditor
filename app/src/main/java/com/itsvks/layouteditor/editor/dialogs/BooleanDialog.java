package com.itsvks.layouteditor.editor.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class BooleanDialog extends AttributeDialog {
	
	private RadioGroup group;
	
    @SuppressLint("ResourceType")
	public BooleanDialog(Context context, String savedValue) {
		super(context);
		
		group = new RadioGroup(context);
		group.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		group.setOrientation(RadioGroup.VERTICAL);
		
		RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
		params.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());
		
		RadioButton rbTrue = new RadioButton(context);
		rbTrue.setLayoutParams(params);
		rbTrue.setText("true");
		rbTrue.setId(1);
		
		group.addView(rbTrue);
		
		RadioButton rbFalse = new RadioButton(context);
		rbFalse.setLayoutParams(params);
		rbFalse.setText("false");
		rbFalse.setId(0);
		
		group.addView(rbFalse);
		
		setView(group, 10, 20, 10, 0);
		
		if(!savedValue.equals("")) {
			if(savedValue.equals("true")) {
				rbTrue.setChecked(true);
			}
			else {
				rbFalse.setChecked(true);
			}
		}
	}
	
    @SuppressLint("ResourceType")
	@Override
	protected void onClickSave() {
	    super.onClickSave();
		
		String value = group.getCheckedRadioButtonId() == 1 ? "true" : "false";
		listener.onSave(value);
	}
}