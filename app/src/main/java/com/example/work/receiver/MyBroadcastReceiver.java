package com.example.work.receiver;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.work.R;


public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isFinish = intent.getBooleanExtra("mode", false);
        String channelId;
        CharSequence channelName = "连连看";
        String channelDescription;
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        if (isFinish) {
            Toast.makeText(context, "游戏结束", Toast.LENGTH_SHORT).show();
            channelId = "1";
            channelDescription = "游戏结束";
        } else {
            Toast.makeText(context, "游戏失败", Toast.LENGTH_SHORT).show();
            channelId = "2";
            channelDescription = "游戏失败";
        }
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setDescription(channelDescription);
        NotificationManager manager = getSystemService(context, NotificationManager.class);
        if (manager != null) {
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder notification = new NotificationCompat.Builder(context, channelId)
                .setContentTitle("连连看")
                .setContentText(isFinish ? "游戏结束111111111" : "游戏失败")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        if (manager != null) {
            manager.notify(1, notification.build());
        }
////            Notification notification = new NotificationCompat.Builder(context, channelId)
////                    .setContentTitle("连连看")
////                    .setContentText(isFinish ? "游戏结束" : "游戏失败")
////                    .setWhen(System.currentTimeMillis())
////                    .setSmallIcon(R.mipmap.ic_launcher)
////                    .setAutoCancel(true)
////                    .build();
////            manager.notify(1, notification);
//        }
    }
}
