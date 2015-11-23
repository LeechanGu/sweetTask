package com.leechangu.sweettask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Date;
import java.util.List;

public class TaskCalendarActivity extends AppCompatActivity {

    MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_calendar);


        Bundle bundle = getIntent().getExtras();
        TaskItem taskItem = (TaskItem) bundle.get("taskItem");


        // calendarView
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        setDatesSelected(calendarView, taskItem.getCompleteDates());
    }

    private void setDatesSelected(MaterialCalendarView calendarView, List<Date> dateList) {
        for (Date date : dateList) {
            calendarView.setDateSelected(date, true);
        }
    }

}
