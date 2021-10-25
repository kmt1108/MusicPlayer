package com.kmt.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static com.kmt.musicplayer.PlayerServices.ACTION_CONTROL_PLAYER;
import static com.kmt.musicplayer.PlayerServices.ACTION_START;

public class ListSongAdapter extends RecyclerView.Adapter<ListSongAdapter.MyViewHolder> {
    final int layout=R.layout.exp_listsong_child;
    Context context;
    ArrayList<SongDetails> arrListSong;

    public ListSongAdapter(Context context, ArrayList<SongDetails> arrListSong) {
        this.context = context;
        this.arrListSong = arrListSong;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);
        view.setPadding(0,0,0,0);
        MyViewHolder vh=new MyViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SongDetails song=arrListSong.get(position);
        holder.songTitle.setText(song.getmTitle());
        holder.songTitle.setSelected(true);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ActivityPlayer.class);
                intent.setAction(ACTION_CONTROL_PLAYER);
                /*intent.putExtra("path",song.getmPath());
                context.startActivity(intent);*/
                intent.putExtra(ACTION_CONTROL_PLAYER,ACTION_START);
                intent.putExtra("song",song);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrListSong.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView songTitle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle=itemView.findViewById(R.id.txtTitleSong);
        }
    }


}
