package com.leechangu.sweettask;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by CharlesGao on 15-11-26.
 *
 * Function: This is a activity that when click the cancellation notification
 */
public class CancellationActivity extends Activity {

    TextView textView_cancellation;
    Button button_got_it;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancellation);

        init();
        // Set username to text view
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            username = extras.get("fromWho").toString();
            textView_cancellation.setText(getString(R.string.invite_from) + " " + username);
        }

        button_got_it.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CancellationActivity.this,
                        "You now are not binding with "+username+" any more", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }

    public void init(){
        textView_cancellation = (TextView)findViewById(R.id.tv_cancel_from);
        button_got_it = (Button)findViewById(R.id.bt_got_it);
    }
}
