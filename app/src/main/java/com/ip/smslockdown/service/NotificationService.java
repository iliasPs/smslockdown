package com.ip.smslockdown.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.os.CountDownTimer;

import com.ip.smslockdown.R;
import com.ip.smslockdown.activities.MainActivity;
import com.ip.smslockdown.helpers.NotificationHelper;

public class NotificationService extends JobService {

    private static final String TAG = "NotificationService";
    private static final long TOTAL_MILLIS = 7200000;
    private static final long INTERVAL_MILLIS = 1000;
    private static final String TIME_FINISHED = "finished";
    private static final String TIME_ENDING = "ending";
    private boolean jobCancelled = false;

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

    @Override
    public boolean onStartJob(JobParameters params) {
        new CountDownTimer(20000, INTERVAL_MILLIS) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished < 6000 && millisUntilFinished > 5000) {
                    sendNotification(TIME_ENDING, getApplicationContext());
                }
            }

            @Override
            public void onFinish() {
                sendNotification(TIME_FINISHED, getApplicationContext());
            }
        }.start();

        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }

}
