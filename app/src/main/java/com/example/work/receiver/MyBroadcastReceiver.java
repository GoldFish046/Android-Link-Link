package com.example.work.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.work.R;


public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isFinish = intent.getBooleanExtra("mode", false);
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId;
        CharSequence channelName = "连连看";
        String channelDescription;

        if (isFinish) {
            Toast.makeText(context, "游戏结束", Toast.LENGTH_SHORT).show();
            channelId = "1";
            channelDescription = "游戏结束";
        } else {
            Toast.makeText(context, "游戏失败", Toast.LENGTH_SHORT).show();
            channelId = "2";
            channelDescription = "游戏失败";
        }
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription(channelDescription);
        manager.createNotificationChannel(channel);
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED)
            return;
        else {
            Notification notification = new NotificationCompat.Builder(context, channelId)
                    .setContentTitle("连连看")
                    .setContentText(isFinish ? "游戏结束" : "游戏失败")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .build();
            manager.notify(1, notification);
        }
    }
}
