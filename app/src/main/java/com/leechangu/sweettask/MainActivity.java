package com.leechangu.sweettask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.leechangu.sweettask.db.TaskDb;
import com.leechangu.sweettask.settask.TaskPreferenceActivity;

import java.util.ArrayList;
import java.util.List;
// This is the main Activity of the app, displaying all the tasks
public class MainActivity extends BaseActionBarActivity implements CheckBox.OnClickListener{
    private ListView taskListView;
    private TaskArrayAdapter taskArrayAdapter;
    private final static String EDIT_STRING = "Edit";
    private final static String DELETE_STRING = "Delete";
    public final static int REQUESTCODE_LOCATION = 2;
    List<CheckBox> checkBoxeList;

    CheckBox mapCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TaskDb.init(MainActivity.this);
        taskListView = (ListView)findViewById(R.id.taskListView);
        final List<TaskItem> taskItems = TaskDb.getAll();
        taskArrayAdapter = new TaskArrayAdapter(this,R.layout.custom_task_row, taskItems);
        taskListView.setAdapter(taskArrayAdapter);
        taskListView.setLongClickable(true);
        taskListView.setClickable(true);
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                final TaskItem taskItem = (TaskItem) taskListView.getItemAtPosition(position);

                AlertDialog.Builder alert;
                alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Task requirement:");

                LayoutInflater inflater = getLayoutInflater();
                final View modifyView = inflater.inflate(R.layout.finish_task_dialog, null);
                alert.setView(modifyView);

                checkBoxeList = new ArrayList<CheckBox>();
                CheckBox contentCheckBox = (CheckBox)modifyView.findViewById(R.id.contentCheckBox);
                contentCheckBox.setText(taskItem.getContent());
                checkBoxeList.add(contentCheckBox);
                mapCheckBox = (CheckBox) modifyView.findViewById(R.id.mapCheckBox);
                if (taskItem.getMapInfo() == null) {
                    mapCheckBox.setVisibility(View.INVISIBLE);
                } else {
                    mapCheckBox.setVisibility(View.VISIBLE);
                    checkBoxeList.add(mapCheckBox);
                    mapCheckBox.setText("Map task (Click for destination)");
                    mapCheckBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mapCheckBox.isChecked()) {
                                mapCheckBox.setChecked(false);
                                Intent intent = new Intent();
                                intent.setClass(MainActivity.this, MyLocationActivity.class);
                                intent.putExtra("map_info", taskItem.getMapInfo());
                                startActivityForResult(intent, REQUESTCODE_LOCATION);
                            }
                        }

                    });
                }

                CheckBox photoCheckBox = (CheckBox) modifyView.findViewById(R.id.photoCheckBox);
                photoCheckBox.setVisibility(View.INVISIBLE);

                alert.setPositiveButton("Finished", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        boolean allChecked = true;
                        for (CheckBox checkBox: checkBoxeList)
                        {
                            if (!checkBox.isChecked())
                            {
                                allChecked = false;
                                break;
                            }
                        }
                        if (allChecked) {
                            Toast.makeText(getApplicationContext(), "Congratulation!", Toast.LENGTH_SHORT).show();
                            taskItem.setFinished(true);
                            TaskDb.update(taskItem);
                            updateAlarmList();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Some tasks are yet to be finished.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alert.setNegativeButton("Not yet", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        taskItem.setFinished(false);
                        TaskDb.update(taskItem);
                        taskArrayAdapter.notifyDataSetChanged();
                    }
                });
                alert.show();
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
                TaskDb.deleteEntry(taskItem);
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
        TaskDb.deactivate();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateAlarmList();
    }

    public void updateAlarmList(){
        TaskDb.init(MainActivity.this);
        final List<TaskItem> taskItems = TaskDb.getAll();
        taskArrayAdapter.setTaskItems(taskItems);
        runOnUiThread(new Runnable() {
            public void run() {
                MainActivity.this.taskArrayAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.activeCheckBox) {
            CheckBox checkBox = (CheckBox) v;
            TaskItem taskItem = (TaskItem) taskArrayAdapter.getItem((Integer) checkBox.getTag());
            taskItem.setActive(checkBox.isChecked());
            TaskDb.update(taskItem);
            // AlarmActivity.this.callMathAlarmScheduleService();
            callScheduleService();
            if (checkBox.isChecked()) {
                Toast.makeText(MainActivity.this, taskItem.getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void callScheduleService()
    {
        Intent scheduleServiceIntent = new Intent(this, TaskNotificationUpdateBroadcastReceiver.class);
        sendBroadcast(scheduleServiceIntent, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getApplicationContext(), "onActivityResult,"+requestCode+","+resultCode, Toast.LENGTH_SHORT).show();
        if (resultCode == REQUESTCODE_LOCATION) {
            Bundle bundle = data.getExtras();
            boolean finishMapOrNot = bundle.getBoolean(MyLocationActivity.LOCATION_RESULT);
            if (finishMapOrNot)
                mapCheckBox.setChecked(finishMapOrNot);
            else
                Toast.makeText(getApplicationContext(), "Map task not finished", Toast.LENGTH_SHORT).show();
        }
    }
}
