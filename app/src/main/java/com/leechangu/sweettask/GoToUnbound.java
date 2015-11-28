package com.leechangu.sweettask;

import android.content.Context;
import android.content.Intent;

/**
 * Created by CharlesGao on 15-11-25.
 *
 * Function: This is a Class that using polymorphism to avoid use switch case statement
 *
 */
public class GoToUnbound implements GoToActivityable {

    @Override
    public void goToActivity(Context content) {
        Intent intent = new Intent(content, UnboundActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        content.startActivity(intent);
    }
}
