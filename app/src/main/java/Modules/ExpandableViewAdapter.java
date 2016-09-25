package Modules;

/**
 * Created by ramitix on 8/15/16.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.ramitix.locations.R;

import java.util.ArrayList;

public class ExpandableViewAdapter extends BaseExpandableListAdapter {

    private Activity activity;
    private ArrayList<Object> childtems;
    private LayoutInflater inflater;
    private ArrayList<String> parentItems, child, status;
    private ArrayList<Object> checkBoxStates ;
    int checkedValue = 0;

    public ExpandableViewAdapter(ArrayList<String> parents, ArrayList<Object> childern, ArrayList<Object> childernChecked) {
        this.parentItems = parents;
        this.childtems = childern;
        this.checkBoxStates = childernChecked;
    }

    public void setInflater(LayoutInflater inflater, Activity activity) {
        this.inflater = inflater;
        this.activity = activity;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        child = (ArrayList<String>) childtems.get(groupPosition);
        status = (ArrayList<String>) checkBoxStates.get(groupPosition);



        if (convertView == null) {
            convertView = inflater.inflate( R.layout.listview_child, null);
        }

        final CheckBox textView = (CheckBox) convertView.findViewById(R.id.checkBox);
        textView.setText(child.get(childPosition));
        textView.setChecked( Boolean.parseBoolean( status.get(childPosition) ) );



        textView.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {

                        Toast.makeText( activity, child.get( childPosition ), Toast.LENGTH_SHORT ).show();

                        for(int i=0; i<child.size(); i++) {
                            if ((child.get( i )) != buttonView.getText().toString()) {  // assuming v is the View param passed in
                                status.set( i, "false" );
                            }else{
                                status.set( i, "true" );
                                checkedValue = i;
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });


        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_parent, null);
        }

        ((CheckedTextView) convertView).setText(parentItems.get(groupPosition));
        ((CheckedTextView) convertView).setChecked(isExpanded);

        return convertView;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((ArrayList<String>) childtems.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return parentItems.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public int getTimeValue(){

        return checkedValue;
    }

}