package com.leechangu.sweettask;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2015/10/2.
 */
public class SweetTaskDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "SweetTaskDb.db";
    public static final String TABLE_NAME = "task_table";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_TIMEBASIS = "time_basis";
    public static final String COLUMN_CREATED = "created";
    public static final String COLUMN_MODIFIED = "modified";


    public SweetTaskDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES = "CREATE TABLE " +TABLE_NAME+"("
                +COLUMN_TYPE+" text,"
                +COLUMN_TIMEBASIS+" text,"
                +COLUMN_CONTENT+" text,"
                +COLUMN_CREATED+" long primary key, "
                +COLUMN_MODIFIED+" long)";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public long insertTask( String content, TaskTypeEnum taskType, TimeBasisEnum timeBasis, long created, long modified)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CONTENT, content);
        contentValues.put(COLUMN_TYPE, taskType.toString());
        contentValues.put(COLUMN_TIMEBASIS, timeBasis.toString());
        contentValues.put(COLUMN_CREATED, created);
        contentValues.put(COLUMN_MODIFIED, modified);
        long n = db.insert(TABLE_NAME, null, contentValues);
        return n;
    }

    public long insertTask(CommonTaskItem item)
    {
        return insertTask(item.getContent(),item.getTaskType(),item.getTimeBasisEnum(),item.getCreated(),item.getModified());
    }

    public Cursor getData(long created){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("select * from " + TABLE_NAME + " where " + COLUMN_CREATED + "=" + created, null);
        return res;
    }

    public CommonTaskItem getItem (long created)
    {
        Cursor cursor = getData(created);
        if (false ==cursor.moveToFirst())
            return null;
        String content = cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT));
        TaskTypeEnum type = TaskTypeEnum.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
        TimeBasisEnum timeBasis = TimeBasisEnum.valueOf(cursor.getString(cursor.getColumnIndex(COLUMN_TIMEBASIS)));
        long modified = cursor.getLong(cursor.getColumnIndex(COLUMN_MODIFIED));
        CommonTaskItem item = new CommonTaskItem(content,timeBasis,type,created,modified);

        return item;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateTask(long created, String name, String content, TaskTypeEnum taskType, TimeBasisEnum timeBasis, long modified)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_TYPE, name);
        contentValues.put(COLUMN_CONTENT, content);
        contentValues.put(COLUMN_TYPE, taskType.toString());
        contentValues.put(COLUMN_TIMEBASIS, timeBasis.toString());
        contentValues.put(COLUMN_CREATED, created);
        contentValues.put(COLUMN_MODIFIED, modified);
        db.update(TABLE_NAME, contentValues, "created = ? ", new String[] {Long.toString(created)});
        return true;
    }

    public Integer deleteTask (long created)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "created = ? ",
                new String[] { Long.toString(created) });
    }

    public Integer deleteTask (CommonTaskItem item)
    {
        return deleteTask(item.getCreated());
    }

    public List<CommonTaskItem> getAllTaskItems()
    {
        List<CommonTaskItem> listCommonTaskItems = new ArrayList<CommonTaskItem>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            String content = res.getString(res.getColumnIndex(COLUMN_CONTENT));
            TimeBasisEnum timeBasis = TimeBasisEnum.valueOf(res.getString(res.getColumnIndex(COLUMN_TIMEBASIS)));
            TaskTypeEnum type = TaskTypeEnum.valueOf(res.getString(res.getColumnIndex(COLUMN_TYPE)));
            long created = res.getLong(res.getColumnIndex(COLUMN_CREATED));
            long modified = res.getLong(res.getColumnIndex(COLUMN_MODIFIED));

            CommonTaskItem item = new CommonTaskItem(content,timeBasis,type, created, modified);
            listCommonTaskItems.add(item);
            res.moveToNext();
        }
        return listCommonTaskItems;
    }
}
