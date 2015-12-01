package com.leechangu.sweettask;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

import org.json.JSONException;
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

        // Send push notification to query
        ParsePush push = new ParsePush();
        try {

            JSONObject jsonObject = new JSONObject();
            // You must have this alert key in JSONObject or your user could not receive notification
            jsonObject.put("alert", "Binding Invitation from " + ParseUser.getCurrentUser().getUsername());
            jsonObject.put("form", "Invite");
            jsonObject.put("fromWho", ParseUser.getCurrentUser().getUsername());

            push.setData(jsonObject);

            push.setQuery(pushQuery); // Set our Installation query
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public static boolean sendBindRejectedInvitationById(String Id){

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

        // Send push notification to query
        ParsePush push = new ParsePush();
        try {
            push.setQuery(pushQuery); // Set our Installation query
            /**
             * Because we just want Current user send a Accept alert to
             * This user and no need to redirect to another activity so we
             * don't need to add "Accepted" forms in {@link ParseNotificationRepository}
             */
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("alert","Binding Invitation Rejected by "+ParseUser.getCurrentUser().getUsername());
            jsonObject.put("form", "Cancel");
            jsonObject.put("fromWho", ParseUser.getCurrentUser().getUsername());
            push.setData(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

    public static boolean sendBindAcceptedInvitationById(String Id){

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

        // Send push notification to query
        ParsePush push = new ParsePush();
        try {
            push.setQuery(pushQuery); // Set our Installation query
            /**
             * Because we just want Current user send a Accept alert to
             * This user and no need to redirect to another activity so we
             * don't need to add "Accepted" forms in {@link ParseNotificationRepository}
              */
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("alert","Binding Invitation Accepted by "+ParseUser.getCurrentUser().getUsername());
            jsonObject.put("form", "Cancel");
            jsonObject.put("fromWho", ParseUser.getCurrentUser().getUsername());
            push.setData(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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

        // Set binding back



        return true;
    }


    public static boolean sendBindCancellationById(String Id){

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

        // Send push notification to query
        ParsePush push = new ParsePush();
        try {
            push.setQuery(pushQuery); // Set our Installation query
//            JSONObject data = new JSONObject(
//                    "{\"alert\":\"Binding Cancellation Requested by "+ParseUser.getCurrentUser().getUsername()
//                            + "\", \"form\":\"Cancel\",\"from\":"
//                            +ParseUser.getCurrentUser().getUsername()+"\"}"
//            );
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("alert","Binding Cancellation Requested by "+ParseUser.getCurrentUser().getUsername());
            jsonObject.put("form", "Cancel");
            jsonObject.put("fromWho", ParseUser.getCurrentUser().getUsername());
            push.setData(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

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
