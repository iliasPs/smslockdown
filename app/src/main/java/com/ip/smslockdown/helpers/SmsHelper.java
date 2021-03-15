package com.ip.smslockdown.helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.ip.smslockdown.models.SmsCode;
import com.ip.smslockdown.models.User;

public class SmsHelper extends ContextWrapper {

    private static final String PHONE_NUMBER = "13033";
    private static final String PHONE_NUMBER_COMMERCIAL = "13032";
    private static final String MOVEMENT = "ΜΕΤΑΚΙΝΗΣΗ";
    private static final String COMMERCIAL = "ΕΜΠΟΡΙΚΟ ΚΑΤΑΣΤΗΜΑ";
    private static final String TAG = "SmsHelper";

    public SmsHelper(Context base) {
        super(base);
    }


    public static void sendSms(String message, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + PHONE_NUMBER));
        intent.putExtra("sms_body", message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void sendCommercialSms(String message, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + PHONE_NUMBER_COMMERCIAL));
        intent.putExtra("sms_body", message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    public static String createSms(User user, Object obj){

        if(obj instanceof SmsCode) {
            Log.d(TAG, "createSms: " + ((SmsCode) obj).code);
            return (((SmsCode) obj).code + " " + user.getFullName() + " " + user.getAddress());
        }
        return null;
    }

    public static String createSms(User user, String action){
            return (action + " " + user.getFullName() + " " + user.getAddress());
    }

    public static String createCommercialSms(User user){
        return (MOVEMENT + user.getFullName() + COMMERCIAL);
    }

}
