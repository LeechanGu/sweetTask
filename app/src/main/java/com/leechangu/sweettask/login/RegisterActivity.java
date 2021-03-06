package com.leechangu.sweettask.login;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.leechangu.sweettask.R;
import com.leechangu.sweettask.UtilRepository;
import com.leechangu.sweettask.db.AccountDbAdapter;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends Activity {
    private EditText newUsername;
    private EditText newPassword;
    private EditText newConfiPass;
    private EditText newEmail;
    private Button registerButton;
    private Button backButton;

    private AccountDbAdapter dbHelper;
    private UtilRepository utilRepo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences(LogInActivity.MY_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("uid", 0);
        editor.commit();

        dbHelper = new AccountDbAdapter(this);
        dbHelper.open();
        setContentView(R.layout.activity_register);
        initControls();

        utilRepo = new UtilRepository();
    }

    /**
     * Handles interface controls.
     */
    private void initControls()
    {
        newUsername = (EditText) findViewById(R.id.usernameTextEdit_RegisterActivity);
        newEmail = (EditText) findViewById(R.id.emailTextEdit_RegisterActivity);
        newPassword = (EditText) findViewById(R.id.passwordTextEdit_RegisterActivity);
        newConfiPass = (EditText) findViewById(R.id.repasswordTextEdit_RegisterActivity);
        registerButton = (Button) findViewById(R.id.registerButton_RegisterActivity);
        backButton = (Button) findViewById(R.id.nBack);

        registerButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                RegisterMe(v);
            }
        });

        backButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                BackToLogin();
            }
        });
    }



    /**
     * Takes user back to login.
     */
    private void BackToLogin()
    {
        finish();
    }

    /**
     * Handles the registration process.
     * @param v
     */
    private void RegisterMe(View v)
    {

        // ProgressDialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Waiting for server...");
        progressDialog.show();

        //Get user details.
        final String username = newUsername.getText().toString();
        String email = newEmail.getText().toString();
        String password = newPassword.getText().toString();
        String confirmpassword = newConfiPass.getText().toString();

        //Check if all fields have been completed.
        if (username.equals("") || password.equals("") || email.equals("")){
            Toast.makeText(getApplicationContext(),
                    "Please ensure all fields have been completed.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Check password match.
        if (!password.equals(confirmpassword)) {
            Toast.makeText(getApplicationContext(),
                    "The password does not match.",
                    Toast.LENGTH_SHORT).show();
            newPassword.setText("");
            newConfiPass.setText("");
            return;
        }

        //Encrypt password with MD5.
        password = UtilRepository.md5(password);

        // SignUp using Parse
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);


//        // other fields can be set just like with ParseObject
//        user.put("phone", "650-253-0000");

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                    saveLoggedInUId(username, newPassword.getText().toString());
                    Intent i = new Intent(getApplicationContext(), LogInActivity.class);
                    progressDialog.dismiss();
                    startActivity(i);
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

//        //Check database for existing users.
//        Cursor user = dbHelper.fetchUser(username, password);
//        if (user == null) {
//            Toast.makeText(getApplicationContext(), "query error",
//                    Toast.LENGTH_SHORT).show();
//        } else {
//            startManagingCursor(user);
//
//            //Check for duplicate usernames
//            if (user.getCount() > 0) {
//                Toast.makeText(getApplicationContext(), "The username is already registered",
//                        Toast.LENGTH_SHORT).show();
//                stopManagingCursor(user);
//                user.close();
//                return;
//            }
//            stopManagingCursor(user);
//            user.close();
//
//            //Create the new username.
//            long id = dbHelper.createUser(username, password);
//            if (id > 0) {
//                Toast.makeText(getApplicationContext(), "Your username was created",
//                        Toast.LENGTH_SHORT).show();
//                saveLoggedInUId(id, username, newPassword.getText().toString());
//                Intent i = new Intent(v.getContext(), MainActivity.class);
//                startActivity(i);
//
//                finish();
//            } else {
//                Toast.makeText(getApplicationContext(), "Failt to create new username",
//                        Toast.LENGTH_SHORT).show();
//            }
//        }
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
