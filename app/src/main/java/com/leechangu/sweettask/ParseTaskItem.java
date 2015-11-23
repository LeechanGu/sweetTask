package com.leechangu.sweettask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by CharlesGao on 15-11-17.
 *
 * Function: This is an entity class that will replace {@link TaskItem}
 */
public class ParseTaskItem implements Serializable {


    public enum TimeBasisEnum {
        DAILY, WEEKLY, MONTHLY
    }

    private static final long serialVersionUID = 8699489847426803789L;
    private String id = null; // This Id is the ObjectId from Parse
    private String taskContent = "undefined";
    private TimeBasisEnum timeBasis = TimeBasisEnum.DAILY;
    private Calendar alarmTime = Calendar.getInstance();
    private boolean vibrate = true;
    private boolean active = true;
    private boolean isPhotoTask = false; // The new Task is not a photo task by default
    private boolean isMapTask = false;
    private boolean ifAllTasksFinished = false;
    private String mapInfo = "";
//    private String belonger;

    private String alarmTonePath = "";//RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();

    private boolean isPhotoTaskFinished = false;
    private boolean isMapTaskFinished = false;

//    public String getBelonger() {
//        return belonger;
//    }
//
//    public void setBelonger(String belonger) {
//        this.belonger = belonger;
//    }



    public boolean isMapTask() {
        return isMapTask;
    }

    public void setIsMapTask(boolean isMapTask) {
        this.isMapTask = isMapTask;
    }


    public boolean isPhotoTaskFinished() {
        return isPhotoTaskFinished;
    }

    public void setIsPhotoTaskFinished(boolean isPhotoTaskFinished) {
        this.isPhotoTaskFinished = isPhotoTaskFinished;
    }

    public boolean isMapTaskFinished() {
        return isMapTaskFinished;
    }

    public void setIsMapTaskFinished(boolean isMapTaskFinished) {
        this.isMapTaskFinished = isMapTaskFinished;
    }

    public boolean isPhotoTask() {
        return isPhotoTask;
    }

    public void setIsPhotoTask(boolean isPhotoTask) {
        this.isPhotoTask = isPhotoTask;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAlarmTime(Calendar alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Calendar getAlarmTime() {
        return alarmTime;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public void setActive(boolean activated) {
        this.active = activated;
    }

    public boolean ifAllTasksFinished() {
        return ifAllTasksFinished;
    }

    public void setIfAllTasksFinished(boolean ifAllTasksFinished) {
        this.ifAllTasksFinished = ifAllTasksFinished;
    }

    public String getAlarmTonePath() {
        return alarmTonePath;
    }

    public void setAlarmTonePath(String alarmTonePath) {
        this.alarmTonePath = alarmTonePath;
    }

    public String getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(String mapInfo) {
        this.mapInfo = mapInfo;
    }

    public ParseTaskItem() {
    }

    public void setTimeBasis(TimeBasisEnum timeBasis) {
        this.timeBasis = timeBasis;
    }

    public Calendar calculateNextAlarmTime() {
        alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.MILLISECOND,0);
        alarmTime.set(Calendar.SECOND,0);
        alarmTime.set(Calendar.MINUTE,0);
        alarmTime.set(Calendar.HOUR_OF_DAY,0);
        switch (timeBasis)
        {
            case DAILY:
                alarmTime.add(Calendar.DATE, 1);
                break;
            case WEEKLY:
                alarmTime.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
                alarmTime.add(Calendar.DATE, 7);
                break;
            case MONTHLY:
                alarmTime.set(Calendar.DAY_OF_MONTH,1);
                alarmTime.add(Calendar.MONTH, 1);
                break;
        }
        return alarmTime;
    }


    public void setContent(String taskContent)
    {
        this.taskContent =taskContent ;
    }


    public String getContent()
    {
        return taskContent;
    }

    public ParseTaskItem.TimeBasisEnum getTimeBasisEnum()
    {
        return timeBasis;
    }


    public boolean isVibrate() {
        return vibrate;
    }

    public boolean isActive() {
        return active;
    }

    public void schedule(Context context) {
        Log.i("LogInfo", this.getClass().getSimpleName() + ".schedule()");
        setActive(true);
        Intent myIntent = new Intent(context, TaskNotificationBroadcastReceiver.class);
        myIntent.putExtra("taskName",this.getContent());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, this.calculateNextAlarmTime().getTimeInMillis() - 10 * 60 * 1000, pendingIntent);
    }


    public String getTimeUntilNextAlarmMessage(){
        long timeDifference = calculateNextAlarmTime().getTimeInMillis() - System.currentTimeMillis();
        long days = timeDifference / (1000 * 60 * 60 * 24);
        long hours = timeDifference / (1000 * 60 * 60) - (days * 24);
        long minutes = timeDifference / (1000 * 60) - (days * 24 * 60) - (hours * 60);
        long seconds = timeDifference / (1000) - (days * 24 * 60 * 60) - (hours * 60 * 60) - (minutes * 60);
        String alert = "Alarm will sound in ";
        if (days > 0) {
            alert += String.format(
                    "%d days, %d hours, %d minutes and %d seconds", days,
                    hours, minutes, seconds);
        } else {
            if (hours > 0) {
                alert += String.format("%d hours, %d minutes and %d seconds",
                        hours, minutes, seconds);
            } else {
                if (minutes > 0) {
                    alert += String.format("%d minutes, %d seconds", minutes,
                            seconds);
                } else {
                    alert += String.format("%d seconds", seconds);
                }
            }
        }
        return alert;
    }



}
