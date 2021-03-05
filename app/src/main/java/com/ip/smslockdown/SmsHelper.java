package com.ip.smslockdown;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RadioButton;

import com.ip.smslockdown.models.User;

public class SmsHelper {

    private static SmsHelper INSTANCE;
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

    public void sendSms(String phoneNumber, String message, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public String createSms(User user, RadioButton button){

        Log.d(TAG, "createSms: " + button.getTag().toString());
        return (button.getTag() + " " + user.getFullName() + " " + user.getAddress());
    }

}
