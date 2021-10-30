package com.kmt.musicplayer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.media.session.PlaybackState;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.util.Size;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.kmt.musicplayer.MyApplication.CHANNEL_ID;

public class PlayerServices extends Service {
    public static final String CURRENT_SETTING = "current_setting";
    public static final String KEY_CURRENT_SONG = "current_setting_song";
    public static final String KEY_SHUFFLE = "current_setting_shuffle";
    public static final String KEY_REPEAT_MODE = "current_setting_repeat";
    public static final String KEY_PLAYING_STATE = "current_playing_state";
    public static final String KEY_CURRENT_PLAYLIST ="current_setting_playlist";

    private static final int NOTIFICATION_PLAY_MUSIC = 111;

    public static final String ACTION_CONTROL_PLAYER = "control_player";

    private static final int TIME_LIMIT_TO_SKIP = 3000;

    public static final int ACTION_SKIP_PREVIOUS = 4;
    public static final int ACTION_SKIP_NEXT = 5;
    public static final int ACTION_START = 1;
    public static final int ACTION_PAUSE = 2;
    public static final int ACTION_RESUME = 3;
    public static final int ACTION_STOP = 0;
    public static final int ACTION_SEEK_TO = 6;

    public static final int REPEAT_MODE_NO_REPEAT = 0;
    public static final int REPEAT_MODE_SINGLE = 1;
    public static final int REPEAT_MODE_ALL = 2;

    private static final String TAG_ERROR ="PlayerServices" ;


