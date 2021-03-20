package com.ip.smslockdown.helpers;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import com.ip.smslockdown.service.NotificationService;

public class ScheduleJob {

    private final Context context;
    private static final int JOB_CODE = 1111;
    private static final String TAG = "ScheduleJob";

    public ScheduleJob(Context context) {
        this.context = context;
    }

    public void scheduleJob(){
        ComponentName componentName = new ComponentName(context, NotificationService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_CODE, componentName).build();

        JobScheduler scheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(jobInfo);

        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "scheduleJob: Success...");
        }else {
            Log.d(TAG, "scheduleJob: Failure...");
        }
    }


}
