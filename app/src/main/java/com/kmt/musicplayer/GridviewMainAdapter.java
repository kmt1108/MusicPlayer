package com.kmt.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridviewMainAdapter extends BaseAdapter {
    Context context;
    String []arrImg;
    String[]arrTitle;
    String packageName;

    public GridviewMainAdapter(Context context, String[] arrImg, String[] arrTitle) {
        this.context = context;
        this.arrImg = arrImg;
        this.arrTitle = arrTitle;
        packageName=context.getPackageName();
    }

    @Override
    public int getCount() {
        return arrImg.length<=arrTitle.length?arrImg.length:arrTitle.length;
    }

    @Override
    public Object getItem(int position) {
        return arrImg[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView=inflater.inflate(R.layout.gridview_item,null);
        ImageView imageView=convertView.findViewById(R.id.imageView);
        TextView textView=convertView.findViewById(R.id.tvTitle);
        imageView.setImageResource(context.getResources().getIdentifier(arrImg[position],"drawable",packageName));
        textView.setText(arrTitle[position]);
        return convertView;
    }
}
