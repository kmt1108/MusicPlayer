package com.kmt.musicplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PropertyResourceBundle;

public class SQLiteAdapter {
    private dbHelper mHelper;

    public SQLiteAdapter(Context context) {
        this.mHelper = new dbHelper(context);
    }

    public HashMap<String,ArrayList<SongDetails>> getAllSongWithFolder(){
        HashMap<String,ArrayList<SongDetails>> mapSong=new HashMap<>();
        ArrayList<SongDetails> listSong=getAllSong();
        for (SongDetails song:listSong) {
            String path=song.getmPath();
            String folder=path.substring(0,path.lastIndexOf('/'));
            if (mapSong.containsKey(folder)){
                mapSong.get(folder).add(song);
            }else{
                ArrayList<SongDetails> lstSong=new ArrayList<>();
                lstSong.add(song);
                mapSong.put(folder,lstSong);
            }
        }
        return mapSong;

    }

    public ArrayList<SongDetails> getSongFromPlaylist(int playlistID){
        ArrayList<SongDetails> listSong=new ArrayList<>();
        SQLiteDatabase db=mHelper.getReadableDatabase();
        String sql="select ("+dbHelper.COL_PATH+","+dbHelper.COL_TITLE+","+dbHelper.COL_ALBUM+","+dbHelper.COL_ARTIST+","+dbHelper.COL_YEAR+")" +
                " FROM "+dbHelper.TABLE_ALLSONG+","+dbHelper.TABLE_PLAYLIST_INFO+","+dbHelper.TABLE_PLAYLIST_SONG+
                " WHERE "+dbHelper.TABLE_PLAYLIST_INFO+"."+dbHelper.COL_PLAYLIST_ID+"="+dbHelper.TABLE_PLAYLIST_SONG+"."+dbHelper.COL_PLAYLIST_ID+"="+playlistID+
                " AND "+dbHelper.TABLE_ALLSONG+"."+dbHelper.COL_PATH+"="+dbHelper.TABLE_PLAYLIST_SONG+"."+dbHelper.COL_PATH+ " order by "+dbHelper.COL_TITLE+" asc";
        Cursor cursor=db.rawQuery(sql,null);
        while (cursor!=null){
            while (cursor.moveToNext()){
                String path=cursor.getString(0);
                String title=cursor.getString(1);
                String album=cursor.getString(2);
                String artist=cursor.getString(3);
                int year=cursor.getInt(4);
                listSong.add(new SongDetails(path,title,album,artist,year));
            }
        }
        return listSong;
    }

    public ArrayList<SongDetails> getAllSong(){
        ArrayList<SongDetails> listSong=new ArrayList<>();
        SQLiteDatabase db=mHelper.getReadableDatabase();
        String[]projection=new String[]{dbHelper.COL_PATH,dbHelper.COL_TITLE,dbHelper.COL_ALBUM,dbHelper.COL_ARTIST,dbHelper.COL_YEAR};
        String sortBy=dbHelper.COL_TITLE+" ASC";
        Cursor cursor=db.query(dbHelper.TABLE_ALLSONG,projection,null,null,null,null,sortBy);
        if (cursor!=null){
            while (cursor.moveToNext()){
                String path=cursor.getString(0);
                String title=cursor.getString(1);
                String album=cursor.getString(2);
                String artist=cursor.getString(3);
                int year=cursor.getInt(4);
                listSong.add(new SongDetails(path,title,album,artist,year));
            }
        }
        return listSong;

    }
    public boolean insertSongToListAll(SongDetails song){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        try{
            ContentValues data=new ContentValues();
            data.put(dbHelper.COL_PATH,song.getmPath());
            data.put(dbHelper.COL_TITLE,song.getmTitle());
            data.put(dbHelper.COL_ALBUM,song.getmAlbum());
            data.put(dbHelper.COL_ARTIST,song.getmArtist());
            data.put(dbHelper.COL_YEAR,song.getmYear());
            return db.insert(dbHelper.TABLE_ALLSONG,null,data)>0;
        }catch(Exception e){
            return false;
        }
    }
    public boolean updateSongFromListAll(SongDetails oldSong, SongDetails newSong){
        SQLiteDatabase db= mHelper.getWritableDatabase();
        try {
            ContentValues data=getInfoSong(newSong);
            return db.update(dbHelper.TABLE_ALLSONG,data,dbHelper.COL_PATH+"=?",new String[]{oldSong.getmPath()})>0;
        }catch(Exception e){
            return false;
        }
    }
    public boolean deleteSongFromAllList(SongDetails song){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        try{
            return db.delete(dbHelper.TABLE_ALLSONG,dbHelper.COL_PATH+"=?",new String[]{song.getmPath()})>0;
        }catch(Exception e){
            return false;
        }
    }
    public boolean insertPlaylist(String name){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        try{
            ContentValues data=new ContentValues();
            data.put(dbHelper.COL_PLAYLIST_NAME,name);
            return db.insert(dbHelper.DROP_TABLE_PLAYLIST_INFO,null,data)>0;
        }catch(Exception e){
            return false;
        }
    }
    public boolean renamePlaylist(String newName,int id){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        try{
            ContentValues data =new ContentValues();
            data.put(dbHelper.COL_PLAYLIST_NAME,newName);
            return db.update(dbHelper.TABLE_PLAYLIST_INFO,data,dbHelper.COL_PLAYLIST_ID+"=?",new String[]{String.valueOf(id)})>0;
        }catch(Exception e){
            return false;
        }
    }
    public boolean deletePlaylist(int id){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        try{
            return db.delete(dbHelper.DROP_TABLE_PLAYLIST_INFO,dbHelper.COL_PLAYLIST_ID,new String[]{String.valueOf(id)})>0;
        }catch (Exception e){
            return false;
        }
    }
    public boolean insertSongToList(int playlistID,SongDetails song){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        try{
            ContentValues data=new ContentValues();
            data.put(dbHelper.COL_PLAYLIST_ID,playlistID);
            data.put(dbHelper.COL_PATH,song.getmPath());
            return db.insert(dbHelper.TABLE_PLAYLIST_SONG,null,data)>0;
        }catch(Exception e){
            return false;
        }
    }
    public boolean deleteSongFromPlaylist(SongDetails song){
        SQLiteDatabase db=mHelper.getWritableDatabase();
        try{
            return db.delete(dbHelper.DROP_TABLE_PLAYLIST_SONG,dbHelper.COL_PATH+"=?",new String[]{song.getmPath()})>0;
        } catch (Exception e) {
            return false;
        }
    }


