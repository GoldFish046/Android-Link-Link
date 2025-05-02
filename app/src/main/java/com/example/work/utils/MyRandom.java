package com.example.work.utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MyRandom {
    public static List<Integer> selectColors(int num) {
        Random random = new Random();
        List<Integer> list = new ArrayList<>();
        List<Integer> apartList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        if (num < 20) {
            for (int i = 0; i < 20 - num; i++) {
                int temp = random.nextInt(20);
                if (!apartList.contains(temp)) {
                    apartList.add(temp);
                } else {
                    i--;
                }
            }
        }
        for(int i=0;i<apartList.size();i++){
            list.remove(apartList.get(i));
        }
        return list;
    }

    public static Map<String, Integer> colorNumbers(List<Integer> targetList) {
        Map<String, Integer> map = new HashMap<>();
        Random random = new Random();
        for (int i = 0; i < targetList.size(); i++) {
            map.put("box" + targetList.get(i), 36 / targetList.size());
        }
        for (int i = 0; i < 36 % targetList.size(); i++) {
            int temp = random.nextInt(targetList.size());
            if (!(map.get("box" + targetList.get(temp)) > 36 / targetList.size() + 2)) {
                map.put("box" + targetList.get(temp), map.get("box" + targetList.get(temp)) + 1);
            } else i--;
        }
        for (int i = 0; i < targetList.size(); i++) {
            map.put("box" + targetList.get(i), map.get("box" + targetList.get(i)) * 2);
        }
        return map;
    }

    public static List<List<Integer>> dislocate(List<Integer> targetList, Map<String, Integer> targetMap) {
        List<List<Integer>> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            List<Integer> tempList = new ArrayList<>();
            for (int j = 0; j < 8; j++) {
                int temp = random.nextInt(targetList.size());
                if (targetMap.get("box" + targetList.get(temp)) > 0) {
                    tempList.add(targetList.get(temp));
                    targetMap.put("box" + targetList.get(temp), targetMap.get("box" + targetList.get(temp)) - 1);
                } else j--;
            }
            list.add(tempList);
        }
        return list;
    }

    public static List<List<Integer>> getDislocateLogic(List<List<Integer>> target) {
        List<List<Integer>> list = new ArrayList<>();
        List<Integer> tempList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tempList.add(-1);
        }
        list.add(tempList);
        for(int i=0;i<target.size();i++){
            List<Integer> temp=new ArrayList<>();
            temp.add(-1);
            for(int tempInt:target.get(i)){
                temp.add(tempInt);
            }
            temp.add(-1);
            list.add(temp);
        }
        list.add(tempList);
        return list;
    }
}
