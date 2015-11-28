package com.leechangu.sweettask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


/**
 * Created by CharlesGao on 15-11-25.
 *
 * Function: This is a Class that using polymorphism to avoid use switch case statement
 *
 * NOTE:
 *
 */
public class GoToBound implements GoToActivityable {

    @Override
    public void goToActivity(Context content) {
        Intent intent = new Intent(content, BoundActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        content.startActivity(intent);
    }
}
