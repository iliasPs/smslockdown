package com.ip.smslockdown.helpers;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.ip.smslockdown.models.SmsCode;
import com.ip.smslockdown.models.User;

public class SmsHelper {

    private static SmsHelper INSTANCE;
    private static final String PHONE_NUMBER = "13033";
    private static final String TAG = "SmsHelper";

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

    public void sendSms(String message, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + PHONE_NUMBER));
        intent.putExtra("sms_body", message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public String createSms(User user, Object obj){

        if(obj instanceof SmsCode) {
            Log.d(TAG, "createSms: " + ((SmsCode) obj).code);
            return (((SmsCode) obj).code + " " + user.getFullName() + " " + user.getAddress());
        }
        return null;
    }

    public String createSms(User user, String action){
            return (action + " " + user.getFullName() + " " + user.getAddress());
    }

}
