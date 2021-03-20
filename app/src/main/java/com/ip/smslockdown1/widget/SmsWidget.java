package com.ip.smslockdown1.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ip.smslockdown1.R;
import com.ip.smslockdown1.db.AppDatabase;
import com.ip.smslockdown1.helpers.AppExecutors;
import com.ip.smslockdown1.helpers.SmsHelper;
import com.ip.smslockdown1.models.SmsCode;
import com.ip.smslockdown1.models.User;

/**
 * Implementation of App Widget functionality.
 */
public class SmsWidget extends AppWidgetProvider {

    private static final String TAG = "SmsWidget";
    private User user;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        SmsCode smsCode = SmsCode.builder().build();
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sms_widget);
            views.setOnClickPendingIntent(R.id.docButton, getPendingSelfIntent(context, smsCode.withCode(1)));
            views.setOnClickPendingIntent(R.id.shopButton, getPendingSelfIntent(context, smsCode.withCode(2)));
            views.setOnClickPendingIntent(R.id.bankButton, getPendingSelfIntent(context, smsCode.withCode(3)));
            views.setOnClickPendingIntent(R.id.helpButton, getPendingSelfIntent(context, smsCode.withCode(4)));
            views.setOnClickPendingIntent(R.id.familyButton, getPendingSelfIntent(context, smsCode.withCode(5)));
            views.setOnClickPendingIntent(R.id.runButton, getPendingSelfIntent(context, smsCode.withCode(6)));
            ComponentName watchWidget = new ComponentName(context, SmsWidget.class);
            appWidgetManager.updateAppWidget(watchWidget, views);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        super.onReceive(context, intent);

        AppExecutors.getInstance().diskIO().execute(new Runnable() {

            @Override
            public void run() {
                user = AppDatabase.getDatabase(context).userDao().loadUserByUsage(true);
                Log.d(TAG, "run: " + user.getFullName());
                if (user == null) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, R.string.no_user_available, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                // the intent here gets the action of which sms user pressed
                if (Character.isDigit(intent.getAction().charAt(0))) {
                    if (user != null) {
                        String smsToSend = SmsHelper.createSms(user, intent.getAction());
                        Log.d(TAG, "onReceive: " + smsToSend);
                        updateWidget(context);
                        SmsHelper.sendSms(smsToSend, context);
                    }
                }
            }
        });
    }

    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.sms_widget);
        ComponentName watchWidget = new ComponentName(context, SmsWidget.class);
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    protected PendingIntent getPendingSelfIntent(Context context, SmsCode action) {
        Intent intent = new Intent(context, getClass());
        Log.d(TAG, "getPendingSelfIntent: " + action);
        intent.setAction(String.valueOf(action.code));
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}