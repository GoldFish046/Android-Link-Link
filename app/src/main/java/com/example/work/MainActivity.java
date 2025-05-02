package com.example.work;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_start_game).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_game) {
//            Log.d("debug","btn_start_game");
            Intent intent = new Intent(this, GamePageActivity.class);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("请选择难度");
            builder.setItems(new String[]{"简单", "普通", "困难"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        intent.putExtra("difficulty", "easy");
                        break;
                    case 1:
                        intent.putExtra("difficulty", "normal");
                        break;
                    case 2:
                        intent.putExtra("difficulty", "hard");
                        break;
                }
                startActivity(intent);
            });
            builder.setNegativeButton("取消", null);
            builder.create().show();
        }
    }
}