    SharedPreferences currentSettingStorage;
    ArrayList<SongDetails> listPlayer;
    MediaPlayer mMediaPlayer;
    MediaSessionCompat sessionPlayer;
    private boolean isPlaying;
    private boolean isShuffle;
    private int repeatMode;
    private int currentPosition=-1;
    MyBinder myBinder=new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int action=intent.getIntExtra(ACTION_CONTROL_PLAYER,-1);
        if (intent.getAction().equals(ACTION_CONTROL_PLAYER)) {
            handleControlPlayer(intent);
        }
        return START_NOT_STICKY;
    }

    private void handleControlPlayer(Intent intent) {
        int action = intent.getIntExtra(ACTION_CONTROL_PLAYER, -1);
        switch (action) {
            case ACTION_START:
                startPlayerAction(intent);
                sendActionToActivity(action);
                saveCurrentSetting(listPlayer.get(currentPosition));
                break;
            case ACTION_PAUSE:
                pausePlayerAction();
                sendActionToActivity(action);
                break;
            case ACTION_RESUME:
                resumePlayerAction();
                sendActionToActivity(action);
                break;
            case ACTION_SKIP_PREVIOUS:
                previousPlayer();
                sendActionToActivity(action);
                break;
            case ACTION_SKIP_NEXT:
                handleCompleteSong();
                sendActionToActivity(action);
                break;
            case ACTION_STOP:
                stopSelf();
                sendActionToActivity(action);
                break;
        }
    }


    private void sendActionToActivity(int action) {
        Intent intent=new Intent();
        intent.setAction(ACTION_CONTROL_PLAYER);
        intent.putExtra(ACTION_CONTROL_PLAYER,action);
        intent.putExtra(KEY_PLAYING_STATE,isPlaying);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void getSettingState() {
        currentSettingStorage=getSharedPreferences(CURRENT_SETTING,MODE_PRIVATE);
        isShuffle=currentSettingStorage.getBoolean(KEY_SHUFFLE,false);
        repeatMode=currentSettingStorage.getInt(KEY_REPEAT_MODE,REPEAT_MODE_NO_REPEAT);
    }

    private void saveCurrentSetting(SongDetails song) {
        currentSettingStorage=getSharedPreferences(CURRENT_SETTING,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=currentSettingStorage.edit();
        String jsonSong = getJsonFromSong(song);
        editor.putString(KEY_CURRENT_SONG,jsonSong);
        editor.apply();
    }
    public String getJsonFromSong(SongDetails song) {
        Gson gson=new Gson();
        return gson.toJson(song);
    }

    private void handleCompleteSong() {
        if (mMediaPlayer!=null&&listPlayer.size()>1) {
            if (isShuffle) {
                handleCompleteSongShuffle();
            } else {
                handleCompleteSongNoShuffle();
            }
        }

        /*if (mMediaPlayer!=null&&listPlayer.size()>1){
            if (repeatMode==REPEAT_MODE_ALL){
                if (currentPosition==listPlayer.size()-1){
                    currentPosition=0;
                    playSongInList(0);
                    sendNotificationPlayer(listPlayer.get(currentPosition));
                }else{
                    currentPosition=currentPosition+1;
                    playSongInList(currentPosition);
                    sendNotificationPlayer(listPlayer.get(currentPosition));
                }
            }else if(repeatMode==REPEAT_MODE_NO_REPEAT||repeatMode==REPEAT_MODE_SINGLE){
                if (currentPosition==listPlayer.size()-1){
                    Toast.makeText(this, getString(R.string.alert_no_next), Toast.LENGTH_SHORT).show();
                }else{
                    currentPosition=currentPosition+1;
                    playSongInList(currentPosition);
                    sendNotificationPlayer(listPlayer.get(currentPosition));
                }
            }
        }*/
    }

    private void handleCompleteSongNoShuffle() {
        if (currentPosition<listPlayer.size()-1){
            currentPosition=currentPosition+1;
            setPlayerData(currentPosition);
            if (isPlaying){
                startOrResumePlayer();
            }
            saveCurrentSetting(listPlayer.get(currentPosition));
            sendNotificationPlayer(listPlayer.get(currentPosition));
        }else if (currentPosition==listPlayer.size()-1){
            if (repeatMode==REPEAT_MODE_ALL){
                currentPosition=0;
                setPlayerData(currentPosition);
                if (isPlaying){
                    startOrResumePlayer();
                }
                saveCurrentSetting(listPlayer.get(currentPosition));
                sendNotificationPlayer(listPlayer.get(currentPosition));
            }else{
                currentPosition=0;
                saveCurrentSetting(listPlayer.get(currentPosition));
                setPlayerData(currentPosition);
                isPlaying=false;
                sendNotificationPlayer(listPlayer.get(currentPosition));
            }
        }
    }

    private void handleCompleteSongShuffle() {
        int shufflePositon =new Random().nextInt(listPlayer.size()-1);
        while (shufflePositon==currentPosition){
            shufflePositon =new Random().nextInt(listPlayer.size()-1);
        }
        currentPosition=shufflePositon;
        if (isPlaying){
            playSongInList(currentPosition);
        }
        sendNotificationPlayer(listPlayer.get(currentPosition));
    }

    private  void handleFinishPlaylist(){

    }

    private void previousPlayer() {
        if (mMediaPlayer!=null){
            if (mMediaPlayer.getCurrentPosition()<TIME_LIMIT_TO_SKIP){
                if (currentPosition>0){
                    currentPosition=currentPosition-1;
                    setPlayerData(currentPosition);
                    if (isPlaying){
                        startOrResumePlayer();
                    }
                    saveCurrentSetting(listPlayer.get(currentPosition));
                    sendNotificationPlayer(listPlayer.get(currentPosition));
                }
            }else{
                mMediaPlayer.seekTo(0);
                sendNotificationPlayer(listPlayer.get(currentPosition));
            }
        }
    }

    private void sendActionCallback(int action) {
        Intent intent = new Intent(this, PlayerServices.class);
        intent.setAction(ACTION_CONTROL_PLAYER);
        intent.putExtra(ACTION_CONTROL_PLAYER, action);
        startService(intent);

    }

    private void resumePlayerAction() {
        if (mMediaPlayer!=null&&!isPlaying){
            startOrResumePlayer();
            isPlaying=true;
            sendNotificationPlayer(listPlayer.get(currentPosition));
        }
    }

    private void pausePlayerAction() {
        if (mMediaPlayer!=null&&isPlaying){
            mMediaPlayer.pause();
            isPlaying=false;
            sendNotificationPlayer(listPlayer.get(currentPosition));
        }
    }

    private void startPlayerAction(Intent intent) {
        if (listPlayer==null){
            listPlayer=intent.getParcelableArrayListExtra("playlist");
        }
        SongDetails song=intent.getParcelableExtra("song");
        if (song != null) {
            if (listPlayer.contains(song)) {
                int index = listPlayer.indexOf(song);
                if (currentPosition == -1 || index != currentPosition) {
                    currentPosition = index;
                    playSongInList(currentPosition);
                    sendNotificationPlayer(song);
                }
            } else {
                listPlayer.add(song);
                currentPosition = listPlayer.indexOf(song);
                playSongInList(currentPosition);
                sendNotificationPlayer(song);
            }
        }
    }

    private void playSongInList(int index) {
        setPlayerData(index);
        startOrResumePlayer();
           /* try {
                mMediaPlayer.setDataSource(this, Uri.parse(song.getmPath()));
                mMediaPlayer.prepare();
                isPlaying = true;
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }*/
    }
    private void setPlayerData(int postion){
        String path=listPlayer.get(postion).getmPath();
        if (mMediaPlayer!=null){
            mMediaPlayer.release();
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build());
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            Log.e(TAG_ERROR,"playBackNew:"+e.getMessage());
        }
        mMediaPlayer.setOnCompletionListener(mp -> handleCompleteSong());
    }
    public void startOrResumePlayer(){
        if (mMediaPlayer!=null){
            mMediaPlayer.start();
            isPlaying=true;
        }


    }


    private void sendNotificationPlayer(SongDetails song) {
        setSessionPlayer();
        Bitmap thumbail=getSongThumbail(song.getmPath(),256,256);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(song.getmTitle())
                .setContentText(song.getmArtist())
                .setSmallIcon(R.drawable.ic_small_icon_notification)
                .setLargeIcon(thumbail)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2)
                        .setMediaSession(sessionPlayer.getSessionToken()));
        if (isPlaying){
            builder.addAction(R.drawable.ic_notification_prev_player, "Previous", getActionControlPlayerIntent(this, ACTION_SKIP_PREVIOUS))
                    .addAction(R.drawable.ic_notification_pause_player, "Pause", getActionControlPlayerIntent(this, ACTION_PAUSE))
                    .addAction(R.drawable.ic_notification_next_player, "Next", getActionControlPlayerIntent(this, ACTION_SKIP_NEXT))
                    .addAction(R.drawable.ic_notification_clear_player, "Stop", getActionControlPlayerIntent(this, ACTION_STOP));
        }else{
            builder.addAction(R.drawable.ic_notification_prev_player, "Previous", getActionControlPlayerIntent(this, ACTION_SKIP_PREVIOUS))
                    .addAction(R.drawable.ic_notification_play_player, "Play", getActionControlPlayerIntent(this, ACTION_RESUME))
                    .addAction(R.drawable.ic_notification_next_player, "Next", getActionControlPlayerIntent(this, ACTION_SKIP_NEXT))
                    .addAction(R.drawable.ic_notification_clear_player, "Stop", getActionControlPlayerIntent(this, ACTION_STOP));
        }
        Notification notification=builder.build();
        startForeground(NOTIFICATION_PLAY_MUSIC, notification);
    }
    @SuppressLint("WrongConstant")
    public void setSessionPlayer() {
        PlaybackStateCompat.Builder builder=new PlaybackStateCompat.Builder();
        if (isPlaying){
            builder.setState(PlaybackStateCompat.STATE_PLAYING,mMediaPlayer.getCurrentPosition(),1f,SystemClock.elapsedRealtime());
        }else {
            builder.setState(PlaybackState.STATE_PAUSED,mMediaPlayer.getCurrentPosition(),1f,SystemClock.elapsedRealtime());
        }
        builder.setActions(PlaybackState.ACTION_SEEK_TO);
        PlaybackStateCompat playbackState=builder.build();

        if (sessionPlayer==null){
            sessionPlayer=new MediaSessionCompat(this,"player");
        }
        sessionPlayer.setActive(true);
        sessionPlayer.setMetadata(new MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION,mMediaPlayer.getDuration()).build());
        sessionPlayer.setPlaybackState(playbackState);
        sessionPlayer.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                seekTo((int) pos);
                sendNotificationPlayer(listPlayer.get(currentPosition));
            }
        });
    }

    private Bitmap getSongThumbail(String path, int width, int height) {
        Bitmap thumbail;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                thumbail = ThumbnailUtils.createAudioThumbnail(new File(path), new Size(width, height), null);
            } catch (IOException e) {
                Log.e(TAG_ERROR,"sendNotificationPlayer:load thumbail Error");
                thumbail = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.player_disc),width,height,true);
            }
        } else {
            thumbail = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.player_disc),width,height,true);
        }
        return thumbail;
    }

    private PendingIntent getActionControlPlayerIntent(Context context, int action) {
        Intent intent = new Intent(context.getApplicationContext(), PlayerServices.class);
        intent.setAction(ACTION_CONTROL_PLAYER);
        intent.putExtra(ACTION_CONTROL_PLAYER, action);
        return PendingIntent.getService(context.getApplicationContext(),action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        getSettingState();

    }

    public long getCurrentPositionPlayer(){
        return mMediaPlayer.getCurrentPosition();
    }
    public long getDurationPlayer(){
        return mMediaPlayer.getDuration();
    }
    public void seekTo(int position){
        mMediaPlayer.seekTo(position);
        sendNotificationPlayer(listPlayer.get(currentPosition));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
    public class MyBinder extends android.os.Binder{
        public PlayerServices getPlayerServices(){
            return PlayerServices.this;
        }
    }
}
