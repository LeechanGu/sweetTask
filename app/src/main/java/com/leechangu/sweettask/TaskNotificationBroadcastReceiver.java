package com.leechangu.sweettask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * Created by Administrator on 2015/10/27.
 */
public class TaskNotificationBroadcastReceiver extends BroadcastReceiver {
    public static final int NOTIF_ID = 56;
    @Override
    public void onReceive(Context context, Intent intent) {
        // set the next alarm
        Intent mathAlarmServiceIntent = new Intent(
                context,
                TaskNotificationUpdateBroadcastReceiver.class);
        context.sendBroadcast(mathAlarmServiceIntent, null);

        // get extra
        String val = intent.getStringExtra("taskName");

        //createNotification(val);
        createNotification(context,val);
    }


    // Construct compatible notification
    private void createNotification(Context context,String val) {
        Log.i("LogInfo", this.getClass().getSimpleName() + ".createNotification()");
        // Construct pending intent to serve as action for notification item
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("message", "Check if the task have been finished");
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
        // Create notification
        String longText = "It will be due for task - "+val +" in 10 mins";
        Notification noti =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("From SweeTask")
                        .setContentText(longText)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(longText))
                        .setContentIntent(pIntent)
                        .build();

        // Hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NOTIF_ID, noti);
    }

}
