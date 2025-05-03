package com.example.work;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.work.adapter.LeaderBoardAdapter;
import com.example.work.entity.LeaderBoard;
import com.example.work.utils.dbConnectHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class LeaderBoardActivity extends AppCompatActivity {
    private final List<LeaderBoard> leaderBoardList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_leaderboard);
        try {
            initLeaderBoard();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        ListView leaderBoardView = findViewById(R.id.leaderboard_list);

        Handler handler = new Handler();
        handler.post(() -> {
            LeaderBoardAdapter leaderBoardAdapter = new LeaderBoardAdapter(this, R.layout.fragment_leaderboard_item, leaderBoardList);
            leaderBoardView.setAdapter(leaderBoardAdapter);
        });
    }

    private void initLeaderBoard() throws ParseException {
        leaderBoardList.add(new LeaderBoard(0, "", 0, 0, "", 0));
        dbConnectHelper dbConnectHelper = new dbConnectHelper(this);
        SQLiteDatabase db = dbConnectHelper.getWritableDatabase();
        Cursor cursor = db.query("leaderboard", null, null, null, null, null, "score");
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow("score"));
            int hard = cursor.getInt(cursor.getColumnIndexOrThrow("hard"));
            String finishtime = cursor.getString(cursor.getColumnIndexOrThrow("finishtime"));
            Log.d("finishtime", finishtime);
            float time = cursor.getFloat(cursor.getColumnIndexOrThrow("time"));
            leaderBoardList.add(new LeaderBoard(id, name, score, hard, finishtime, time));
        }
        cursor.close();
    }
}


