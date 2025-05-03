package com.example.work.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.work.GamePageActivity;
import com.example.work.LeaderBoardActivity;
import com.example.work.R;

import java.util.Objects;

public class NavigationTop extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation_top, container, false);
        ImageButton imageButton = view.findViewById(R.id.btn_back);
        if (GamePageActivity.class.equals(requireActivity().getClass())) {
            imageButton.setOnClickListener(v -> {
                GamePageActivity gamePageActivity = (GamePageActivity) requireActivity();
                gamePageActivity.back();
            });
        }
        if (LeaderBoardActivity.class.equals(requireActivity().getClass())) {
            imageButton.setOnClickListener(v -> {
                LeaderBoardActivity  leaderBoardActivity = (LeaderBoardActivity) requireActivity();
                leaderBoardActivity.finish();
            });
        }
        return view;
    }
}
