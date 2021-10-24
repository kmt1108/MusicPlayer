package com.kmt.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ExpandableListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ActivityListSong extends AppCompatActivity {
    ArrayList<SongDetails> arrAllsongs;
    ListSongAdapter adapter;
    RecyclerView listSongView;
    SQLiteAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song);
        requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},23);
        initView();
        setAdapterListSong();

    }

    private void initView() {
        listSongView=findViewById(R.id.listAllsong);
        dbAdapter=new SQLiteAdapter(this);
        arrAllsongs=dbAdapter.getAllSong();
    }

    private void setAdapterListSong() {
        adapter=new ListSongAdapter(this,arrAllsongs);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        listSongView.setLayoutManager(linearLayoutManager);
        listSongView.setAdapter(adapter);
    }
    /*private HashMap<String, ArrayList<HashMap<String, String>>> getListSong(String filePath) {
        HashMap<String, ArrayList<HashMap<String, String>>> listSong = new HashMap<>();
        try {
            File rootfolder = new File(filePath);
            File[] files = rootfolder.listFiles();
            ArrayList<HashMap<String, String>> songs = new ArrayList<>();
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getListSong(file.getAbsolutePath()) != null) {
                        listSong.putAll(getListSong(file.getAbsolutePath()));
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    HashMap<String, String> song = new HashMap<>();
                    song.put("file_path", file.getAbsolutePath());
                    song.put("file_name", file.getName());
                    songs.add(song);
                }
            }
            if (songs.size() > 0) {
                if (listSong==null)
                    listSong=new HashMap<>();
                listSong.put(rootfolder.getAbsolutePath(), songs);

            }else
                listSong=null;
        } catch (Exception e) {
            return null;
        }
        return listSong;
    }*/


}