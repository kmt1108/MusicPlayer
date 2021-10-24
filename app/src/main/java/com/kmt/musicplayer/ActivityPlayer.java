package com.kmt.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ActivityPlayer extends AppCompatActivity {
    private Handler mHandler=new Handler();
    final static int DISC_STATE_NEW=1;
    final static int DISC_STATE_ROUNDING=2;
    final static int DISC_STATE_PAUSE=3;
    SeekBar seekBarProgress;
    ImageView imageViewDisc;
    TextView tvRuntime,tvDestime,tvSongName;
    ImageButton btList,btBack,btShuffle,btPrevious,btPlayPause,btNext,btRepeat;
    ObjectAnimator anim;
    boolean isPlaying=false;
    int stateDisc=1;
    final DateFormat df=new SimpleDateFormat("mm:ss");
    String pathSong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        Intent intent=getIntent();
        pathSong=intent.getStringExtra("path");
        MediaPlayer player = new MediaPlayer();
        player.setAudioAttributes(
                new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
        );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (player.isPlaying()){
                    int currentPosotion=player.getCurrentPosition();
                    seekBarProgress.setProgress(currentPosotion/1000);
                    tvRuntime.setText(df.format(currentPosotion));
                }
                mHandler.postDelayed(this,1000);
            }
        });
        setDiscAnimation();
        btPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying==false){
                    player.start();
                    btPlayPause.setImageResource(R.drawable.player_pausebutton);
                    if (stateDisc==DISC_STATE_NEW){
                        tvSongName.setText(new File(pathSong).getName());
                        tvSongName.setSelected(true);
                        anim.start();
                        stateDisc=DISC_STATE_ROUNDING;
                        long total=player.getDuration();
                        String destTime=df.format(total);
                        tvDestime.setText(destTime);
                        seekBarProgress.setProgress(0);
                        seekBarProgress.setMax((int) (total/1000));
                    }else if(stateDisc==DISC_STATE_PAUSE){
                        anim.resume();
                        stateDisc=DISC_STATE_ROUNDING;
                    }
                    isPlaying=true;
                }else{
                    player.pause();
                    btPlayPause.setImageResource(R.drawable.player_playbutton);
                    imageViewDisc.clearAnimation();
                    anim.pause();
                    stateDisc=DISC_STATE_PAUSE;
                    isPlaying=false;
                }

            }
        });
        seekBarProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                player.seekTo(seekBar.getProgress()*1000);
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                anim.end();
            }
        });
        if (pathSong!=null){
            Uri myUri =Uri.parse(pathSong); // initialize Uri here
            try {
                player.setDataSource(this,myUri);
                player.prepare();
                btPlayPause.callOnClick();
            } catch (IOException e) {
                Log.d(this.getLocalClassName(),"load data error");
            }
        }
    }

    private void setDiscAnimation() {
        anim=ObjectAnimator.ofFloat(imageViewDisc,"rotation",0,360);
        anim.setDuration(30000);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setInterpolator(new LinearInterpolator());
    }

    private void initView() {
        imageViewDisc=findViewById(R.id.imageViewDisc);
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

    }
}
