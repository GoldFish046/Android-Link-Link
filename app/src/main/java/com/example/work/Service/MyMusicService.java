package com.example.work.Service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MyMusicService extends Service {

    private MediaPlayer mediaPlayer;


    public MyMusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new MusicController();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer=new MediaPlayer();
    }


    public class MusicController extends Binder {
        public void play(int i){
//            Toast.makeText(MyMusicService.this, "播放音乐"+i, Toast.LENGTH_SHORT).show();
            Uri uri=Uri.parse("android.resource://"+getPackageName()+"/raw/music"+i);
            try {
//                mediaPlayer.reset();
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                mediaPlayer.start();
            }catch (Exception e){
                e.printStackTrace();
            }
            SharedPreferences  sp=getSharedPreferences("music",MODE_PRIVATE);
            int progress=sp.getInt("music_value",100);
            mediaPlayer.setVolume(progress/100f,progress/100f);
            mediaPlayer.setLooping(i == 1);
        }
        public void pause(){
            mediaPlayer.pause();
        }
        public void stop(){
            mediaPlayer.stop();
        }
        public void change(int i){
            mediaPlayer.setVolume(i/100f,i/100f);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer==null) return;
        if(mediaPlayer.isPlaying()) mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer=null;
    }
}