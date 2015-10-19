package com.leechangu.sweettask;

import android.app.Activity;
import android.content.SharedPreferences;

import com.leechangu.sweettask.login.LogInActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2015/10/13.
 */

public class UtilRepository extends Activity {

    public  void saveLoggedInUId(long id, String username, String password) {
        SharedPreferences settings = getSharedPreferences(LogInActivity.MY_PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong("uid", id);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.commit();
    }
    /**
     * Hashes the password with MD5.
     * @param s
     * @return
     */
    public static String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            return s;
        }
    }
}
