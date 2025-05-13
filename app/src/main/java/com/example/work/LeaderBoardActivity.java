package com.example.work;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
            registerForContextMenu(leaderBoardView);
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        if (info.position != 0) {
            menu.add(1, 1, 1, "修改姓名");
            menu.add(1, 2, 2, "删除记录");
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == 1) {
            EditText input = new EditText(this);
            input.setHint("请输入你的名字");
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            input.setTextColor(Color.BLACK);
            input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20), (source, start, end, dest, dstart, dend) -> {
                if (source.charAt(start) == ' ') return "";
                else return null;
            }});
            new AlertDialog.Builder(this).setTitle("修改姓名")
                    .setView(input)
                    .setPositiveButton("确定", (dialog, which) -> {
                        String newName = input.getText().toString();
                        dbConnectHelper dbConnectHelper = new dbConnectHelper(this);
                        SQLiteDatabase db = dbConnectHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("name", newName);
                        db.update("leaderboard", values, "id=?", new String[]{leaderBoardList.get(info.position).getId() + ""});
                        leaderBoardList.get(info.position).setName(newName);
                    }).show();
        } else if (item.getItemId() == 2) {
            new AlertDialog.Builder(this).setTitle("确定删除吗?")
                    .setPositiveButton("确定", (dialog, which) -> {
                        dbConnectHelper dbConnectHelper = new dbConnectHelper(this);
                        SQLiteDatabase db = dbConnectHelper.getWritableDatabase();
                        db.delete("leaderboard", "id=?", new String[]{leaderBoardList.get(info.position).getId() + ""});
                        leaderBoardList.remove(info.position);
                    }).setNegativeButton("取消", null)
                    .show();
        }
        return super.onContextItemSelected(item);
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


