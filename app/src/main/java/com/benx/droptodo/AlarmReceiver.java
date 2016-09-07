package com.benx.droptodo;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Project: DropTODO
 * Author:  Ben.X
 * Date:    2016/9/4
 */

public class AlarmReceiver extends BroadcastReceiver {

    private String TAG = "getin";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "onReceive: received alarm");

        String action = intent.getAction();
        String title = intent.getStringExtra("title");
        String item1 = intent.getStringExtra("item1");
        String item2 = intent.getStringExtra("item2");
        String item3 = intent.getStringExtra("item3");

        if (action.equals("cancel_alarm")) {
            return;
        } else {

            Log.d(TAG, "onReceive: operate alarm: "+title);

            Intent intent1 = new Intent(context,MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context,1,intent1,0);

            NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(item1 + "\n" + item2 + "\n" + item3 + "\n" + "    from DropTODO")
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)//设置可以清除
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pIntent)
                    .build();
            manager.notify(6,notification);

        }

    }
}
