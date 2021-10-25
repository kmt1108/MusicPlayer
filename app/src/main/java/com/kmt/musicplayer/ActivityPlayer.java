package com.kmt.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ActivityPlayer extends AppCompatActivity {
    private static final String TAG_ERROR ="ActivityPlayer" ;

    SharedPreferences currentSettingStorage;
    SeekBar seekBarProgress;
    ImageView imageViewDisc;
    TextView tvRuntime,tvDestime,tvSongName,tvArtist;
    ImageButton btList,btBack,btShuffle,btPrevious,btPlayPause,btNext,btRepeat;
    ObjectAnimator discRoudingAnimation;
    private boolean isPlaying;
    private int repeatMode=PlayerServices.REPEAT_MODE_NO_REPEAT;
    private boolean isShuffle;


    final DateFormat df=new SimpleDateFormat("mm:ss");

    BroadcastReceiver receiverPlayerControl=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            syncControlPlayer(intent);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverPlayerControl,new IntentFilter(PlayerServices.ACTION_CONTROL_PLAYER));

        Intent intent=getIntent();
        if (intent!=null){
            ComponentName componentName=new ComponentName(this,PlayerServices.class);
            intent.setComponent(componentName);
            startService(intent);
        }
        setDiscAnimation();

    }
    private void syncControlPlayer(Intent intent) {
        int action=intent.getIntExtra(PlayerServices.ACTION_CONTROL_PLAYER,-1);
        switch (action){
            case PlayerServices.ACTION_START:
                showSongInfo();
                isPlaying=intent.getBooleanExtra(PlayerServices.KEY_PLAYING_STATE,false);
                setStateButtonPlay();
                setDiscState();
                break;
            case PlayerServices.ACTION_PAUSE:
                isPlaying=intent.getBooleanExtra(PlayerServices.KEY_PLAYING_STATE,false);
                setStateButtonPlay();
                setDiscState();
                break;
            case PlayerServices.ACTION_RESUME:
                isPlaying=intent.getBooleanExtra(PlayerServices.KEY_PLAYING_STATE,false);
                setStateButtonPlay();
                setDiscState();
                break;
            case PlayerServices.ACTION_SKIP_PREVIOUS:
                break;
            case PlayerServices.ACTION_SKIP_NEXT:
                break;
            case PlayerServices.ACTION_STOP:
                break;

        }
    }

    private void setStateButtonPlay() {
        if (isPlaying){
            btPlayPause.setImageResource(R.drawable.player_pausebutton);
        }else {
            btPlayPause.setImageResource(R.drawable.player_playbutton);
        }

    }

    private void showSongInfo() {
        currentSettingStorage=getSharedPreferences(PlayerServices.CURRENT_SETTING,MODE_PRIVATE);
        SongDetails song=getSongFromString(currentSettingStorage.getString(PlayerServices.KEY_CURRENT_SONG,null));
        tvSongName.setText(song.getmTitle());
        tvArtist.setText(song.getmArtist());
        imageViewDisc.setImageBitmap(getSongThumbail(song.getmPath(),1024,1024));
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

    private SongDetails getSongFromString(String string) {
        Gson gson=new Gson();
        return gson.fromJson(string,SongDetails.class);
    }


    private void setDiscState(){
        if (isPlaying){
            discRoudingAnimation.start();
        }else{
            discRoudingAnimation.pause();
        }
    }

    private void setDiscAnimation() {
        discRoudingAnimation=ObjectAnimator.ofFloat(imageViewDisc,"rotation",0,360);
        discRoudingAnimation.setDuration(30000);
        discRoudingAnimation.setRepeatCount(ValueAnimator.INFINITE);
        discRoudingAnimation.setRepeatMode(ValueAnimator.RESTART);
        discRoudingAnimation.setInterpolator(new LinearInterpolator());
    }

    private void initView() {
        Bitmap bitmap=Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.player_disc), 1024,1024,true);
        imageViewDisc=findViewById(R.id.imageViewDisc);
        imageViewDisc.setImageBitmap(bitmap);
        btList=findViewById(R.id.btCurrentlist);
        btBack=findViewById(R.id.btBack);
        btShuffle=findViewById(R.id.btShuffle);
        btPrevious=findViewById(R.id.btPrevious);
        btNext=findViewById(R.id.btNext);
        btPlayPause=findViewById(R.id.btPlayPause);
        btRepeat=findViewById(R.id.btRepeat);
        seekBarProgress=findViewById(R.id.seekBarController);
        tvRuntime=findViewById(R.id.tvRunTime);
        tvDestime=findViewById(R.id.tvDestTime);
        tvSongName=findViewById(R.id.txtSongName);
        tvSongName.setSelected(true);
        tvArtist=findViewById(R.id.txtArtist);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverPlayerControl);
    }
}
