package com.example.work;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.work.Service.MyMusicService;
import com.example.work.utils.MyRandom;
import com.example.work.utils.dbConnectHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GamePageActivity extends AppCompatActivity implements View.OnClickListener {

    //计时器
    private int timer;
    //控制计时器的开关
    private boolean startTimer = true;
    //游戏UI面板的二维数组
    private List<List<Integer>> gameBoardBoxes;
    //游戏UI面板的二维逻辑数组
    private List<List<Integer>> gameBoardBoxesLogic;
    private int[] boxColors;
    //分数
    private int score;
    //记录上一次点击的方块的id
    private int clickedBoxId = -1;
    //计时文本组件
    TextView txtGameTime;
    //方块尺寸
    int[] boxSize = new int[]{0, 0};
    //标题栏高度
    int titleHeight;
    //线程池
    private ExecutorService executorService;
    //计时器
    private ScheduledExecutorService scheduledExecutorService;
    private SQLiteDatabase db;
    private int hard;
    private boolean mode;

    public MyMusicService.MusicController getMusicController() {
        startTimer = false;
        return musicController;
    }
    public void startTimer(){
        startTimer = true;
    }

    private MyMusicService.MusicController musicController;
//    private MyMusicService.MusicController music2Controller;
    private Intent musicIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gamepage);
        txtGameTime = findViewById(R.id.txt_game_time);
        executorService = Executors.newFixedThreadPool(1);
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        //初始化数据
        initData(savedInstanceState);
        //计时器
        timer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        scheduledExecutorService.shutdown();
        if (musicController != null) {
            musicController.stop();
        }
    }

    @Override
    public void onClick(View v) {
        if (Arrays.equals(boxSize, new int[]{0, 0})) {
            int temp = 11;
            boxSize = new int[]{findViewById(temp).getWidth(), findViewById(temp).getHeight()};
            Log.d("click", findViewById(temp).getWidth() + "//" + findViewById(temp).getHeight());
            titleHeight = findViewById(R.id.title).getHeight();
        }
/*        if (v.getId() == R.id.btn_back) {
            back();
        } else*/
        executorService.execute(() -> {
            if (clickedBoxId == -1) {
                clickedBoxId = v.getId();
                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(() -> {
                    ImageButton  btn = findViewById(clickedBoxId);
                    btn.setAlpha(0.5f);
                },0);
            } else if (clickedBoxId != v.getId()) {
                findViewById(clickedBoxId).setAlpha(1f);
                List<Integer> path = getPath(clickedBoxId, v.getId());
                if (path.get(0) != 0) {
                    musicController.play(2);
//                    startService(musicIntent);
//                    bindService(musicIntent, new ServiceConnection() {
//                        @Override
//                        public void onServiceConnected(ComponentName name, IBinder service) {
//                            musicController = (MyMusicService.MusicController) service;
//                            Log.d("click",(musicController==null)+"");
//                        }
//
//                        @Override
//                        public void onServiceDisconnected(ComponentName name) {
//                        }
//                    }, Service.BIND_AUTO_CREATE);
//                    Log.d("click",(musicController==null)+"");
//                    Log.d("click", v.getId() + "//" + clickedBoxId);
                    score++;
                    drawLine(path);
                }
//                findViewById(clickedBoxId).setEnabled(true);
                clickedBoxId = -1;
            }
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("hard", hard);
    }
    public void back() {
        startTimer = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否退出当前游戏");
        builder.setPositiveButton("确定", (dialog, which) -> saveScore());
        builder.setNegativeButton("取消", (dialog, which) -> {
            startTimer = true;
//                timerHandler.postDelayed(timerRunnable, 0);
        });
        builder.create().show();
    }
    private void BroadcastIntent(boolean mode) {
        Intent intent = new Intent();
        if (mode) intent.setAction("com.example.work.broadcast.GAME_SUCCESS");
        if (!mode) intent.setAction("com.example.work.broadcast.GAME_FAIL");
        intent.putExtra("mode", mode);
        sendBroadcast(intent);
    }

    /**
     * 初始化数据
     */
    private void initData(Bundle savedInstanceState) {
        executorService.submit(() -> {
            db = new dbConnectHelper(this).getWritableDatabase();
            timer = 0;
            score = 0;
            boxColors = new int[]{R.color.box1, R.color.box2, R.color.box3, R.color.box4, R.color.box5, R.color.box6, R.color.box7, R.color.box8, R.color.box9, R.color.box10
                    , R.color.box11, R.color.box12, R.color.box13, R.color.box14, R.color.box15, R.color.box16, R.color.box17, R.color.box18, R.color.box19, R.color.box20};
            //随机选取的颜色种类
            List<Integer> selectColors = new ArrayList<>();
            //获取难度
            if (savedInstanceState != null) {
                hard = savedInstanceState.getInt("hard");
            } else {
                Intent intent = getIntent();
                String difficulty = intent.getStringExtra("difficulty");
                if (difficulty != null) {
                    switch (difficulty) {
                        case "easy":
                            selectColors = MyRandom.selectColors(10);
                            hard = 1;
                            break;
                        case "normal":
                            selectColors = MyRandom.selectColors(15);
                            hard = 2;
                            break;
                        case "hard":
                            selectColors = MyRandom.selectColors(20);
                            hard = 3;
                            break;
                        case "timerEasy":
                            selectColors = MyRandom.selectColors(10);
                            hard = 4;
                            break;
                        case "timerNormal":
                            selectColors = MyRandom.selectColors(15);
                            hard = 5;
                            break;
                        case "timerHard":
                            selectColors = MyRandom.selectColors(20);
                            hard = 6;
                            back();
                        default:
                            break;
                    }
                } else {
                    Toast.makeText(this, "请选择游戏难度", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            mode = hard > 3;
            //各种颜色所有的方块数（随机）
            Map<String, Integer> colorNumbers = MyRandom.colorNumbers(selectColors);
            //二维数组用于存放各种颜色的位置（随机）
            gameBoardBoxes = MyRandom.dislocate(selectColors, colorNumbers);
            gameBoardBoxesLogic = MyRandom.getDislocateLogic(new ArrayList<>(gameBoardBoxes));
            Log.d("gameBoardBoxes", gameBoardBoxes.toString());
            Log.d("gameBoardBoxesLogic", gameBoardBoxesLogic.toString());
            //开启音乐服务
            musicIntent = new Intent(this, MyMusicService.class);
            startService(musicIntent);
            bindService(musicIntent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    musicController = (MyMusicService.MusicController) service;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {

                }
            }, Context.BIND_AUTO_CREATE);
//            bindService(musicIntent, new ServiceConnection() {
//                @Override
//                public void onServiceConnected(ComponentName name, IBinder service) {
//                    music2Controller = (MyMusicService.MusicController) service;
//                    music2Controller.play(1);
//                }
//
//                @Override
//                public void onServiceDisconnected(ComponentName name) {
//
//                }
//            }, Context.BIND_AUTO_CREATE);

            initView();
        });
    }

    /**
     * 初始化UI
     */
//    @SuppressLint("ResourceType")
    private void initView() {
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(() -> {
            //生成游戏面板
            LinearLayout gameBoard = findViewById(R.id.game_board);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
//            btnParams.setMargins(10, 10, 10, 10);
            for (int i = 1; i < 10; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
                row.setId(i);
                row.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 1; j < 9; j++) {
                    ImageButton imageButton = new ImageButton(this);
                    imageButton.setId(i * 10 + j);
                    imageButton.setLayoutParams(btnParams);
//                    imageButton.setBackground(ContextCompat.getDrawable(this, R.drawable.border));
//                    imageButton.setBackgroundColor(ContextCompat.getColor(this, boxColors[gameBoardBoxes.get(i - 1).get(j - 1)]));
                    imageButton.setOnClickListener(this);
//                    Bitmap bitmap = Bitmap.createBitmap(imageButton.getWidth(), imageButton.getHeight(), Bitmap.Config.ARGB_8888);
//                    Canvas canvas = new Canvas(bitmap);
//                    Paint paint = new Paint();
//                    paint.setColor(Color.BLACK);
//                    paint.setStrokeWidth(5);
//                    canvas.drawText(String.valueOf(gameBoardBoxesLogic.get(i - 1).get(j)), (float) imageButton.getWidth() / 2, (float) imageButton.getHeight() / 2, paint);
//                    imageButton.setImageBitmap(bitmap);
                    row.addView(imageButton);
                }
                uiHandler.post(() -> {
                    for (int j = 0; j < 8; j++) {
                        ImageButton imageButton = (ImageButton) row.getChildAt(j);
//                        Log.d("click", imageButton.getId() + "//" + imageButton.getHeight() + "//" + imageButton.getWidth());
                        Bitmap bitmap = Bitmap.createBitmap(imageButton.getWidth(), imageButton.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        Paint paint = new Paint();
                        canvas.drawColor(ContextCompat.getColor(this, boxColors[gameBoardBoxesLogic.get(row.getId()).get(j+1)]));
                        paint.setColor(Color.BLACK);
                        paint.setTextSize(80);
                        paint.setStrokeWidth(10);
                        canvas.drawText(String.valueOf(gameBoardBoxesLogic.get(row.getId()).get(j + 1)), (float) 15, (float) imageButton.getHeight() / 3 * 2, paint);
                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawRect(0, 0, imageButton.getWidth(), imageButton.getHeight(), paint);
                        imageButton.setImageBitmap(bitmap);
                    }
                });
//                Log.d("click",row.getId()+"");
                gameBoard.addView(row);
            }
            TextView txtGameScore = findViewById(R.id.txt_game_score);
            txtGameScore.setText("得分：" + score);
//            Log.d("Size", findViewById(R.id.gamePage).getHeight() + "//" + findViewById(R.id.title).getHeight() + "//" + findViewById(R.id.playground).getHeight());
//            Log.d("Size", Arrays.toString(boxAxis));
//            int[] location = new int[2];
//            findViewById(temp).getLocationInWindow(location);
//            Log.d("Size", Arrays.toString(location));
//            findViewById(R.id.playground).getLocationInWindow(location);
//            Log.d("Size", Arrays.toString(location));
        });
    }

    /**
     * 计时器
     */
    private void timer() {
        //                    timerHandler.postDelayed(this, 100);
        //计时器的runnable对象
        Runnable timerRunnable = () -> {
            if (startTimer) {
                if (!mode) txtGameTime.setText("游戏时间：" + timer / 10.0 + "秒");
                else txtGameTime.setText("倒计时:" + (120 - timer / 10.0) + "秒");
                if (mode && (120 - timer / 10.0) <= 0 && score != 36) {
                    BroadcastIntent(false);
                    startTimer = false;
                    new AlertDialog.Builder(this).setTitle("你未能在规定时长内完成游戏")
                            .setNegativeButton("点击退出", (dialog, which) -> finish())
                            .setPositiveButton("重新开始", (dialog, which) -> {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
                            }).show();
                }
                timer++;
//                    timerHandler.postDelayed(this, 100);
            }
        };
//        timerHandler.postDelayed(timerRunnable, 0);
        scheduledExecutorService.scheduleWithFixedDelay(timerRunnable, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void drawLine(List<Integer> path) {
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(() -> {
            Log.d("click", path + "");
//            Log.d("click",clickedBoxId+"");
//            Log.d("click", String.valueOf(findViewById(path.get(1))==null));
            ImageButton box1 = findViewById(path.get(1));
            ImageButton box2 = findViewById(path.get(path.size() - 1));
            box1.setEnabled(false);
            box2.setEnabled(false);
            int[] box1Location = new int[2];
            int[] box2Location = new int[2];
            box1.getLocationInWindow(box1Location);
            box2.getLocationInWindow(box2Location);
            //划线
            FrameLayout playGround = findViewById(R.id.playground);
            ImageView lineBoard = new ImageView(this);
            lineBoard.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            //获取画布
            Bitmap bitmap = Bitmap.createBitmap(playGround.getWidth(), playGround.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
//            canvas.drawColor(Color.BLACK);
//            //画笔设置
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15);
//            Log.d("click", "1111");
            //获取坐标点
            float[] pts = new float[(path.size() - 2) * 4];
//            Log.d("click", "2222");
            int[] startAxis = new int[2];
            int[] endAxis = new int[2];
            findViewById(path.get(1)).getLocationInWindow(startAxis);
            findViewById(path.get(path.size() - 1)).getLocationInWindow(endAxis);
            pts[0] = startAxis[0] + (float) boxSize[0] / 2;
            pts[1] = startAxis[1] + (float) boxSize[1] / 2 - titleHeight;
            pts[pts.length - 2] = endAxis[0] + (float) boxSize[0] / 2;
            pts[pts.length - 1] = endAxis[1] + (float) boxSize[1] / 2 - titleHeight;
            if (path.size() == 4) {
                pts[2] = pts[0] + (float) (path.get(2) % 10 - path.get(1) % 10) * boxSize[0];
                pts[3] = pts[1] + (float) (path.get(2) / 10 - path.get(1) / 10) * boxSize[1];
                pts[pts.length - 4] = pts[pts.length - 2] + (float) (path.get(2) % 10 - path.get(3) % 10) * boxSize[0];
                pts[pts.length - 3] = pts[pts.length - 1] + (float) (path.get(2) / 10 - path.get(3) / 10) * boxSize[1];
            }
            if (path.size() == 5) {
                pts[2] = pts[0] + (float) (path.get(2) % 10 - path.get(1) % 10) * boxSize[0];
                pts[3] = pts[1] + (float) (path.get(2) / 10 - path.get(1) / 10) * boxSize[1];
                pts[4] = pts[2];
                pts[5] = pts[3];
                pts[pts.length - 6] = pts[pts.length - 2] + (float) (path.get(3) % 10 - path.get(4) % 10) * boxSize[0];
                pts[pts.length - 5] = pts[pts.length - 1] + (float) (path.get(3) / 10 - path.get(4) / 10) * boxSize[1];
                pts[pts.length - 4] = pts[pts.length - 6];
                pts[pts.length - 3] = pts[pts.length - 5];
            }
//            Log.d("click", "3333");

//            DrawLineView drawLineView = new DrawLineView(this,pts);
            Log.d("click", Arrays.toString(pts) + "//" + Arrays.toString(boxSize));
            //划线
//            canvas.drawLine(pts[0], pts[1], pts[pts.length - 2], pts[pts.length - 1], paint);
            canvas.drawLines(pts, paint);
//            Log.d("click", bitmap.getWidth()+"//"+bitmap.getHeight());
            lineBoard.setImageBitmap(bitmap);
//            lineBoard.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            playGround.addView(lineBoard);
//            playGround.addView(drawLineView);
            //方块消失
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            playGround.removeView(lineBoard);
            Handler uiHandler2 = new Handler(Looper.getMainLooper());
            uiHandler2.postDelayed(() -> {
                playGround.removeView(lineBoard);
                Bitmap bitmap1 = Bitmap.createBitmap(box1.getWidth(), box1.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas1 = new Canvas(bitmap1);
                canvas1.drawColor(Color.WHITE);
                box1.setImageBitmap(bitmap1);
                box2.setImageBitmap(bitmap1);
                box1.getBackground().setAlpha(0);
                box2.getBackground().setAlpha(0);
            }, 200);
            TextView txtGameScore = findViewById(R.id.txt_game_score);
            txtGameScore.setText("得分：" + score);
            gameBoardBoxesLogic.get(path.get(1) / 10).set(path.get(1) % 10, -1);
            gameBoardBoxesLogic.get(path.get(path.size() - 1) / 10).set(path.get(path.size() - 1) % 10, -1);
            if (score == 36) {
                BroadcastIntent(true);
                saveScore();
            }

        });
    }

    /**
     * 保存分数
     */
    public void saveScore() {
        startTimer = false;
//                scheduledExecutorService.shutdown();
        ContentValues values = new ContentValues();
        values.put("score", score);
        values.put("hard", hard);
        values.put("time", timer / 10);
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        Date date = new Date();
//        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String dateString = DateFormat.format(date);
        String dateString = localDateTime.format(dateTimeFormatter);
        Log.d("finishtime", dateString);
//        String dateString = date.toString();
        values.put("finishtime", dateString);
        EditText input = new EditText(this);
        input.setHint("请输入你的名字");
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setTextColor(Color.BLACK);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20), (source, start, end, dest, dstart, dend) -> {
            if (source.charAt(start) == ' ') return "";
            else return null;
        }});
        if (score == 36) new AlertDialog.Builder(this)
                .setTitle("恭喜你！")
                .setMessage("你完成了游戏！留下你的名字吧")
                .setView(input)
                .setPositiveButton("确定", (dialog, which) -> {
                    String name = input.getText().toString();
                    if (name.isEmpty()) name = "未命名";
                    values.put("name", name);
                    db.insert("leaderboard", null, values);
                    Intent intent = new Intent(GamePageActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("取消", (dialog, which) -> finish())
                .show();
        else new AlertDialog.Builder(this)
                .setTitle("游戏结束")
                .setMessage("你的分数为：" + score)
                .setView(input)
                .setPositiveButton("确定", (dialog, which) -> {
                    String name = input.getText().toString();
                    if (name.isEmpty()) name = "未命名";
                    values.put("name", name);
                    db.insert("leaderboard", null, values);
                    Intent intent = new Intent(GamePageActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("取消保存", (dialog, which) -> finish())
                .show();
    }

    /**
     * 获取路径
     *
     * @param start 起始点
     * @param end   结束点
     * @return 返回一个整型数列表，
     * 第一个元素代表路径类型：0代表无法连线或拐点数大于2，1代表相邻竖直线，2代表非相邻竖直线，3代表相邻水平线，4代表非相邻水平线,5代表一个拐点，6代表两个拐点
     * 第二个元素为起始点，
     * 最后一个元素为结束点，
     * 中间元素为拐点（拐点数量大于等于0小于等于二）
     */
    private List<Integer> getPath(int start, int end) {
        List<Integer> path = new ArrayList<>();
        //获取起始点和终点的索引值，第一个元素为竖轴索引值，设为x轴；第二个元素为横轴索引值，设为y轴
        int[] startPos = new int[]{start / 10, start % 10};
        int[] endPos = new int[]{end / 10, end % 10};
        path.add(0);
        path.add(start);
        path.add(end);
        Log.d("click", gameBoardBoxesLogic.get(startPos[0]).get(startPos[1]) + "//" + gameBoardBoxesLogic.get(endPos[0]).get(endPos[1]));
        //判断两个点颜色是否相等
        if (!Objects.equals(gameBoardBoxesLogic.get(startPos[0]).get(startPos[1]), gameBoardBoxesLogic.get(endPos[0]).get(endPos[1])))
            return path;
        //判断是否在同一水平线上
        if (startPos[0] == endPos[0]) {
            int absX = Math.abs(startPos[1] - endPos[1]);
            //判断是否水平相邻
            if (absX == 1) {
                path.clear();
                path.add(1);
                path.add(start);
                path.add(end);
                return path;
            } else {
                for (int i = 1; i < absX; i++) {
                    if (gameBoardBoxesLogic.get(startPos[0]).get(Math.min(startPos[1], endPos[1]) + i) == -1) {
                        if (i == absX - 1) {
                            path.clear();
                            path.add(2);
                            path.add(start);
                            path.add(end);
                            return path;
                        }
                    }else break;
                }
            }
        }
        //判断是否在同一竖直线上
        else if (startPos[1] == endPos[1]) {
            int absY = Math.abs(startPos[0] - endPos[0]);
            //判断是否竖直相邻
            if (absY == 1) {
                path.clear();
                path.add(3);
                path.add(start);
                path.add(end);
                return path;
            } else {
                for (int i = 1; i < absY; i++) {
                    if (gameBoardBoxesLogic.get(Math.min(startPos[0], endPos[0]) + i).get(startPos[1]) == -1) {
                        if (i == absY - 1) {
                            path.clear();
                            path.add(4);
                            path.add(start);
                            path.add(end);
                            return path;
                        }
                    }else break;
                }
            }
        }
        int onePointEnable = onePointEnable(start, end, Optional.empty());
        if (onePointEnable != -1) {
            path.clear();
            path.add(5);
            path.add(start);
            path.add(onePointEnable);
            path.add(end);
            return path;
        } else {
            List<Integer> twoPointEnable = twoPointEnable(start, end);
            if (twoPointEnable != null) {
                path.clear();
                path.add(6);
                path.add(start);
                path.addAll(twoPointEnable);
                path.add(end);
                return path;
            }
        }
        return path;
    }

    /**
     * 判断两个点.是否存在一个拐点的路径
     *
     * @param start   整型类型，起始点的索引值
     * @param end     整型类型，结束点的索引值
     * @param optMode 整型类型，判断模式。0代表默认模式，1代表竖直模式，2代表水平模式
     * @return 返回一个整型数，-1代表不存在一个拐点的路径，反之则为拐点逻辑索引值
     */
    private int onePointEnable(int start, int end, Optional<Integer> optMode) {
        int[] pointPos = {0, 0};
        int[] result = {0, 0, 0, 0};
        int mode = optMode.orElse(0);
        //将UI索引值转为逻辑索引值
        int[] startPos = new int[]{start / 10, start % 10};
        int[] endPos = new int[]{end / 10, end % 10};
        //以起始点竖直寻找
        int abs = Math.abs(startPos[0] - endPos[0]);
        boolean b = abs == 1;
        int abs1 = Math.abs(startPos[1] - endPos[1]);
        if (mode != 2) {
            if (gameBoardBoxesLogic.get(endPos[0]).get(startPos[1]) == -1) {
                pointPos[0] = endPos[0];
                pointPos[1] = startPos[1];
                if (b) {
                    result[0] = 1;
//                    break;
                }
                if (result[0] == 0) {
                    for (int i = 1; i < abs + 1; i++) {

                        if (gameBoardBoxesLogic.get(Math.min(startPos[0], endPos[0]) + i).get(pointPos[1]) != -1) {
                            result[0] = 0;
                            break;
                        } else result[0] = 1;
                    }
                }
                if (result[0] == 1) {
                    if (abs1 == 1) {
                        result[1] = 1;
//                        break;
                    }
                    if (result[1] == 0) {
                        for (int i = 1; i < abs1; i++) {
                            if (gameBoardBoxesLogic.get(pointPos[0]).get(Math.min(startPos[1], endPos[1]) + i) != -1) {
                                result[1] = 0;
                                break;
                            } else result[1] = 1;
                        }
                    }
                }
                if (result[0] == 1 && result[1] == 1) return pointPos[0] * 10 + pointPos[1];
            }
        }
        //以起始点水平寻找
        if (mode != 1) {
            if (result[0] == 0) {
                if (gameBoardBoxesLogic.get(startPos[0]).get(endPos[1]) == -1) {
                    pointPos[0] = startPos[0];
                    pointPos[1] = endPos[1];
                    if (abs1 == 1) {
                        result[2] = 1;
                    }
                    if (result[2] == 0) {
                        for (int i = 1; i < abs1; i++) {

                            if (gameBoardBoxesLogic.get(pointPos[0]).get(Math.min(startPos[1], endPos[1]) + i) != -1) {
                                result[2] = 0;
                                break;
                            } else result[2] = 1;
                        }
                    }
                    if (result[2] == 1) {
                        if (b) {
                            result[3] = 1;
                        }
                        if (result[3] == 0) {
                            for (int i = 1; i < abs; i++) {
//                                Log.d("clickOneHor", Arrays.toString(startPos) + "//" + Arrays.toString(endPos) + "//" + Arrays.toString(pointPos));
                                if (gameBoardBoxesLogic.get(Math.min(startPos[0], endPos[0]) + i).get(pointPos[1]) != -1) {
                                    result[3] = 0;
                                    break;
                                } else result[3] = 1;
                            }
                        }
                    }
//                    Log.d("clickOneHor", result[0] + "// " + result[1] + "// " + result[2] + "// " + result[3]);
                    if (result[2] == 1 && result[3] == 1) return pointPos[0] * 10 + pointPos[1];
                }
            }
        }
        return -1;
    }

    /**
     * 判断两个点.是否存在两个拐点路径
     *
     * @param start 整型类型，起始点的索引值
     * @param end   整型类型，结束点的索引值
     * @return 返回一个整型列表，null代表不存在两个拐点路径，反之则为拐点逻辑索引值
     */
    public List<Integer> twoPointEnable(int start, int end) {
        int[] startPos = new int[]{start / 10, start % 10};
        List<Integer> points = new ArrayList<>();
        //以起始点竖直向上寻找
        for (int i = 1; i < startPos[0] + 1; i++) {
//            Log.d("clickTwoUp", "0");
            if (gameBoardBoxesLogic.get(startPos[0] - i).get(startPos[1]) != -1) {
                break;
            } else {
                int tempStart = (startPos[0] - i) * 10 + startPos[1];
                int tempPoint = onePointEnable(tempStart, end, Optional.of(2));
//                Log.d("clickTwoUp",gameBoardBoxesLogic+"");
//                Log.d("clickTwoUp", "2:" + tempPoint + "//" + tempStart + "//" + start);
                if (tempPoint != -1) {
                    points.add(tempStart);
                    points.add(tempPoint);
                    return points;
                }
            }
        }
        //以起始点竖直向下寻找
        for (int i = 1; i < 11 - startPos[0]; i++) {
            if (gameBoardBoxesLogic.get(startPos[0] + i).get(startPos[1]) != -1) {
//                Log.d("clickTwoDown", "1");
                break;
            } else {
                int tempStart = (startPos[0] + i) * 10 + startPos[1];
                int tempPoint = onePointEnable(tempStart, end, Optional.of(2));
                if (tempPoint != -1) {
                    points.add(tempStart);
                    points.add(tempPoint);
                    return points;
                }
            }
        }
        //以起始点水平向左寻找
        for (int i = 1; i < startPos[1] + 1; i++) {
            if (gameBoardBoxesLogic.get(startPos[0]).get(startPos[1] - i) != -1) {
//                Log.d("clickTwoLeft", "1");
                break;
            } else {
                int tempStart = startPos[0] * 10 + startPos[1] - i;
                int tempPoint = onePointEnable(tempStart, end, Optional.of(1));
                if (tempPoint != -1) {
                    points.add(tempStart);
                    points.add(tempPoint);
                    return points;
                }
            }
        }
        //以起始点水平向右寻找
        for (int i = 1; i < 10 - startPos[1]; i++) {
            if (gameBoardBoxesLogic.get(startPos[0]).get(startPos[1] + i) != -1) {
//                Log.d("clickTwoRight", "1");
                break;
            } else {
                int tempStart = startPos[0] * 10 + startPos[1] + i;
                int tempPoint = onePointEnable(tempStart, end, Optional.of(1));
                if (tempPoint != -1) {
                    points.add(tempStart);
                    points.add(tempPoint);
                    return points;
                }
            }
        }
        return null;
    }
}
