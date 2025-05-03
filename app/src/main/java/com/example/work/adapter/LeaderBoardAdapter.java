package com.example.work.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.work.R;
import com.example.work.entity.LeaderBoard;

import java.text.DateFormat;
import java.util.List;

public class LeaderBoardAdapter extends ArrayAdapter<LeaderBoard> {
    private final int resourceId;

    public LeaderBoardAdapter(Context context, int textViewResourceId, List<LeaderBoard> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LeaderBoard leaderBoard = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.name = view.findViewById(R.id.leaderboard_item_name);
            viewHolder.score = view.findViewById(R.id.leaderboard_item_score);
            viewHolder.time = view.findViewById(R.id.leaderboard_item_time);
            viewHolder.hard = view.findViewById(R.id.leaderboard_item_hard);
            viewHolder.finishTime = view.findViewById(R.id.leaderboard_item_finishtime);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (position == 0) {
            viewHolder.name.setText("姓名");
            viewHolder.score.setText("分数");
            viewHolder.time.setText("耗时");
            viewHolder.hard.setText("难度");
            viewHolder.finishTime.setText("完成时间");
        } else {
            viewHolder.name.setText(leaderBoard.getName());
            viewHolder.score.setText(String.valueOf(leaderBoard.getScore()));
            viewHolder.time.setText(String.valueOf(leaderBoard.getTime()));
            viewHolder.hard.setText(String.valueOf(leaderBoard.getHard()));
            viewHolder.finishTime.setText(leaderBoard.getFinishtime());
        }
        return view;
    }

    static class ViewHolder {
        TextView name;
        TextView score;
        TextView time;
        TextView hard;
        TextView finishTime;
    }
}
