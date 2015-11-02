package com.leechangu.sweettask.settask;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.view.HapticFeedbackConstants;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.leechangu.sweettask.BaseActionBarActivity;
import com.leechangu.sweettask.MapCircle;
import com.leechangu.sweettask.MapsActivity;
import com.leechangu.sweettask.R;
import com.leechangu.sweettask.TaskItem;
import com.leechangu.sweettask.db.TaskDb;
import com.leechangu.sweettask.settask.TaskPreference.Key;

public class TaskPreferenceActivity extends BaseActionBarActivity {

    private TaskItem taskItem;
    private TaskPreferenceListAdapter listAdapter;
    private ListView listView;
    private MediaPlayer mediaPlayer;
    private CountDownTimer alarmToneTimer;
    public final static int REQUESTCODE_MAP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_task);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("taskItem")) {
            setTaskItem((TaskItem) bundle.getSerializable("taskItem"));
        } else {
            setTaskItem(new TaskItem());
        }
        if (bundle != null && bundle.containsKey("adapter")) {
            setListAdapter((TaskPreferenceListAdapter) bundle.getSerializable("adapter"));
        } else {
            setListAdapter(new TaskPreferenceListAdapter(this, getTaskItem()));
        }

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                final TaskPreferenceListAdapter taskPreferenceListAdapter = (TaskPreferenceListAdapter) getListAdapter();
                final TaskPreference TaskPreference = (TaskPreference) getListAdapter().getItem(position);

                AlertDialog.Builder alert;
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                switch (TaskPreference.getType()) {
                    case BOOLEAN:
                        CheckedTextView checkedTextView = (CheckedTextView) v;
                        boolean checked = !checkedTextView.isChecked();
                        ((CheckedTextView) v).setChecked(checked);
                        switch (TaskPreference.getKey()) {
                            case TASK_ACTIVE:
                                taskItem.setActive(checked);
                                break;
                            case TASK_VIBRATE:
                                taskItem.setVibrate(checked);
                                if (checked) {
                                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                                    vibrator.vibrate(1000);
                                }
                                break;
                        }
                        TaskPreference.setValue(checked);
                        break;
                    case STRING:
                        alert = new AlertDialog.Builder(TaskPreferenceActivity.this);
                        alert.setTitle(TaskPreference.getTitle());

                        final EditText input = new EditText(TaskPreferenceActivity.this);

                        input.setText(TaskPreference.getValue().toString());

                        alert.setView(input);
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                TaskPreference.setValue(input.getText().toString());

                                if (TaskPreference.getKey() == Key.TASK_CONTENT) {
                                    taskItem.setContent(TaskPreference.getValue().toString());
                                }

                                taskPreferenceListAdapter.DisplayPreferences(getTaskItem());
                                taskPreferenceListAdapter.notifyDataSetChanged();
                            }
                        });
                        alert.show();
                        break;
                    case LIST:
                        alert = new AlertDialog.Builder(TaskPreferenceActivity.this);
                        alert.setTitle(TaskPreference.getTitle());

                        CharSequence[] items = new CharSequence[TaskPreference.getOptions().length];
                        for (int i = 0; i < items.length; i++)
                            items[i] = TaskPreference.getOptions()[i];

                        alert.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (TaskPreference.getKey()) {
                                    case TASK_REPEAT:
                                        taskItem.setTimeBasis(TaskItem.TimeBasisEnum.values()[which]);
                                        break;
                                    case TASK_TONE:
                                        taskItem.setAlarmTonePath(taskPreferenceListAdapter.getAlarmTonePaths()[which]);
                                        if (taskItem.getAlarmTonePath() != null) {
                                            if (mediaPlayer == null) {
                                                mediaPlayer = new MediaPlayer();
                                            } else {
                                                if (mediaPlayer.isPlaying())
                                                    mediaPlayer.stop();
                                                mediaPlayer.reset();
                                            }
                                            try {
                                                // mediaPlayer.setVolume(1.0f, 1.0f);
                                                mediaPlayer.setVolume(0.2f, 0.2f);
                                                mediaPlayer.setDataSource(TaskPreferenceActivity.this, Uri.parse(taskItem.getAlarmTonePath()));
                                                mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                                                mediaPlayer.setLooping(false);
                                                mediaPlayer.prepare();
                                                mediaPlayer.start();

                                                // Force the mediaPlayer to stop after 3
                                                // seconds...

                                                if (alarmToneTimer != null)
                                                    alarmToneTimer.cancel();
                                                alarmToneTimer = new CountDownTimer(3000, 3000) {
                                                    @Override
                                                    public void onTick(long millisUntilFinished) {

                                                    }

                                                    @Override
                                                    public void onFinish() {
                                                        try {
                                                            if (mediaPlayer.isPlaying())
                                                                mediaPlayer.stop();
                                                        } catch (Exception e) {

                                                        }
                                                    }
                                                };
                                                alarmToneTimer.start();
                                            } catch (Exception e) {
                                                try {
                                                    if (mediaPlayer.isPlaying())
                                                        mediaPlayer.stop();
                                                } catch (Exception e2) {

                                                }
                                            }
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                taskPreferenceListAdapter.DisplayPreferences(getTaskItem());
                                taskPreferenceListAdapter.notifyDataSetChanged();
                            }
                        });
                        alert.show();
                        break;
                    case MAP:
                        if (TaskPreference.getKey().equals(Key.TASK_MAP))
                        {
                            Intent intent = new Intent();
                            intent.setClass(TaskPreferenceActivity.this, MapsActivity.class);
                            intent.putExtra("map_info",taskItem.getMapInfo());
                            startActivityForResult(intent, REQUESTCODE_MAP);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final TaskPreferenceListAdapter taskPreferenceListAdapter = (TaskPreferenceListAdapter) getListAdapter();
        if (requestCode == REQUESTCODE_MAP)
        {
            if (resultCode >0) {
                Bundle bundle = data.getExtras();
                String mapInfo = bundle.getString(MapsActivity.MAP_RESULT);
                if (!mapInfo.equals(MapCircle.NO_MAP)) {
                    taskItem.setMapInfo(mapInfo);
                    updatePreferenceList();
                }
                else
                {
                    taskItem.setMapInfo(null);
                    updatePreferenceList();
                }
                taskPreferenceListAdapter.DisplayPreferences(getTaskItem());
                taskPreferenceListAdapter.notifyDataSetChanged();
            }
        }
    }

    public void updatePreferenceList(){
        runOnUiThread(new Runnable() {
            public void run() {
                TaskPreferenceActivity.this.listAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.menu_item_new).setVisible(false);
        menu.findItem(R.id.menu_item_save).setVisible(true);
        menu.findItem(R.id.menu_item_delete).setVisible(true);
        return result;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save:
                TaskDb.init(getApplicationContext());
                if (getTaskItem().getId() < 1) {
                    TaskDb.create(getTaskItem());
                } else {
                    TaskDb.update(getTaskItem());
                }
                //callMathAlarmScheduleService();
                Toast.makeText(TaskPreferenceActivity.this, getTaskItem().getTimeUntilNextAlarmMessage(), Toast.LENGTH_LONG).show();
                finish();
                break;
            case R.id.menu_item_delete:
                AlertDialog.Builder dialog = new AlertDialog.Builder(TaskPreferenceActivity.this);
                dialog.setTitle("Delete");
                dialog.setMessage("Delete this alarm?");
                dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        TaskDb.init(getApplicationContext());
                        if (getTaskItem().getId() < 1) {
                            // Alarm not saved
                        } else {
                            TaskDb.deleteEntry(taskItem);
                            //callMathAlarmScheduleService();
                        }
                        finish();
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setTaskItem(TaskItem taskItem) {
        this.taskItem = taskItem;
    }

    public void setListAdapter(TaskPreferenceListAdapter listAdapter) {
        this.listAdapter = listAdapter;
        getListView().setAdapter(listAdapter);
    }

    public ListAdapter getListAdapter() {
        return listAdapter;
    }

    private ListView getListView() {
        if (listView == null)
            listView = (ListView) findViewById(R.id.setTaskListView);
        return listView;
    }

    public TaskItem getTaskItem() {
        return taskItem;
    }
}
