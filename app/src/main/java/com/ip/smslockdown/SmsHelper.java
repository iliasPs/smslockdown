package com.ip.smslockdown;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SmsHelper {

    private static SmsHelper INSTANCE;

    private SmsHelper(){

    }

    public static SmsHelper getInstance(){
        synchronized (SmsHelper.class){
            if (INSTANCE == null){
                INSTANCE = new SmsHelper();
            }
        }
        return INSTANCE;
    }

    public void sendSms(String phoneNumber, String message, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
