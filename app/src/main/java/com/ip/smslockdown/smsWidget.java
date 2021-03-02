package com.ip.smslockdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.ip.smslockdown.models.User;

/**
 * Implementation of App Widget functionality.
 */
public class smsWidget extends AppWidgetProvider {

    private static final String SMS1 = "sms1";
    private static final String SMS2 = "sms2";
    private static final String SMS3 = "sms3";
    private static final String SMS4 = "sms4";
    private static final String SMS5 = "sms5";
    private static final String SMS6 = "sms6";
    private final static String PHONE_NUMBER = "13033";
    User user;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sms_widget);
            views.setOnClickPendingIntent(R.id.docButton, getPendingSelfIntent(context, SMS1));
            views.setOnClickPendingIntent(R.id.shopButton, getPendingSelfIntent(context, SMS2));
            views.setOnClickPendingIntent(R.id.bankButton, getPendingSelfIntent(context, SMS3));
            views.setOnClickPendingIntent(R.id.helpButton, getPendingSelfIntent(context, SMS4));
            views.setOnClickPendingIntent(R.id.familyButton, getPendingSelfIntent(context, SMS5));
            views.setOnClickPendingIntent(R.id.runButton, getPendingSelfIntent(context, SMS6));
            ComponentName watchWidget = new ComponentName(context, smsWidget.class);
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
        SmsHelper smsHelper = SmsHelper.getInstance();

        if (SMS1.equals(intent.getAction())) {
            updateWidget(context);
            if (doesUserExist(context)) {
                user = User.builder().build().getUserFromCache(context);
                smsHelper.sendSms(PHONE_NUMBER, "1 " + user.getFullName() + " " + user.getAddress(), context);
            }
        }
        if (SMS2.equals(intent.getAction())) {
            updateWidget(context);
            if (doesUserExist(context)) {
                user = User.builder().build().getUserFromCache(context);
                smsHelper.sendSms(PHONE_NUMBER, "2 " + user.getFullName() + " " + user.getAddress(), context);
            }
        }
        if (SMS3.equals(intent.getAction())) {
            updateWidget(context);
            if (doesUserExist(context)) {
                user = User.builder().build().getUserFromCache(context);
                smsHelper.sendSms(PHONE_NUMBER, "3 " + user.getFullName() + " " + user.getAddress(), context);
            }
        }
        if (SMS4.equals(intent.getAction())) {
            updateWidget(context);
            if (doesUserExist(context)) {
                user = User.builder().build().getUserFromCache(context);
                smsHelper.sendSms(PHONE_NUMBER, "4 " + user.getFullName() + " " + user.getAddress(), context);
            }
        }
        if (SMS5.equals(intent.getAction())) {
            updateWidget(context);
            if (doesUserExist(context)) {
                user = User.builder().build().getUserFromCache(context);
                smsHelper.sendSms(PHONE_NUMBER, "5 " + user.getFullName() + " " + user.getAddress(), context);
            }
        }
        if (SMS6.equals(intent.getAction())) {
            updateWidget(context);
            if (doesUserExist(context)) {
                user = User.builder().build().getUserFromCache(context);
                smsHelper.sendSms(PHONE_NUMBER, "6 " + user.getFullName() + " " + user.getAddress(), context);
            }
        }
    }


    private void updateWidget(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.sms_widget);
        ComponentName watchWidget = new ComponentName(context, smsWidget.class);
        appWidgetManager.updateAppWidget(watchWidget, remoteViews);
    }

    private boolean doesUserExist(Context c) {
        if (User.builder().build().getUserFromCache(c) != null) {
            return true;
        }
        Toast.makeText(c, R.string.no_user_available, Toast.LENGTH_LONG).show();
        return false;
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}