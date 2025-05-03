package com.example.work.entity;


import androidx.annotation.NonNull;

public class LeaderBoard {

    private int id;
    private String name;
    private int score;
    private int hard;
    private String finishtime;
    private float time;

    public LeaderBoard(int id, String name, int score, int hard, String finishtime, float time) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.hard = hard;
        this.finishtime = finishtime;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getHard() {
        return hard;
    }

    public void setHard(int hard) {
        this.hard = hard;
    }

    public String getFinishtime() {
        return finishtime;
    }

    public void setFinishtime(String finishtime) {
        this.finishtime = finishtime;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
    @NonNull
    @Override
    public String toString() {
        return "LeaderBoard{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", hard=" + hard +
                ", finishtime='" + finishtime + '\'' +
                ", time=" + time +
                '}';
    }
}
