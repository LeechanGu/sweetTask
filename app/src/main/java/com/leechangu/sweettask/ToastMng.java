package com.leechangu.sweettask;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/11/25.
 */
public class ToastMng {
    private static ToastMng ourInstance = new ToastMng();

    private ToastMng() {
    }

    public static ToastMng getInstance() {
        return ourInstance;
    }

    public static void toastSomething(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
