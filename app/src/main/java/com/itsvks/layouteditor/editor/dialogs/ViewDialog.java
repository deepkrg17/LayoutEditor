package com.itsvks.layouteditor.editor.dialogs;

import android.content.Context;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import com.itsvks.layouteditor.managers.IdManager;

public class ViewDialog extends AttributeDialog {
	
	private ArrayList<String> ids = new ArrayList<>();
	private ListView listview;
	
	private String constant;
	
	public ViewDialog(Context context, String savedValue, String constant) {
		super(context);
		this.constant = constant;
		
		ids.addAll(IdManager.getIds());
		
		if(constant != null) {
			ids.add(0, constant);
		}
		
		listview = new ListView(context);
		listview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		listview.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, ids));
		listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		listview.setDivider(null);

		if(!savedValue.equals("")) {
			listview.setItemChecked(ids.indexOf(savedValue.replace("@id/", "")), true);
		}
		
		setView(listview, 0, 20, 0, 0);
	}
	
	@Override
	protected void onClickSave() {
	    super.onClickSave();
		
		if(listview.getCheckedItemPosition() == -1) {
			listener.onSave("-1");
		}
		else {
		//	listener.onSave("@id/" + ids.get(listview.getCheckedItemPosition()));
			if(constant == null) {
				listener.onSave("@id/" + ids.get(listview.getCheckedItemPosition()));
				return;
			}
			
			if(listview.getCheckedItemPosition() > 0) {
				listener.onSave("@id/" + ids.get(listview.getCheckedItemPosition()));
			}
			else {
				listener.onSave(constant);
			}
		}
	}
}