package com.kmt.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivitySongFolder extends AppCompatActivity {
    ExpandableListView lv;
    List<SongDetails> list;
    HashMap<String, ArrayList<SongDetails>> mapSong;
    SQLiteAdapter dbAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_folder);
        dbAdapter=new SQLiteAdapter(this);
        lv=findViewById(R.id.lv);
        list=new ArrayList<>();
        mapSong=new HashMap<>();
        //int a=getAllAudioFromDevices();
        list=dbAdapter.getAllSong();
        mapSong=dbAdapter.getAllSongWithFolder();
        //ArrayAdapter<SongDetails> adapter=new ArrayAdapter<SongDetails>(this,android.R.layout.simple_list_item_1,list);
        SongFolderAdapter adapter=new SongFolderAdapter(mapSong,this);
        lv.setAdapter(adapter);
    }
    private int getAllAudioFromDevices(){
        List<SongDetails> list=new ArrayList<>();
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
}