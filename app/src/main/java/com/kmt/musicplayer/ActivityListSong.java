package com.kmt.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityListSong extends AppCompatActivity {
    ArrayList<SongDetails> arrAllsongs;
    ListSongAdapter adapter;
    RecyclerView listSongView;
    SQLiteAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_song);
        initView();
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            arrAllsongs=dbAdapter.getAllSong();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
        } else {
            // You can directly ask for the permission.
            requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },23);
        }


        setAdapterListSong();

    }

    private void initView() {
        listSongView=findViewById(R.id.listAllsong);
        dbAdapter=new SQLiteAdapter(this);
    }

    private void setAdapterListSong() {
        adapter=new ListSongAdapter(this,arrAllsongs);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        listSongView.setLayoutManager(linearLayoutManager);
        listSongView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==23&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            arrAllsongs=dbAdapter.getAllSong();
        }else{
            finish();
        }
    }
    private int getAllAudioFromDevices(){
        //List<SongDetails> list=new ArrayList<>();
        Uri collection;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q){
            collection= MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }else{
            collection=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }
        Toast.makeText(this, collection.toString(), Toast.LENGTH_SHORT).show();

        String[] projection=new String[]{
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.YEAR
        };
        String selection= MediaStore.Audio.Media.DATA+" like ?";
        String[] selectionAgrs=new String[]{"%.mp3%"};
        String sortOrder=MediaStore.Audio.Media.TITLE+" ASC";
        Cursor cursor=getContentResolver().query(
                collection,
                projection,
                selection,
                selectionAgrs,
                sortOrder);
        int dem=0;
        if (cursor!=null){
            while (cursor.moveToNext()){
                String path=cursor.getString(0);
                String title=cursor.getString(1);
                String album=cursor.getString(2);
                String artist=cursor.getString(3);
                int year=cursor.getInt(4);
                SongDetails song=new SongDetails(path,title,album,artist,year);
                dbAdapter.insertSongToListAll(song);
                dem++;
            }
        }
        return dem;

    }

    @Override
    protected void onStart() {
        super.onStart();
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