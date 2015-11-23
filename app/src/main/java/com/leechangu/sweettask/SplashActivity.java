package com.leechangu.sweettask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.leechangu.sweettask.login.LogInActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by CharlesGao on 15-11-03.
 */
public class SplashActivity extends Activity {

    private static final long DELAY = 3000;
    private boolean scheduled = false;
    private Timer splashTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        // This is the start animation of SplashActivity
        LinearLayout linearLayout = (LinearLayout)findViewById(R.id.id_layout_splash);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f,1.0f);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(3000);
        linearLayout.startAnimation(alphaAnimation);

        splashTimer = new Timer();
        splashTimer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                startActivity(new Intent(SplashActivity.this, LogInActivity.class));
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
                // If you use onDestroy here, this activity will show up again
                // if you exit from Welcome page
                SplashActivity.this.finish();

            }
        }, DELAY);
        scheduled = true;
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (scheduled)
            splashTimer.cancel();
        splashTimer.purge();
    }


}
