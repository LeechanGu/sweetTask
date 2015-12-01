package com.leechangu.sweettask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * Created by CharlesGao on 15-11-26.
 *
 * Function: This is a activity that when click the invitation notification
 */
public class InvitationActivity extends Activity {

    private TextView textView;
    private Button button_accept;
    private Button button_reject;
    String username;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        init();

        // Set username to text view
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            username = extras.get("fromWho").toString();
            textView.setText(getString(R.string.invite_from)+" "+username);
        }

        button_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(InvitationActivity.this);
                progressDialog.setMessage("Binding...");
                progressDialog.show();

                // Respond to server
                if (!UserMngRepository.isThisUserPartnerIsMe(username)) {
                // If this user's partner is not me, which means that before current user accept the
                    // Invitation, this user unbind with current user and sent another invitation
                    // to another user
                    Toast.makeText(InvitationActivity.this,
                            "The user you want to accept now bind with other user",
                            Toast.LENGTH_SHORT).show();
                } else {
                    UserMngRepository.acceptBindingInvitation(username);
                    progressDialog.dismiss();

                    Toast.makeText(InvitationActivity.this, "You now successfully binding with " +
                            username + ", Let's set some tasks for him/her", Toast.LENGTH_SHORT).show();

                    finish();
                }


            }
        });

        button_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Send rejected notification
                UserMngRepository.rejectBindingInvitation(username);
                Toast.makeText(InvitationActivity.this, "You rejected " +
                        username + "'s invitation", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }


    public void init(){
        textView = (TextView)findViewById(R.id.tv_invite_from);
        button_accept = (Button)findViewById(R.id.bt_accept_invite);
        button_reject = (Button)findViewById(R.id.bt_reject_invite);
    }


}
