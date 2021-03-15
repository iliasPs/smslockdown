package com.ip.smslockdown.helpers;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.ip.smslockdown.activities.MainActivity;
import com.ip.smslockdown.R;

public class TimerHelper {

    private static final String TAG = "TimerHelper";
    private static final long TOTAL_MILLIS = 7200000;
    private static final long INTERVAL_MILLIS = 1000;
    private static final String TIME_FINISHED = "finished";
    private static final String TIME_ENDING = "ending";


    public static void createTimer(final Context context) {
        Log.d(TAG, "createTimer: Starting countdown 2hrs");
        new CountDownTimer(TOTAL_MILLIS, INTERVAL_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 900000 && millisUntilFinished > 899000) {
                    sendNotification(TIME_ENDING, context);
                }
            }

            @Override
            public void onFinish() {
                sendNotification(TIME_FINISHED, context);
            }
        }.start();
    }

    private static void sendNotification(String remainingTime, Context context) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        if (TIME_ENDING.equals(remainingTime)) {
            notificationHelper.sendHighPriorityNotification(
                    context.getResources().getString(R.string.timer_title)
                    , context.getResources().getString(R.string.timer_ending)
                    , MainActivity.class);
        }
        if (TIME_FINISHED.equals(remainingTime)) {
            notificationHelper.sendHighPriorityNotification(
                    context.getResources().getString(R.string.timer_title)
                    , context.getResources().getString(R.string.timer_ended)
                    , MainActivity.class);
        }
    }
}