    private ContentValues getInfoSong(SongDetails song){
        ContentValues data=new ContentValues();
        data.put(dbHelper.COL_PATH,song.getmPath());
        data.put(dbHelper.COL_TITLE,song.getmTitle());
        data.put(dbHelper.COL_ALBUM,song.getmAlbum());
        data.put(dbHelper.COL_ARTIST,song.getmArtist());
        data.put(dbHelper.COL_YEAR,song.getmYear());
        return data;
    }

    private static class dbHelper extends SQLiteOpenHelper {
        private static final int DB_VERSION=1;
        private static final String DB_NAME="StoreMusicData.db";

        private static final String TABLE_ALLSONG="TB_ALLSONG";
        private static final String COL_PATH="FILE_PATH";
        private static final String COL_TITLE="TITLE";
        private static final String COL_ALBUM="ALBUM";
        private static final String COL_ARTIST="ARTIST";
        private static final String COL_YEAR="YEAR";


        private static final String TABLE_PLAYLIST_INFO="TB_PLAYLIST_INFO";
        private static final String COL_PLAYLIST_ID="PLAYLIST_ID";
        private static final String COL_PLAYLIST_NAME="PLAYLIST_NAME";

        private static final String TABLE_PLAYLIST_SONG="TB_PLAYLIST_SONG";



        private static final String CREATE_TB_ALLSONG="CREATE TABLE "+TABLE_ALLSONG+ " ("
                + COL_PATH + " TEXT PRIMARY KEY, "
                + COL_TITLE + " TEXT, "
                + COL_ALBUM + " TEXT, "
                + COL_ARTIST + " TEXT, "
                + COL_YEAR + " TEXT)";
        private static final String CREATE_TB_PLAYlIST_INFO="CREATE TABLE "+TABLE_PLAYLIST_INFO+"("
                + COL_PLAYLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PLAYLIST_NAME + " TEXT)";
        private static final String CREATE_TB_PLAYLIST_SONG="CREATE TABLE "+TABLE_PLAYLIST_SONG+"("
                + COL_PLAYLIST_ID + " TEXT REFERENCES "+TABLE_PLAYLIST_INFO+"("+COL_PLAYLIST_ID+") ON UPDATE CASCADE ON DELETE CASCADE,"
                + COL_PATH + " TEXT REFERENCES "+TABLE_ALLSONG+ "("+COL_PATH+") ON UPDATE CASCADE ON DELETE CASCADE," +
                "PRIMARY KEY ("+dbHelper.COL_PLAYLIST_ID+","+dbHelper.COL_PATH+")) ";

        private static final String DROP_TABLE_ALLSONG="DROP TABLE IF EXISTS "+TABLE_ALLSONG;
        private static final String DROP_TABLE_PLAYLIST_INFO="DROP TABLE IF EXISTS "+TABLE_PLAYLIST_INFO;
        private static final String DROP_TABLE_PLAYLIST_SONG="DROP TABLE IF EXISTS "+TABLE_PLAYLIST_SONG;

        public dbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_TB_ALLSONG);
            db.execSQL(CREATE_TB_PLAYlIST_INFO);
            db.execSQL(CREATE_TB_PLAYLIST_SONG);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_TABLE_ALLSONG);
            db.execSQL(DROP_TABLE_PLAYLIST_INFO);
            db.execSQL(DROP_TABLE_PLAYLIST_SONG);
            onCreate(db);
        }
    }
}
