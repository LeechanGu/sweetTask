package com.leechangu.sweettask.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.leechangu.sweettask.MainActivity;
import com.leechangu.sweettask.R;
import com.leechangu.sweettask.UserMng;
import com.leechangu.sweettask.UserMngRepository;
import com.leechangu.sweettask.UtilRepository;
import com.leechangu.sweettask.db.AccountDbAdapter;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class LogInActivity extends Activity {

    public static final String MY_PREFS = "SweeTaskPreference";
    private AccountDbAdapter dbAdapter;
    private EditText theUsername;
    private EditText thePassword;
    private Button loginButton;
    private TextView registerTextView;
    private CheckBox rememberDetails;
    private UtilRepository utilRepo;



    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Already add this two lines in App.java class that extends the Application
        // Then you don't need to add these two line in every activity of your app
//        // Enable Local Datastore.
//        Parse.enableLocalDatastore(this);
//        Parse.initialize(this, "RkIHvpR9hEdejBizCivkmywOflupRH49ozgFHROt", "ZqTOMc3F7JucJzOzJc0rt3st4Iffy2ij6njXc0QX");

        SharedPreferences mySharedPreferences = getSharedPreferences(MY_PREFS, 0);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
//        editor.putLong("uid", 0);
        editor.commit();

        dbAdapter = new AccountDbAdapter(this);
        dbAdapter.open();

        setContentView(R.layout.activity_log_in);
        initControls();
        utilRepo = new UtilRepository();
    }

    private void initControls() {
        //Set the activity layout.
        theUsername = (EditText) findViewById(R.id.usernameTextEdit_LoginActivity);
        thePassword = (EditText) findViewById(R.id.passwordTextEdit_LoginActivity);
        loginButton = (Button) findViewById(R.id.login_LoginActivity);
        registerTextView = (TextView) findViewById(R.id.Register);

        rememberDetails = (CheckBox) findViewById(R.id.RememberMe);

        //Create touch listeners for all buttons.
        loginButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick (View v){
                LogMeIn(v);
            }
        });

        registerTextView.setOnClickListener(new TextView.OnClickListener() {
            public void onClick(View v) {
                Register(v);
            }
        });


        //Create remember password check box listener.
        rememberDetails.setOnClickListener(new CheckBox.OnClickListener() {
            public void onClick(View v) {
                RememberMe();
            }
        });

        //Handle remember password preferences.
        SharedPreferences prefs = getSharedPreferences(MY_PREFS, 0);
        String thisUsername = prefs.getString("username", "");
        String thisPassword = prefs.getString("password", "");
        boolean thisRemember = prefs.getBoolean("remember", false);
        if(thisRemember) {
            theUsername.setText(thisUsername);
            thePassword.setText(thisPassword);
            rememberDetails.setChecked(thisRemember);
        }

    }


    /**
     * Handles the remember password option.
     */
    private void RememberMe() {
        boolean thisRemember = rememberDetails.isChecked();
        SharedPreferences prefs = getSharedPreferences(MY_PREFS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("remember", thisRemember);
        editor.commit();
    }

    /**
     * This method handles the user login process.
     * @param v
     */
    private void LogMeIn(View v) {
        // ProgressDialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.waiting_for_the_server));
        progressDialog.show();


        //Get the username and password
        final String thisUsername = theUsername.getText().toString();
        String thisPassword = thePassword.getText().toString();


        // Associate the device with a device once a user login
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("pushToId", UserMngRepository.convertUsernameToId(thisUsername)); // Should not be objectId!
        // which already have in Installation table
        try {
            installation.save();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("push", e.toString());
        }

        //Assign the hash to the password
        thisPassword = UtilRepository.md5(thisPassword);

        // Using Parse
        ParseUser.logInInBackground(thisUsername, thisPassword, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    UserMng userMng = UserMng.getInstance();
                    userMng.setMyUsername(thisUsername);

                    Intent i;
                    saveLoggedInUId(thisUsername, thePassword.getText().toString());
//                    if (ParsePushNotificationBroadcast.flag_If_Go_To_Invitation_Activity_Automatically==1){
//                        i = new Intent(getApplicationContext(), InvitationActivity.class);
//                        ParsePushNotificationBroadcast.flag_If_Go_To_Invitation_Activity_Automatically=0;
//                    } else if (ParsePushNotificationBroadcast.flag_If_Go_To_Cancellation_Activity_Automatically==1){
//                        i = new Intent(getApplicationContext(), CancellationActivity.class);
//                        ParsePushNotificationBroadcast.flag_If_Go_To_Cancellation_Activity_Automatically=0;
//                    } else {
                        i = new Intent(getApplicationContext(), MainActivity.class);
//                    }

                    progressDialog.dismiss();
                    startActivity(i);
                } else {
                    // Signup failed. Look at the ParseException to see what happened.
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });

//        // Check the existing user name and password database
//        Cursor theUser = dbAdapter.fetchUser(thisUsername, thisPassword);
//        if (theUser != null) {
//            startManagingCursor(theUser);
//            if (theUser.getCount() > 0) {
//                saveLoggedInUId(theUser.getLong(theUser.getColumnIndex(AccountDbAdapter.COL_ID)), thisUsername, thePassword.getText().toString());
//                stopManagingCursor(theUser);
//                theUser.close();
//                Intent i = new Intent(v.getContext(), MainActivity.class);
//                startActivity(i);
//            }
//
//            //Returns appropriate message if no match is made
//            else {
//                Toast.makeText(getApplicationContext(),
//                        "You have entered an incorrect username or password.",
//                        Toast.LENGTH_SHORT).show();
//                saveLoggedInUId(0, "", "");
//            }
//            stopManagingCursor(theUser);
//            theUser.close();
//        }
//
//        else {
//            Toast.makeText(getApplicationContext(),
//                    "query error",
//                    Toast.LENGTH_SHORT).show();
//        }
    }

    /**
     * Open the Registration activity.
     * @param v
     */
    private void Register(View v)
    {
        Intent i = new Intent(v.getContext(), RegisterActivity.class);
        startActivity(i);
    }

    public  void saveLoggedInUId(String username, String password) {
        SharedPreferences settings = getSharedPreferences(LogInActivity.MY_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
//        editor.putLong("uid", id);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }
}