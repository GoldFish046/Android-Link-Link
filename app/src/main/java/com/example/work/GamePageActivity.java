package com.example.work;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.work.utils.MyRandom;

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
    private int timer = 0;
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
    int[] boxSize;
    //左上角方块左上角相对于组件playground的坐标
    int[] boxAxis;
    //标题栏高度
    int titleHeight;
    //线程池
    private ExecutorService executorService;
    //计时器
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gamepage);
        txtGameTime = findViewById(R.id.txt_game_time);
        findViewById(R.id.btn_back).setOnClickListener(this);
        executorService = Executors.newFixedThreadPool(1);
        scheduledExecutorService = Executors.newScheduledThreadPool(1);


        //初始化数据
        initData();
        //计时器
        timer();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        scheduledExecutorService.shutdown();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            startTimer = false;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("确定退出吗，当前游戏进度不会被记录");
            builder.setPositiveButton("确定", (dialog, which) -> finish());
            builder.setNegativeButton("取消", (dialog, which) -> {
                startTimer = true;
//                timerHandler.postDelayed(timerRunnable, 0);
            });
            builder.create().show();
        } else executorService.execute(() -> {
            if (clickedBoxId == -1) {
                clickedBoxId = v.getId();
            } else if (clickedBoxId != v.getId()) {
                List<Integer> path = getPath(clickedBoxId, v.getId());
                if (path.get(0) != 0) {
                    Log.d("click",v.getId()+"//"+clickedBoxId);
                    score++;
                    drawLine(path);
                }
//                findViewById(clickedBoxId).setEnabled(true);
                clickedBoxId = -1;
            }
        });

    }

    /**
     * 初始化数据
     */

    private void initData() {
        executorService.submit(() -> {
            score = 0;
            boxColors = new int[]{R.color.box1, R.color.box2, R.color.box3, R.color.box4, R.color.box5, R.color.box6, R.color.box7, R.color.box8, R.color.box9, R.color.box10
                    , R.color.box11, R.color.box12, R.color.box13, R.color.box14, R.color.box15, R.color.box16, R.color.box17, R.color.box18, R.color.box19, R.color.box20};
            //随机选取的颜色种类
            List<Integer> selectColors = new ArrayList<>();
            //获取难度
            Intent intent = getIntent();
            String difficulty = intent.getStringExtra("difficulty");
            if (difficulty != null) {
                switch (difficulty) {
                    case "easy":
                        selectColors = MyRandom.selectColors(10);
                        break;
                    case "normal":
                        selectColors = MyRandom.selectColors(15);
                        break;
                    case "hard":
                        selectColors = MyRandom.selectColors(20);
                        break;
                }
            } else {
                Toast.makeText(this, "请选择游戏难度", Toast.LENGTH_SHORT).show();
                finish();
            }
            //各种颜色所有的方块数（随机）
            Map<String, Integer> colorNumbers = MyRandom.colorNumbers(selectColors);
            //二维数组用于存放各种颜色的位置（随机）
            gameBoardBoxes = MyRandom.dislocate(selectColors, colorNumbers);
            gameBoardBoxesLogic = MyRandom.getDislocateLogic(new ArrayList<>(gameBoardBoxes));
            Log.d("gameBoardBoxes", gameBoardBoxes.toString());
            Log.d("gameBoardBoxesLogic", gameBoardBoxesLogic.toString());
            initView();
        });
    }

    /**
     * 初始化UI
     */
    private void initView() {
        Handler uiHandler = new Handler(Looper.getMainLooper());
        uiHandler.post(() -> {
            //生成游戏面板
            LinearLayout gameBoard = findViewById(R.id.game_board);
            LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            btnParams.setMargins(10, 10, 10, 10);
            for (int i = 1; i < 10; i++) {
                LinearLayout row = new LinearLayout(this);
                row.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
                row.setId(i);
                row.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 1; j < 9; j++) {
                    ImageButton imageButton = new ImageButton(this);
                    imageButton.setId(i * 10 + j);
                    imageButton.setLayoutParams(btnParams);
                    imageButton.setBackgroundColor(ContextCompat.getColor(this, boxColors[gameBoardBoxes.get(i - 1).get(j - 1)]));
                    imageButton.setOnClickListener(this);
                    row.addView(imageButton);
                }
                gameBoard.addView(row);
            }
            TextView txtGameScore = findViewById(R.id.txt_game_score);
            txtGameScore.setText("得分：" + score);
//            Log.d("Size", findViewById(R.id.gamePage).getHeight() + "//" + findViewById(R.id.title).getHeight() + "//" + findViewById(R.id.playground).getHeight());
            int temp = 11;
            boxSize = new int[]{findViewById(temp).getWidth(), findViewById(temp).getHeight()};
            titleHeight = findViewById(R.id.title).getHeight();
            boxAxis = new int[2];
            findViewById(temp).getLocationInWindow(boxAxis);
            boxAxis[1] -= titleHeight;
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
                txtGameTime.setText("游戏时间：" + timer / 10.0 + "秒");
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
            Log.d("click",path+"");
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
            //获取画布
            Bitmap bitmap = Bitmap.createBitmap(playGround.getWidth(), playGround.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            //画笔设置
            Paint paint = new Paint();
            paint.setColor(ContextCompat.getColor(this, R.color.black));
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(5);

            //获取坐标点
            float[] pts = new float[(path.size() - 1) * 2];
            for (int i = 1; i < path.size(); i++) {
                float posX;
                float posY;
                posX = (float) ((path.get(i) % 10 - 1) * 2 + 1) / 2 * boxSize[0]+boxAxis[0];
                posY = (float) ((path.get(i) / 10 - 1) * 2 + 1) / 2 * boxSize[1]+boxAxis[1];
                pts[i * 2 - 2] = posX;
                pts[i * 2 - 1] = posY;
            }
            //划线
            canvas.drawLines(pts, paint);
            lineBoard.setImageBitmap(bitmap);
//            lineBoard.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
            playGround.addView(lineBoard);
            //方块消失
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            playGround.removeView(lineBoard);
            box1.getBackground().setAlpha(0);
            box2.getBackground().setAlpha(0);
            TextView txtGameScore = findViewById(R.id.txt_game_score);
            txtGameScore.setText("得分：" + score);
            gameBoardBoxesLogic.get(path.get(1) / 10).set(path.get(1) % 10, -1);
            gameBoardBoxesLogic.get(path.get(path.size()-1) / 10).set(path.get(path.size()-1) % 10, -1);
        });
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
                    }
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
                    }
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
