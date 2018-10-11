package com.saganet.encuestas;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoBase extends AppCompatActivity {
    VideoView videoHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_base);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.video_stops);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if(videoHolder.isPlaying()){
                    videoHolder.stopPlayback();
                }
                //Snackbar.make(view, "Salir", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
            }
        });
        try {
            splashPlayer();
        } catch (Exception ex) {
            jumpMain();
        }
    }
    public void splashPlayer() {
        //VideoView videoHolder = new VideoView(this);
        //setContentView(videoHolder);
        videoHolder = (VideoView)findViewById(R.id.VideoView);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.calidad_de_los_servicios_de_salud);
        videoHolder.setVideoURI(video);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoHolder);
        videoHolder.setMediaController (mediaController);
        videoHolder.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                jumpMain();
            }

        });
        videoHolder.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ((VideoView) v).stopPlayback();
                jumpMain();
                return true;
            }
        });
        videoHolder.start();
    }
    private synchronized void jumpMain() {
        //Intent intent = new Intent(this, setcuestionario.class);
        //startActivity(intent);
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }



}
