package com.leechangu.sweettask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.leechangu.sweettask.settask.TaskPreferenceActivity;

// this should be inherited by other activities that requires action bar
public abstract class BaseActionBarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_action_bar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_base_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId())
        {
            case R.id.menu_item_new:
                intent = new Intent();
                intent.setClass(this, TaskPreferenceActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_item_settings:
                 intent = new Intent();
                intent.setClass(this, SettingActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



}
