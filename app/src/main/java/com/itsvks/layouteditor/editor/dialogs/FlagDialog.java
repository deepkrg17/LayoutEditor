package com.itsvks.layouteditor.editor.dialogs;

import android.content.Context;

import android.util.SparseBooleanArray;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.itsvks.layouteditor.editor.dialogs.AttributeDialog;
import java.util.ArrayList;

public class FlagDialog extends AttributeDialog {
	
	private ListView listview;
	private ArrayList<String> arguments;
	
	public FlagDialog(Context context, String savedValue, ArrayList<String> arguments) {
		super(context);
		
		this.arguments = arguments;
		
		listview = new ListView(context);
		listview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listview.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_multiple_choice, arguments));
		listview.setDivider(null);
		
		if(!savedValue.equals("")) {
			String[] flags = savedValue.split("\\|");
			for(String flag : flags) {
				listview.setItemChecked(arguments.indexOf(flag), true);
			}
		}
		
		
		setView(listview, 0, 20, 0, 0);
	}
	
	@Override
	protected void onClickSave() {
	    super.onClickSave();
		
		if(listview.getCheckedItemCount() == 0) {
			listener.onSave("-1");
			return;
		}
		
		StringBuilder builder = new StringBuilder();
		SparseBooleanArray array = listview.getCheckedItemPositions();
		
		for(int i = 0; i < array.size(); i ++) {
			int checkedItem = array.keyAt(i);
					
			if(array.get(checkedItem)) {
				builder.append(arguments.get(checkedItem) + "|");
			}
		}
				
		String value = builder.toString();
		value = value.substring(0, value.lastIndexOf("|"));
				
		listener.onSave(value);
	}
}