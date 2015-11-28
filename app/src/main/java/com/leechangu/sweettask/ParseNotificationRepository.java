package com.leechangu.sweettask;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import org.json.JSONObject;

/**
 * Created by CharlesGao on 15-11-26.
 *
 * Function: This is a class that have all the methods related with ParseNotificationRepository
 *
 *
 */
public class ParseNotificationRepository {

    public static boolean sendBindInvitationById(String Id){

        ParseInstallation.getCurrentInstallation().saveInBackground();

        // Associate the device with a user
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("pushToId",Id); // Should not be objectId!
        // which already have in Installation table
        try {
            installation.save();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("push", e.toString());
        }

        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("pushToId", Id);


        // TODO still needs to have some extra info needs to be saved in this push

        // Send push notification to query
        ParsePush push = new ParsePush();
        String json = new ""
        JSONObject jsonObject = new JSONObject()
        push.setData();
        push.setQuery(pushQuery); // Set our Installation query
        push.setMessage("Binding Invitation from " + ParseUser.getCurrentUser().getUsername());
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("push", "The push campaign has been created.");
                } else {
                    Log.d("push", "Error sending push:" + e.getMessage());
                }
            }
        });
        return true;
    }

}
