package com.ip.smslockdown.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.room.Room;

import com.ip.smslockdown.R;
import com.ip.smslockdown.db.AppDatabase;
import com.ip.smslockdown.db.UserDao;
import com.ip.smslockdown.helpers.SmsHelper;
import com.ip.smslockdown.models.SmsCode;
import com.ip.smslockdown.models.User;

/**
 * Implementation of App Widget functionality.
 */
public class SmsWidget extends AppWidgetProvider {

    private static final SmsHelper smsHelper = SmsHelper.getInstance();
    User user;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        SmsCode smsCode = SmsCode.builder().build();
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sms_widget);
            views.setOnClickPendingIntent(R.id.docButton, getPendingSelfIntent(context, String.valueOf(smsCode.withCode(1))));
            views.setOnClickPendingIntent(R.id.shopButton, getPendingSelfIntent(context, String.valueOf(smsCode.withCode(2))));
            views.setOnClickPendingIntent(R.id.bankButton, getPendingSelfIntent(context, String.valueOf(smsCode.withCode(3))));
            views.setOnClickPendingIntent(R.id.helpButton, getPendingSelfIntent(context, String.valueOf(smsCode.withCode(4))));
            views.setOnClickPendingIntent(R.id.familyButton, getPendingSelfIntent(context, String.valueOf(smsCode.withCode(5))));
            views.setOnClickPendingIntent(R.id.runButton, getPendingSelfIntent(context, String.valueOf(smsCode.withCode(6))));
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
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);


        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "database-name").build();
         UserDao userDao = db.userDao();

        if(userDao.loadUserByUsage(true)!=null){
            user = userDao.loadUserByUsage(true);
        }

        if (!doesUserExist(user, context)) {
            return;
        }

        String smsToSend = smsHelper.createSms(user, intent.getAction());
        updateWidget(context);
        smsHelper.sendSms(smsToSend, context);
    }


    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.sms_widget);
        ComponentName watchWidget = new ComponentName(context, SmsWidget.class);
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    private boolean doesUserExist(User user, Context context) {
        if (user != null) {
            return true;
        }
        Toast.makeText(context, R.string.no_user_available, Toast.LENGTH_LONG).show();
        return false;
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}