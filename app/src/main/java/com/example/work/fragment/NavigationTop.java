package com.example.work.fragment;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.work.GamePageActivity;
import com.example.work.LeaderBoardActivity;
import com.example.work.R;
import com.example.work.Service.MyMusicService;

public class NavigationTop extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_top, container, false);
        ImageButton imageButton = view.findViewById(R.id.btn_back);
        TextView textView = view.findViewById(R.id.txt_setting);
        if (GamePageActivity.class.equals(requireActivity().getClass())) {
            imageButton.setOnClickListener(v -> {
                GamePageActivity gamePageActivity = (GamePageActivity) requireActivity();
                gamePageActivity.back();
            });
            textView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("音量调节");
                SeekBar seekBar = new SeekBar(requireActivity());
                seekBar.setMax(100);
                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("music_value", 0);
                seekBar.setProgress(sharedPreferences.getInt("music_value", 100));
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (fromUser) {
                            editor.putInt("music_value", progress);
                            editor.apply();
                            GamePageActivity  gamePageActivity = (GamePageActivity) requireActivity();
                            MyMusicService.MusicController musicController =gamePageActivity.getMusicController();
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
                builder.setView(seekBar);
                builder.setPositiveButton("确定", (dialog, which) -> {
                    GamePageActivity gamePageActivity = (GamePageActivity) requireActivity();
                    gamePageActivity.startTimer();
                });
                builder.show();
            });
        }
        if (LeaderBoardActivity.class.equals(requireActivity().getClass())) {
            imageButton.setOnClickListener(v -> {
                LeaderBoardActivity leaderBoardActivity = (LeaderBoardActivity) requireActivity();
                leaderBoardActivity.finish();
            });
            textView.setAlpha(0);
            textView.setText("");
        }
        return view;
    }
}
