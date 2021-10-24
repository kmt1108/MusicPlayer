package com.kmt.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class SongFolderAdapter extends BaseExpandableListAdapter {
    final int grouplayout=R.layout.exp_listsong_group;
    final int childlayout=R.layout.exp_listsong_child;
    HashMap<String, ArrayList<SongDetails>> listSong;
    String[] keySet;
    Context context;

    public SongFolderAdapter(HashMap<String, ArrayList<SongDetails>> listSong, Context context) {
        this.listSong = listSong;
        this.context = context;
        Set<String> keys=listSong.keySet();
        keySet=keys.toArray(new String[keys.size()]);
    }

    @Override
    public int getGroupCount() {
        return listSong.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listSong.get(keySet[groupPosition]).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listSong.get(keySet[groupPosition]);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listSong.get(keySet[groupPosition]).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(grouplayout,null);
        TextView tv=convertView.findViewById(R.id.txtTitleFolder);
        tv.setText(keySet[groupPosition]);
        tv.setSelected(true);
        if (isExpanded)
            convertView.setBackgroundResource(R.color.group_expand);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater  inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(childlayout,null);
        TextView tv=convertView.findViewById(R.id.txtTitleSong);
        String fileName=((SongDetails)getChild(groupPosition,childPosition)).getmTitle();
        tv.setText(fileName);
        tv.setSelected(true);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
