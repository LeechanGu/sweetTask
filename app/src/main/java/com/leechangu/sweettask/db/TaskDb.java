package com.leechangu.sweettask.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.leechangu.sweettask.TaskItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2015/10/15.
 */

public class TaskDb extends SQLiteOpenHelper {
    static TaskDb instance = null;
    static SQLiteDatabase database = null;

    static final String DATABASE_NAME = "DB";
    static final int DATABASE_VERSION = 7;

    public static final String TASK_TABLE = "tasks";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ALARM_TIME = "alarm_time";
    public static final String COLUMN_ACTIVE = "active";
    public static final String COLUMN_REPEAT = "repeat";
    public static final String COLUMN_TONE_PATH = "task_tone";
    public static final String COLUMN_VIBRATE = "task_vibrate";
    public static final String COLUMN_CONTENT = "task_content";
    public static final String COLUMN_MAP = "task_map";
    public static final String COLUMN_PHOTO = "task_photo";
    public static final String COLUMN_FINISHED = "task_finished";
    public static final String COLUMN_COMPLETE_DATE = "complete_date";

    private static final String[] columns = new String[] {
            COLUMN_ID,
            COLUMN_ALARM_TIME,
            COLUMN_ACTIVE,
            COLUMN_REPEAT,
            COLUMN_TONE_PATH,
            COLUMN_VIBRATE,
            COLUMN_CONTENT,
            COLUMN_MAP,
            COLUMN_PHOTO,
            COLUMN_FINISHED,
            COLUMN_COMPLETE_DATE
    };

    public static void init(Context context) {
        if (null == instance) {
            instance = new TaskDb(context);
        }
    }

    public static SQLiteDatabase getDatabase() {
        if (null == database) {
            database = instance.getWritableDatabase();
        }
        return database;
    }

    public static void deactivate() {
        if (null != database && database.isOpen()) {
            database.close();
        }
        database = null;
        instance = null;
    }

    public static long create(TaskItem taskItem) {
        ContentValues cv = taskItem2ContentValue(taskItem);
        return getDatabase().insert(TASK_TABLE, null, cv);
    }
    public static int update(TaskItem taskItem) {
        ContentValues cv = taskItem2ContentValue(taskItem);
        return getDatabase().update(TASK_TABLE, cv, "_id=" + taskItem.getId(), null);
    }

    private static ContentValues taskItem2ContentValue(TaskItem taskItem)
    {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_ACTIVE, taskItem.isActive());
        cv.put(COLUMN_ALARM_TIME, taskItem.calculateNextAlarmTime().getTimeInMillis());
        cv.put(COLUMN_TONE_PATH, taskItem.getAlarmTonePath());
        cv.put(COLUMN_REPEAT, taskItem.getTimeBasisEnum().toString());
        cv.put(COLUMN_VIBRATE, taskItem.isVibrate());
        cv.put(COLUMN_CONTENT, taskItem.getContent());
        cv.put(COLUMN_MAP,taskItem.getMapInfo());
        cv.put(COLUMN_PHOTO, taskItem.isPhotoTask());
        cv.put(COLUMN_FINISHED,taskItem.isFinished());
        cv.put(COLUMN_COMPLETE_DATE, taskItem.getCompleteDatesAsString());
        return cv;
    }
    public static int deleteEntry(TaskItem taskItem){
        return deleteEntry(taskItem.getId());
    }

    public static int deleteEntry(int id){
        return getDatabase().delete(TASK_TABLE, COLUMN_ID + "=" + id, null);
    }

    public static int deleteAll(){
        return getDatabase().delete(TASK_TABLE, "1", null);
    }

    public static TaskItem getTask(int id) {
        Cursor c = getDatabase().query(TASK_TABLE, columns, COLUMN_ID+"="+id, null, null, null,
                null);
        TaskItem taskItem = null;

        if(c.moveToFirst()){
            taskItem = getTaskFromCursor(c);
        }
        c.close();
        return taskItem;
    }

    private static TaskItem getTaskFromCursor(Cursor cursor)
    {
        TaskItem taskItem = new TaskItem();
        taskItem.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
        taskItem.setActive(cursor.getInt(cursor.getColumnIndex(COLUMN_ACTIVE)) == 1);
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(cursor.getLong(cursor.getColumnIndex(COLUMN_ALARM_TIME)));
        taskItem.setAlarmTime(calendar);
        taskItem.setTimeBasis(TaskItem.TimeBasisEnum.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_REPEAT))));
        taskItem.setAlarmTonePath(cursor.getString(cursor.getColumnIndex(COLUMN_TONE_PATH)));
        taskItem.setVibrate(cursor.getInt(cursor.getColumnIndex(COLUMN_VIBRATE)) == 1);
        taskItem.setContent(cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
        taskItem.setMapInfo(cursor.getString(cursor.getColumnIndex(COLUMN_MAP)));
        taskItem.setIsPhotoTask(cursor.getInt(cursor.getColumnIndex(COLUMN_PHOTO)) == 1);
        taskItem.setFinished(cursor.getInt(cursor.getColumnIndex(COLUMN_FINISHED)) == 1);
        taskItem.setCompleteDatesByString(cursor.getString(cursor.getColumnIndex(COLUMN_COMPLETE_DATE)));
        return taskItem;
    }

    public static Cursor getCursor() {
        return getDatabase().query(TASK_TABLE, columns, null, null, null, null,
                null);
    }

    TaskDb(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TASK_TABLE + " ( "
                + COLUMN_ID + " INTEGER primary key autoincrement, "
                + COLUMN_ALARM_TIME + " LONG NOT NULL, "
                + COLUMN_ACTIVE + " INTEGER NOT NULL, "
                + COLUMN_REPEAT + " TEXT NOT NULL, "
                + COLUMN_TONE_PATH + " TEXT NOT NULL, "
                + COLUMN_VIBRATE + " INTEGER NOT NULL, "
                + COLUMN_MAP + " TEXT DEFAULT NULL, "
                + COLUMN_CONTENT + " TEXT NOT NULL,"
                + COLUMN_PHOTO + " INTEGER NOT NULL, "
                + COLUMN_FINISHED + " INTEGER NOT NULL,"
                + COLUMN_COMPLETE_DATE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TASK_TABLE);
        onCreate(db);
    }

    public static List<TaskItem> getAll() {
        List<TaskItem> taskItems = new ArrayList<TaskItem>();
        Cursor cursor = TaskDb.getCursor();
        if (cursor.moveToFirst()) {
            do {
                TaskItem taskItem = getTaskFromCursor(cursor);
                taskItems.add(taskItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return taskItems;
    }
}