package com.leechangu.sweettask;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * Created by CharlesGao on 15-11-26.
 *
 * Function: This is a activity that when click the invitation notification
 */
public class InvitationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        Toast.makeText(this,ParseUser.getCurrentUser().getUsername(),Toast.LENGTH_SHORT).show();

    }
}
