package com.kmt.musicplayer;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ActivityPlayer extends AppCompatActivity {
    private static final String TAG_ERROR ="ActivityPlayer" ;

    PlayerServices playerServices;
    SharedPreferences currentSettingStorage;
    SeekBar seekBarProgress;
    ImageView imageViewDisc;
    TextView tvRuntime,tvDestime,tvSongName,tvArtist;
    ImageButton btList,btBack,btShuffle,btPrevious,btPlayPause,btNext,btRepeat;
    ObjectAnimator discRoudingAnimation;
    ProgressDialog waiter;
    private boolean isPlaying;
    private int repeatMode;
    private boolean isShuffle;
    private boolean isServicesConnected;


    final DateFormat df=new SimpleDateFormat("mm:ss");

    BroadcastReceiver receiverPlayerControl=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            syncControlPlayer(intent);
        }
    };
    private ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isServicesConnected=true;
            PlayerServices.MyBinder myBinder= (PlayerServices.MyBinder) service;
            playerServices=myBinder.getPlayerServices();
            long duration=playerServices.getDurationPlayer();
            tvDestime.setText(df.format(duration));
            seekBarProgress.setMax((int) (duration/1000));
            Handler handler=new Handler();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isServicesConnected){
                        long position= playerServices.getCurrentPositionPlayer();
                        seekBarProgress.setProgress((int) (position/1000));
                        tvRuntime.setText(df.format(position));
                        handler.postDelayed(this,1000);
                    }

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServicesConnected=false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiverPlayerControl,new IntentFilter(PlayerServices.ACTION_CONTROL_PLAYER));
        waiter=new ProgressDialog(this);
        Intent intent=getIntent();
        if (intent!=null){
            ComponentName componentName=new ComponentName(this,PlayerServices.class);
            intent.setComponent(componentName);
            startService(intent);
            bindService(intent,serviceConnection,Context.BIND_AUTO_CREATE);
        }
        setDiscAnimation();

        btPlayPause.setOnClickListener(v -> {
            if (isPlaying){
                sendActionToServices(PlayerServices.ACTION_PAUSE);
            }else{
                sendActionToServices(PlayerServices.ACTION_RESUME);
            }
        });
        btPrevious.setOnClickListener(v -> sendActionToServices(PlayerServices.ACTION_SKIP_PREVIOUS));
        btNext.setOnClickListener(v -> sendActionToServices(PlayerServices.ACTION_SKIP_NEXT));
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                playerServices.seekTo(seekBar.getProgress()*1000);
            }
        });
    }

    private void sendActionToServices(int action) {
        Intent intent=new Intent(this,PlayerServices.class);
        intent.setAction(PlayerServices.ACTION_CONTROL_PLAYER);
        switch (action){
            case PlayerServices.ACTION_RESUME:
                intent.putExtra(PlayerServices.ACTION_CONTROL_PLAYER,action);
                break;
            case PlayerServices.ACTION_PAUSE:
                intent.putExtra(PlayerServices.ACTION_CONTROL_PLAYER,action);
                break;
            case PlayerServices.ACTION_SKIP_NEXT:
                intent.putExtra(PlayerServices.ACTION_CONTROL_PLAYER,action);
                break;
            case PlayerServices.ACTION_SKIP_PREVIOUS:
                intent.putExtra(PlayerServices.ACTION_CONTROL_PLAYER,action);
                break;
            case PlayerServices.ACTION_SEEK_TO:
                break;
        }
        startService(intent);
    }

    private void syncControlPlayer(Intent intent) {
        int action=intent.getIntExtra(PlayerServices.ACTION_CONTROL_PLAYER,-1);
        isPlaying=intent.getBooleanExtra(PlayerServices.KEY_PLAYING_STATE,false);
        switch (action){
            case PlayerServices.ACTION_START:
                showSongInfo();
                setStatePlaying();
                break;
            case PlayerServices.ACTION_PAUSE:
                setStatePlaying();
                break;
            case PlayerServices.ACTION_RESUME:
                setStatePlaying();
                break;
            case PlayerServices.ACTION_SKIP_PREVIOUS:
                showSongInfo();
                updateProgessAndDestime();
                setStatePlaying();
                break;
            case PlayerServices.ACTION_SKIP_NEXT:
                showSongInfo();
                updateProgessAndDestime();
                setStatePlaying();
                break;
            case PlayerServices.ACTION_STOP:
                stopBindServices();
                break;

        }
    }

    private void setStatePlaying() {
        setStateButtonPlay();
        setDiscState();
    }

    private void updateProgessAndDestime() {
        if (playerServices!=null){
            long duration=playerServices.getDurationPlayer();
            tvDestime.setText(df.format(duration));
            seekBarProgress.setMax((int) (duration/1000));
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
            if (discRoudingAnimation.isStarted()){
                discRoudingAnimation.resume();
            }else {
                discRoudingAnimation.start();
            }
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
    private void stopBindServices(){
        if (isServicesConnected){
            unbindService(serviceConnection);
        }
        isServicesConnected=false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiverPlayerControl);
        stopBindServices();
    }
}
