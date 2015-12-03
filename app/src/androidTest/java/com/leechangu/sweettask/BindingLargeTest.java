package com.leechangu.sweettask;

/**
 * Created by Administrator on 2015/12/3.
 */

import android.app.Activity;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;

import com.leechangu.sweettask.login.LogInActivity;
import com.leechangu.sweettask.login.RegisterActivity;
import com.parse.ParseUser;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.matcher.ViewMatchers.withId;


/**
 * Created by Administrator on 2015/12/2.
 */

import android.app.Activity;
import android.app.Instrumentation;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.test.runner.lifecycle.Stage;
import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.widget.Button;

import com.leechangu.sweettask.login.RegisterActivity;
import com.parse.ParseUser;

import junit.framework.Assert;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Predicates.not;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.assertEquals;

/**
 * Created by CharlesGao on 15-11-03.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class BindingLargeTest extends ActivityInstrumentationTestCase2<LogInActivity> {

    public BindingLargeTest() {
        super(LogInActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Espresso does not start the Activity for you we need to do this manually here.
        mActivity = getActivity();
    }

    @Rule
    public ActivityTestRule<LogInActivity> mActivityRule = new ActivityTestRule<>(
            LogInActivity.class);

    private LogInActivity mActivity = null;

    @Before
    public void setActivity() {
        mActivity = mActivityRule.getActivity();
    }

    @Test
    public void testRegisterWithValidNameAndPassword() throws Throwable {
        String username = "test";
        String password = "test";
        onView(withId(R.id.usernameTextEdit_LoginActivity)).perform(ViewActions.clearText());
        onView(withId(R.id.usernameTextEdit_LoginActivity)).perform(ViewActions.replaceText(username),closeSoftKeyboard());
        onView(withId(R.id.passwordTextEdit_LoginActivity)).perform(ViewActions.clearText());
        onView(withId(R.id.passwordTextEdit_LoginActivity)).perform(ViewActions.replaceText(password),closeSoftKeyboard());
        onView(withId(R.id.login_LoginActivity)).perform(ViewActions.click());
        sleep();
        onView(withId(R.id.partnerScheduleButton)).perform(ViewActions.click());
        onView(withId(R.id.et_enter_name_of_your_partner)).perform(ViewActions.clearText());
        onView(withId(R.id.et_enter_name_of_your_partner)).perform(ViewActions.replaceText("test2"),closeSoftKeyboard());
        onView(withId(R.id.bt_find_partner)).perform(ViewActions.click());
        //onView(withId(R.id.myScheduleButton)).inRoot(withDecorView(not(is(getActivity().getWindow().getDecorView()))).check(matches(withText("Me:" + username)));
    }

    public static void sleep(){
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}


