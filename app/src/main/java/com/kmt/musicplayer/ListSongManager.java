package com.kmt.musicplayer;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ListSongManager {
    public HashMap<String, ArrayList<HashMap<String, String>>> getListSong(String filePath) {
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
                listSong.put(rootfolder.getName(), songs);
                return listSong;
            } else
                return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
