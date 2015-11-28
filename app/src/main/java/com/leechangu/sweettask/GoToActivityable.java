package com.leechangu.sweettask;

import android.content.Context;
import android.content.Intent;

/**
 * Created by CharlesGao on 15-11-25.
 *
 * Function: This is the Interface that provide go to exact Activity method,
 * in order to use polymorphism and avoid switch case statement.
 */
public interface GoToActivityable {

    public void goToActivity(Context content);

}
