package com.leechangu.sweettask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class TaskNotificationUpdateBroadcastReceiver extends BroadcastReceiver {
    public TaskNotificationUpdateBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("LogInfo", this.getClass().getSimpleName() + ".onReceive()");
        Bundle bundle = intent.getExtras();
        //final TaskItem taskItem = (TaskItem) bundle.getSerializable("taskItem");
        // if it is finished
        /* {
        // TODO: write to the calendar that it's not finished
        // TODO: give a reminder that it is not finished
        // }
        */
        Intent i = new Intent(context, TaskNotificationUpdateService.class);
        //ResultReceiver receiver = intent.getParcelableExtra("receiver");
        //i.putExtra("taskName", taskItem.getContent());
        context.startService(i);
    }
}
