package com.leechangu.sweettask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

//import com.leechangu.sweettask.db.TaskDb;

import com.parse.ParseUser;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class TaskNotificationUpdateService extends Service {
    public TaskNotificationUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private ParseTaskItem getEarliestNext(){
        Set<ParseTaskItem> taskQueue = new TreeSet<ParseTaskItem>(new Comparator<ParseTaskItem>() {
            @Override
            public int compare(ParseTaskItem lhs, ParseTaskItem rhs) {
                int result = 0;
                long diff = lhs.calculateNextAlarmTime().getTimeInMillis() - rhs.calculateNextAlarmTime().getTimeInMillis();
                if(diff>0){
                    return 1;
                }else if (diff < 0){
                    return -1;
                }
                return result;
            }
        }); //in order to get the earliest one

//        TaskDb.init(getApplicationContext());
//        List<TaskItem> taskItems = TaskDb.getAll();
        ParseUser currentUser = ParseUser.getCurrentUser();
        List<ParseTaskItem> parseTaskItems = ParseTaskItemRepository.
                getAllParseTasksFromParseByUserName(currentUser.getUsername());

        for(ParseTaskItem taskItem : parseTaskItems){
            if(taskItem.isActive())
                taskQueue.add(taskItem);
        }
        if(taskQueue.iterator().hasNext()){
            return taskQueue.iterator().next();
        }else{
            return null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LogInfo",this.getClass().getSimpleName()+".onStartCommand()");
        ParseTaskItem taskItem = getEarliestNext();
        if(null != taskItem){
            Log.i("LogInfo",this.getClass().getSimpleName()+".onStartCommand(), null != taskItem");
            taskItem.schedule(getApplicationContext());
        }else{
            Log.i("LogInfo",this.getClass().getSimpleName()+".onStartCommand(), null == taskItem");
            Intent myIntent = new Intent(getApplicationContext(), TaskNotificationBroadcastReceiver.class);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT); // it identify the same type of myIntent
            AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);

            alarmManager.cancel(pendingIntent);
        }

        return super.onStartCommand(intent, flags, startId);
    }
}
