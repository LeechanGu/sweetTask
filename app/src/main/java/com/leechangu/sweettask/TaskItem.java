package com.leechangu.sweettask;

import android.media.RingtoneManager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2015/10/2.
 */
public class TaskItem implements Serializable {

    private static final long serialVersionUID = 8699489847426803789L;
    private int id = -1;
    private String taskContent = "undefined";
    private TimeBasisEnum timeBasis = TimeBasisEnum.DAILY;
    private TaskTypeEnum taskType = TaskTypeEnum.COMMON_TASK;
    private Calendar alarmTime = Calendar.getInstance();
    private boolean vibrate = true;
    private boolean activated = true;
    private String mapInfo = null;
    private String alarmTonePath = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString();

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
        this.activated = activated;
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

    TaskItem(String taskContent, TimeBasisEnum timeBasis, TaskTypeEnum taskType) {
        this.taskContent = taskContent;
        this.timeBasis = timeBasis;
        this.taskType = taskType;
    }

    TaskItem(String taskContent, TimeBasisEnum timeBasis, TaskTypeEnum taskType, long created, long modified) {
        this.taskContent = taskContent;
        this.timeBasis = timeBasis;
        this.taskType = taskType;
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

    public void setTaskType(TaskTypeEnum taskType)
    {
        this.taskType =taskType ;
    }


    public String getContent()
    {
        return taskContent;
    }

    public TimeBasisEnum getTimeBasisEnum()
    {
        return timeBasis;
    }

    public TaskTypeEnum getTaskType()
    {
        return taskType;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public boolean isActive() {
        return activated;
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
