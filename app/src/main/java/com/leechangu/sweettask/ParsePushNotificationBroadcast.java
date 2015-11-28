package com.leechangu.sweettask;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseUser;

/**
 * Created by CharlesGao on 15-11-26.
 *
 * Function: This is a class that respond the notification from parse
 *
 */
public class ParsePushNotificationBroadcast extends ParsePushBroadcastReceiver {

    public static int flag_If_Go_To_Invitation_Activity_Automatically = 0;
    public static int flag_If_Go_To_Cancellation_Activity_Automatically = 0;


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





//        if(ParseUser.getCurrentUser()==null){
            Intent intent1 = new Intent(context.getApplicationContext(), InvitationActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
//        }else{
//            //
//            flag_If_Go_To_Invitation_Activity_Automatically = 1;
//
//        }


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

}
