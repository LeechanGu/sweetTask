package com.leechangu.sweettask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class SetCommonTaskActivity extends BaseActionBarActivity {

    private EditText commonTaskEditText;
    private RadioGroup timeBasisRadioGroup;
    private RadioButton dailyRadioButton;
    private RadioButton weeklyRadioButton;
    private RadioButton monthlyRadioButton;
    private SweetTaskDbHelper sweetTaskDbHelper;
    private CheckBox mapTaskCheckBox;
    private final static int MAP_RESULT_REQUEST = 1;
    private final static String MAP_RESULT_CENTER = "mapResult_center";
    private final static String MAP_RESULT_RADIUS= "mapResult_radius";
    private LatLng center;
    private Double radius;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_common_task);
        commonTaskEditText = (EditText) findViewById(R.id.commonTaskEditText);
        timeBasisRadioGroup = (RadioGroup) findViewById(R.id.timeBasisRadioGroup);
        dailyRadioButton = (RadioButton) findViewById(R.id.dailyRadioButton);
        weeklyRadioButton = (RadioButton) findViewById(R.id.weeklyRadioButton);
        monthlyRadioButton = (RadioButton) findViewById(R.id.monthlyRadioButton);
        mapTaskCheckBox = (CheckBox) findViewById(R.id.mapTaskButton);
        mapTaskCheckBox.setChecked(false);
        mapTaskCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mapTaskCheckBox.isSelected()) {
                    Intent intent = new Intent();
                    intent.setClass(SetCommonTaskActivity.this, MapsActivity.class);
                    startActivityForResult(intent, MAP_RESULT_REQUEST);
                } else {
                    // TODO: remove the task
                }
            }
        });

        sweetTaskDbHelper = new SweetTaskDbHelper(this);

        // get intent extra
        Bundle bundle = getIntent().getExtras();
        String newOrEdit = bundle.getString(getResources().getString(R.string.intent_extra_new_or_edit));
        if (newOrEdit.equals("Edit")) {
            long created = bundle.getLong(getResources().getString(R.string.intent_extra_task_item_key));
            CommonTaskItem item = sweetTaskDbHelper.getItem(created);
            commonTaskEditText.setText(item.getContent());
            TimeBasisEnum timeBasis = item.getTimeBasisEnum();
            switch (timeBasis) {
                case DAILY:
                    dailyRadioButton.setChecked(true);
                    weeklyRadioButton.setChecked(false);
                    monthlyRadioButton.setChecked(false);
                    break;
                case WEEKLY:
                    dailyRadioButton.setChecked(false);
                    weeklyRadioButton.setChecked(true);
                    monthlyRadioButton.setChecked(false);
                    break;
                case MONTHLY:
                    dailyRadioButton.setChecked(false);
                    weeklyRadioButton.setChecked(false);
                    monthlyRadioButton.setChecked(true);
                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.menu_item_new).setVisible(false);
        menu.findItem(R.id.menu_item_delete).setVisible(true);
        return result;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 2
        if(requestCode==MAP_RESULT_REQUEST)
        {
            center  = data.getParcelableExtra(MAP_RESULT_CENTER);
            radius = data.getDoubleExtra(MAP_RESULT_RADIUS,0);
            if (center!=null)
            {
                mapTaskCheckBox.setChecked(true);
                // Todo: display the center and the radius
            }
            else
            {
                mapTaskCheckBox.setChecked(false);
                // Todo: display a Toast.
            }
        }
    }

    public void onSetTaskClicked(View view)
    {
        int radioButtonID = timeBasisRadioGroup.getCheckedRadioButtonId();
        if (radioButtonID==-1)
        {
            Toast.makeText(getApplicationContext(), "Please select a time basis.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        View radioButton = timeBasisRadioGroup.findViewById(radioButtonID);
        TimeBasisEnum timeBasisEnum = null;
        switch (radioButton.getId())
        {
            case R.id.dailyRadioButton:
                timeBasisEnum = TimeBasisEnum.DAILY;
                break;
            case R.id.weeklyRadioButton:
                timeBasisEnum = TimeBasisEnum.WEEKLY;
                break;
            case R.id.monthlyRadioButton:
                timeBasisEnum = TimeBasisEnum.MONTHLY;
                break;
        }

        // check blank
        if (commonTaskEditText.getText().toString().isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Please enter task content.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // set value
        String text = commonTaskEditText.getText().toString();
        CommonTaskItem commonTaskItem = new CommonTaskItem(text,timeBasisEnum,TaskTypeEnum.COMMON_TASK);
        long inserted = sweetTaskDbHelper.insertTask(commonTaskItem);

        // get intent extra
        Bundle bundle = getIntent().getExtras();
        String newOrEdit = bundle.getString(getResources().getString(R.string.intent_extra_new_or_edit));
        //Toast.makeText(getApplicationContext(),"NewOrEdit="+newOrEdit, Toast.LENGTH_SHORT).show();
        if (newOrEdit.equals("Edit")) {
            long created = bundle.getLong(getResources().getString(R.string.intent_extra_task_item_key));
            sweetTaskDbHelper.deleteTask(created);
        }
        //Toast.makeText(getApplicationContext(), commonTaskItem.getCreated()+": "+commonTaskItem.getContent()+"("+inserted+")", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
