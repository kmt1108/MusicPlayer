package com.kmt.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

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
        holder.itemView.setOnClickListener(hd -> {
                Intent intent=new Intent(context,PlayerServices.class);
                intent.setAction(ACTION_CONTROL_PLAYER);
                /*intent.putExtra("path",song.getmPath());
                context.startActivity(intent);*/
                intent.putExtra(ACTION_CONTROL_PLAYER,ACTION_START);
                intent.putExtra("song",song);
                intent.putParcelableArrayListExtra("playlist",arrListSong);
                context.startService(intent);
                /*saveCurrentPlayList();
                context.startActivity(intent);*/
            }
        );
    }

    private void saveCurrentPlayList() {
        SharedPreferences sharedPreferences=context.getSharedPreferences(PlayerServices.CURRENT_SETTING,Context.MODE_PRIVATE);
        Gson gson=new Gson();
        JsonArray array=gson.toJsonTree(arrListSong).getAsJsonArray();
        sharedPreferences.edit().putString(PlayerServices.KEY_CURRENT_PLAYLIST,array.toString()).apply();
    }

    @Override
    public int getItemCount() {
        return arrListSong.size();
    }


    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView songTitle;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle=itemView.findViewById(R.id.txtTitleSong);
        }
    }


}
