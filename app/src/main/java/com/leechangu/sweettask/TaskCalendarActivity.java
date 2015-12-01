package com.leechangu.sweettask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.Date;
import java.util.List;

public class TaskCalendarActivity extends BaseActionBarActivity {

    MaterialCalendarView calendarView;
    TextView taskProfileTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_calendar);

        Bundle bundle = getIntent().getExtras();
        ParseTaskItem taskItem = (ParseTaskItem) bundle.get("taskItem");
        taskProfileTextView = (TextView) findViewById(R.id.datesTextView);
        taskProfileTextView.setText(taskItem.toString());

        // calendarView
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        setDatesSelected(calendarView, taskItem.getCompleteDates());
    }

    private void setDatesSelected(MaterialCalendarView calendarView, List<Long> dateList) {
        for (Long date : dateList) {
            calendarView.setDateSelected(new Date(date), true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.menu_item_new).setVisible(false);
        menu.findItem(R.id.menu_item_delete).setVisible(false);
        menu.findItem(R.id.menu_item_save).setVisible(false);
        return result;
    }

}
