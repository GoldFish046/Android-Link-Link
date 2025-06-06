package com.example.work;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.work.Service.MyMusicService;
import com.example.work.utils.dbConnectHelper;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private MyMusicService.MusicController musicController;
    private ActivityResultLauncher<Intent> launcher;

    //    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start_game).setOnClickListener(this);
        findViewById(R.id.btn_leaderboard).setOnClickListener(this);
        findViewById(R.id.btn_start_timer).setOnClickListener(this);
        dbConnectHelper dbConnectHelper = new dbConnectHelper(MainActivity.this);
        SQLiteDatabase db = dbConnectHelper.getWritableDatabase();
        launcher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
            if(o.getResultCode()==RESULT_OK){
                if (o.getData() != null && Objects.equals(o.getData().getStringExtra("returnFirst"), "gamePage")) {
                    musicController.play(1);
                }
            }
        });
        sharedPreferences = getSharedPreferences("music", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        int musicValue1 = sharedPreferences.getInt("music_value", 100);
//        int musicValue2 = sharedPreferences.getInt("sound_value2", 0);
        SeekBar seekBar1 = findViewById(R.id.seekBar1);
//        SeekBar seekBar2 = findViewById(R.id.seekBar2);
//        if (musicValue1 != 0) seekBar1.setProgress(musicValue1);
//        else seekBar1.setProgress(100);
        seekBar1.setProgress(musicValue1);
//        if (musicValue2 != 0) seekBar2.setProgress(musicValue2);
//        else seekBar2.setProgress(100);
        seekBar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editor.putInt("music_value", progress);
                    editor.apply();
                    musicController.change(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
//        seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser) {
//                    editor.putInt("sound_value2", progress);
//                    editor.apply();
//                    musicController.change();
//                }
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
        Intent musicIntent = new Intent(this, MyMusicService.class);
        bindService(musicIntent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                musicController = (MyMusicService.MusicController) service;
                musicController.play(1);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, Context.BIND_AUTO_CREATE);
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.POST_NOTIFICATIONS},1);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
//        Log.d("dclick",audioManager.isMusicActive()+"");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (musicController != null) musicController.stop();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_game || v.getId() == R.id.btn_start_timer) {
//            Log.d("debug","btn_start_game");
            Intent intent = new Intent(this, GamePageActivity.class);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请选择难度");
            builder.setItems(new String[]{"简单", "普通", "困难"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        if (v.getId() == R.id.btn_start_game) intent.putExtra("difficulty", "easy");
                        else intent.putExtra("difficulty", "timerEasy");
                        break;
                    case 1:
                        if (v.getId() == R.id.btn_start_game)
                            intent.putExtra("difficulty", "normal");
                        else intent.putExtra("difficulty", "timerNormal");
                        break;
                    case 2:
                        if (v.getId() == R.id.btn_start_game) intent.putExtra("difficulty", "hard");
                        else intent.putExtra("difficulty", "timerHard");
                        break;
                }
                launcher.launch(intent);
            });
            builder.setNegativeButton("取消", null);
            builder.create().show();
        } else if (v.getId() == R.id.btn_leaderboard) {
            Intent intent = new Intent(this, LeaderBoardActivity.class);
            startActivity(intent);
        }
    }
}