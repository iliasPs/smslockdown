package com.ip.smslockdown;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import com.ip.smslockdown.models.User;
import com.shawnlin.preferencesmanager.PreferencesManager;

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


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sms_widget);



        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        User user= PreferencesManager.getObject("user", User.class);
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.sms_widget);
            views.setOnClickPendingIntent(R.id.docButton, getPendingSelfIntent(context, SMS1, user));
//            views.setOnClickPendingIntent(R.id.shopButton, getPendingSelfIntent(context, SMS2));
//            views.setOnClickPendingIntent(R.id.bankButton, getPendingSelfIntent(context, SMS3));
//            views.setOnClickPendingIntent(R.id.helpButton, getPendingSelfIntent(context, SMS4));
//            views.setOnClickPendingIntent(R.id.familyButton, getPendingSelfIntent(context, SMS5));
//            views.setOnClickPendingIntent(R.id.runButton, getPendingSelfIntent(context, SMS6));
            ComponentName watchWidget = new ComponentName(context, smsWidget.class);
            appWidgetManager.updateAppWidget(watchWidget, views);
        }
    }

    private void sendSms(Context context, String smsNumber, User user){

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + "13033"));
        String message = ("1 " + user.getFullName() + " " + user.getAddress());
        intent.putExtra("sms_body", message);
        context.startActivity(intent);
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
//        SmsHelper smsHelper = SmsHelper.getInstance();
        Log.d("widget", "on receive 1");

        super.onReceive(context, intent);

        if(SMS1.equals(intent.getAction())){
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews remoteViews;
            ComponentName watchWidget;
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.sms_widget);
            watchWidget = new ComponentName(context, smsWidget.class);
            appWidgetManager.updateAppWidget(watchWidget, remoteViews);
//            smsHelper.sendSms(PHONE_NUMBER, "1 " + user.getFullName() + " " + user.getAddress());

        }
//        if(mainActivity.user!=null)
//        switch (intent.getAction()){
//            case(SMS1):
//                Log.d("widget", "on receive 1");
//                Toast.makeText(context, "Button1", Toast.LENGTH_SHORT).show();
//                mainActivity.sendSms(PHONE_NUMBER, "1 " + user.getFullName() + " " + user.getAddress());
//                break;
//            case(SMS2):
//                mainActivity.sendSms(PHONE_NUMBER, "2 " + mainActivity.getUser().getFullName() + " " + mainActivity.getUser().getAddress());
//                break;
//            case(SMS3):
//                mainActivity.sendSms(PHONE_NUMBER, "3 " + mainActivity.getUser().getFullName() + " " + mainActivity.getUser().getAddress());
//                break;
//            case(SMS4):
//                mainActivity.sendSms(PHONE_NUMBER, "4 " + mainActivity.getUser().getFullName() + " " + mainActivity.getUser().getAddress());
//                break;
//            case(SMS5):
//                mainActivity.sendSms(PHONE_NUMBER, "5 " + mainActivity.getUser().getFullName() + " " + mainActivity.getUser().getAddress());
//                break;
//            case(SMS6):
//                mainActivity.sendSms(PHONE_NUMBER, "6 " + mainActivity.getUser().getFullName() + " " + mainActivity.getUser().getAddress());
//                break;
//            default:
//        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action, User user) {
        Log.d("widget", "pending intent " + action);
        Intent intent = new Intent(context, getClass());
        intent.setAction(Intent.ACTION_VIEW);
        sendSms(context, "1", user);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
}