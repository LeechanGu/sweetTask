package com.leechangu.sweettask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import database.DatabaseAdapter;

public class LogInActivity extends Activity {

    public static final String MY_PREFS = "SweeTaskPreference";
    private DatabaseAdapter dbAdapter;
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

        SharedPreferences mySharedPreferences = getSharedPreferences(MY_PREFS, 0);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putLong("uid", 0);
        editor.commit();

        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();

        setContentView(R.layout.activity_log_in);
        initControls();
        utilRepo = new UtilRepository();
    }

    private void initControls() {
        //Set the activity layout.
        theUsername = (EditText) findViewById(R.id.Username);
        thePassword = (EditText) findViewById(R.id.Password);
        loginButton = (Button) findViewById(R.id.Login);
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
        //Get the username and password
        String thisUsername = theUsername.getText().toString();
        String thisPassword = thePassword.getText().toString();

        //Assign the hash to the password
        thisPassword = UtilRepository.md5(thisPassword);

        // Check the existing user name and password database
        Cursor theUser = dbAdapter.fetchUser(thisUsername, thisPassword);
        if (theUser != null) {
            startManagingCursor(theUser);
            if (theUser.getCount() > 0) {
                saveLoggedInUId(theUser.getLong(theUser.getColumnIndex(DatabaseAdapter.COL_ID)), thisUsername, thePassword.getText().toString());
                stopManagingCursor(theUser);
                theUser.close();
                Intent i = new Intent(v.getContext(), MainActivity.class);
                startActivity(i);
            }

            //Returns appropriate message if no match is made
            else {
                Toast.makeText(getApplicationContext(),
                        "You have entered an incorrect username or password.",
                        Toast.LENGTH_SHORT).show();
                saveLoggedInUId(0, "", "");
            }
            stopManagingCursor(theUser);
            theUser.close();
        }

        else {
            Toast.makeText(getApplicationContext(),
                    "Database query error",
                    Toast.LENGTH_SHORT).show();
        }
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

    public  void saveLoggedInUId(long id, String username, String password) {
        SharedPreferences settings = getSharedPreferences(LogInActivity.MY_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("uid", id);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }
}