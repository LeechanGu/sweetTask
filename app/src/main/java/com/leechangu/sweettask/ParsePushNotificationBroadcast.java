package com.leechangu.sweettask;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by CharlesGao on 15-11-26.
 *
 * Function: This is a class that respond the notification from parse
 *
 */
public class ParsePushNotificationBroadcast extends ParsePushBroadcastReceiver {



    /**
     * Called when the push notification is opened by the user. Sends analytics info back to Parse
     * that the application was opened from this push notification. By default, this will navigate
     * to the {@link Activity} returned by {@link #getActivity(Context, Intent)}. If the push contains
     * a 'uri' parameter, an Intent is fired to view that URI with the Activity returned by
     * {@link #getActivity} in the back stack.
     *
     * @param context The {@code Context} in which the receiver is running.
     * @param intent  An {@code Intent} containing the channel and data of the current push notification.
     */
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);

        String form;
        String fromWho;

        // Bundle from server
        Bundle extras = intent.getExtras();
        // when extras != null
        if(extras!=null){
            String jsonData = extras.getString("com.parse.Data");
            try {
                JSONObject jsonObject = new JSONObject(jsonData);
                form = jsonObject.getString("form");
                fromWho = jsonObject.getString("fromWho");

                // Bundle to client
                Bundle extrasToClient = new Bundle();
                extrasToClient.putString("fromWho", fromWho);
                Intent intentToClient;
                // several forms
                if(!form.equals("")){
                    if(form.equals("Invite")){
                        intentToClient = new Intent(context.getApplicationContext(), InvitationActivity.class);
                        intentToClient.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentToClient.putExtras(extrasToClient);
                        context.startActivity(intentToClient);
                    }else if(form.equals("Cancel")){
                        intentToClient = new Intent(context.getApplicationContext(), CancellationActivity.class);
                        intentToClient.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentToClient.putExtras(extrasToClient);
                        context.startActivity(intentToClient);
                    }else{
                        // Other form go to MainActivity of the app.
                        intentToClient = new Intent(context.getApplicationContext(), MainActivity.class);
                        intentToClient.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intentToClient.putExtras(extrasToClient);
                        context.startActivity(intentToClient);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }




    }

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        // TODO Auto-generated method stub
        return super.getNotification(context, intent);
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        // TODO Auto-generated method stub
        super.onPushDismiss(context, intent);
    }

    @Override
    protected void onPushReceive(Context context, Intent intent) {
        //here You can handle push before appearing into status e.g if you want to stop it.
        super.onPushReceive(context, intent);
    }

    @Override
    protected Bitmap getLargeIcon(Context context, Intent intent) {
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        return largeIcon;
    }
}
