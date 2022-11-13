package com.itsvks.layouteditor.editor.dialogs;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class EnumDialog extends AttributeDialog {
	
	private ListView listview;
	private List<String> arguments;
	
	public EnumDialog(Context context, String savedValue, ArrayList<String> arguments) {
		super(context);
		
		this.arguments = arguments;
		
		listview = new ListView(context);
		listview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		listview.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, arguments));
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listview.setDivider(null);
		
		if(!savedValue.equals("")) {
			listview.setItemChecked(arguments.indexOf(savedValue), true);
		}
		
		setView(listview, 0, 20, 0, 0);
	}
	
	@Override
	protected void onClickSave() {
		if(listview.getCheckedItemPosition() == -1) {
			listener.onSave("-1");
			return;
		}
		
		listener.onSave(arguments.get(listview.getCheckedItemPosition()));
	}
}