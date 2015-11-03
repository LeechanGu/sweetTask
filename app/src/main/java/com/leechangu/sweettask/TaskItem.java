package com.leechangu.sweettask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by Administrator on 2015/10/2.
 */
public class TaskItem implements Serializable {
    public enum TimeBasisEnum {
        DAILY,WEEKLY, MONTHLY
    }

    private static final long serialVersionUID = 8699489847426803789L;
    private int id = -1;
    private String taskContent = "undefined";
    private TimeBasisEnum timeBasis = TimeBasisEnum.DAILY;
    private Calendar alarmTime = Calendar.getInstance();
    private boolean vibrate = true;
    private boolean active = true;
    private boolean isPhotoTask = false; // The new Task is not a photo task by default
    private boolean finished = false;
    private String mapInfo = null;
    private String alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();
    private boolean isPhotoTaskFinished = false;
    private boolean isMapTaskFinished = false;


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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAlarmTime(Calendar alarmTime) {
        this.alarmTime = alarmTime;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public void setActive(boolean activated) {
        this.active = activated;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
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

    public TaskItem() {
    }

    public void setTimeBasis(TimeBasisEnum timeBasis) {
        this.timeBasis = timeBasis;
    }

    public Calendar getAlarmTime() {
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

    public TaskItem.TimeBasisEnum getTimeBasisEnum()
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
        alarmManager.set(AlarmManager.RTC_WAKEUP, this.getAlarmTime().getTimeInMillis()-10*60*1000,pendingIntent);//getAlarmTime().getTimeInMillis()
    }


    public String getTimeUntilNextAlarmMessage(){
        long timeDifference = getAlarmTime().getTimeInMillis() - System.currentTimeMillis();
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
