package com.leechangu.sweettask;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.leechangu.sweettask.settask.TaskPreferenceActivity;
import com.parse.ParseUser;

// this should be inherited by other activities that requires action bar
public abstract class BaseActionBarActivity extends AppCompatActivity {

//    Context context;

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
            case R.id.menu_item_settings:
//                 intent = new Intent();
//                intent.setClass(this, SettingActivity.class);
//                startActivity(intent);

                if (ParseUser.getCurrentUser().get("partnerId")==null ||
                        ParseUser.getCurrentUser().get("partnerId").equals("")){
                    GoToActivityable goToActivityable = new GoToBound();
                    goToActivityable.goToActivity(getApplicationContext());
                }else {
                    GoToActivityable goToActivityable = new GoToUnbound();
                    goToActivityable.goToActivity(getApplicationContext());
                }


                break;
        }


        return super.onOptionsItemSelected(item);
    }



}
