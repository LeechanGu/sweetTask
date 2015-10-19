package com.leechangu.sweettask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.leechangu.sweettask.settask.TaskDatabase;
import com.leechangu.sweettask.settask.TaskPreferenceActivity;

import java.util.List;

public class MainActivity extends BaseActionBarActivity implements CheckBox.OnClickListener{
    private ListView taskListView;
    private TaskArrayAdapter taskArrayAdapter;
    private final static String EDIT_STRING = "Edit";
    private final static String DELETE_STRING = "Delete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TaskDatabase.init(MainActivity.this);
        taskListView = (ListView)findViewById(R.id.taskListView);
        final List<TaskItem> taskItems = TaskDatabase.getAll();
        taskArrayAdapter = new TaskArrayAdapter(this,R.layout.custom_task_row, taskItems);
        taskListView.setAdapter(taskArrayAdapter);
        taskListView.setLongClickable(true);
        taskListView.setClickable(true);
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                TaskItem taskItem = (TaskItem)taskListView.getItemAtPosition(position);
                view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, TaskPreferenceActivity.class);
                intent.putExtra("taskItem", taskItem);
                startActivity(intent);
            }
        });

        // display tasks in listView
        updateAlarmList();
        registerForContextMenu(taskListView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.menu_item_new).setVisible(true);
        menu.findItem(R.id.menu_item_delete).setVisible(false);
        menu.findItem(R.id.menu_item_save).setVisible(false);
        return result;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, EDIT_STRING);
        menu.add(0, v.getId(), 0, DELETE_STRING);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo itemInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        TaskItem taskItem = (TaskItem)taskListView.getItemAtPosition(itemInfo.position);
        switch (item.toString())
        {
            case EDIT_STRING:
                Intent intent = new Intent();
                intent.setClass(this, TaskPreferenceActivity.class);
                intent.putExtra("taskItem",taskItem);
                startActivity(intent);
                break;
            case DELETE_STRING:
                TaskDatabase.init(MainActivity.this);
                TaskDatabase.deleteEntry(taskItem);
                break;
        }
        updateAlarmList();
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateAlarmList();
    }

    @Override
    protected void onPause() {
        // setListAdapter(null);
        TaskDatabase.deactivate();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAlarmList();
    }

    public void updateAlarmList(){
        TaskDatabase.init(MainActivity.this);
        final List<TaskItem> taskItems = TaskDatabase.getAll();
        taskArrayAdapter.setTaskItems(taskItems);
        runOnUiThread(new Runnable() {
            public void run() {
                MainActivity.this.taskArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.finishCheckBox) {
            CheckBox checkBox = (CheckBox) v;
            TaskItem alarm = (TaskItem) taskArrayAdapter.getItem((Integer) checkBox.getTag());
            alarm.setActive(checkBox.isChecked());
            TaskDatabase.update(alarm);
           // AlarmActivity.this.callMathAlarmScheduleService();
            if (checkBox.isChecked()) {
                Toast.makeText(MainActivity.this, alarm.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
