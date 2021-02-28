package com.ip.smslockdown;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.ip.smslockdown.databinding.ActivityMainBinding;
import com.ip.smslockdown.models.User;
import com.shawnlin.preferencesmanager.PreferencesManager;

public class MainActivity extends LocalizationActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String PHONE_NUMBER = "13033";
    private final Gson gson = new Gson();
    private AdView adView;
    private ActivityMainBinding binding;
    private TextView descriptionTv;
    private TextView userName;
    private TextView userAddress;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RadioButton radioButton5;
    private RadioButton radioButton6;
    private Button sendButton;
    private Toolbar toolbar;
    private User user;
    private String smsToSend;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initViews(binding);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        RadioGroup radioGroup = binding.smsRadioGroup;
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        new PreferencesManager(this).setName("manager").init();

        if (PreferencesManager.getObject("user", User.class) != null) {
            user = PreferencesManager.getObject("user", User.class);
            userName.setText(user.getFullName());
            userAddress.setText(user.getAddress());
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (i == radioButton1.getId()) {
                    descriptionTv.setText(R.string.sms1_desc);
                    smsToSend = ("1 " + userName.getText() + " " + userAddress.getText());
                } else if (i == radioButton2.getId()) {
                    smsToSend = ("2 " + userName.getText() + " " + userAddress.getText());
                    descriptionTv.setText(R.string.sms2_desc);
                } else if (i == radioButton3.getId()) {
                    smsToSend = ("3 " + userName.getText() + " " + userAddress.getText());
                    descriptionTv.setText(R.string.sms3_desc);
                } else if (i == radioButton4.getId()) {
                    smsToSend = ("4 " + userName.getText() + " " + userAddress.getText());
                    descriptionTv.setText(R.string.sms4_desc);
                } else if (i == radioButton5.getId()) {
                    smsToSend = ("5 " + userName.getText() + " " + userAddress.getText());
                    descriptionTv.setText(R.string.sms5_desc);
                } else if (i == radioButton6.getId()) {
                    smsToSend = ("6 " + userName.getText() + " " + userAddress.getText());
                    descriptionTv.setText(R.string.sms6_desc);
                }

            }
        });

        Intent intent = getIntent();

        if (intent.getExtras() != null) {
            String userString = intent.getStringExtra("user");
            user = gson.fromJson(userString, User.class);
            try {
                userName.setText(user.getFullName());
                userAddress.setText(user.getAddress());
            } catch (NullPointerException e) {
                Log.e(TAG, "Username or Address is null");
            }
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSMSPermissionGranted()) {
                    sendSms(smsToSend);
                    Log.d(TAG, "sms to send " + smsToSend);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.english_option) {
            setLanguage("en");
        }

        if (item.getItemId() == R.id.greek_option) {
            setLanguage("gr");
        }

        if (item.getItemId() == R.id.new_user) {
            startActivity(new Intent(getApplicationContext(), UserInputActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PreferencesManager.putObject("user", user);
    }

    private boolean isSMSPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {
                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 0);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }


    private void sendSms(String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(MainActivity.PHONE_NUMBER, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    private void initViews(ActivityMainBinding binding) {

        toolbar = binding.toolBar;
        descriptionTv = binding.descriptionText;
        userName = binding.userName;
        userAddress = binding.userAddress;
        radioButton1 = binding.sms1;
        radioButton2 = binding.sms2;
        radioButton3 = binding.sms3;
        radioButton4 = binding.sms4;
        radioButton5 = binding.sms5;
        radioButton6 = binding.sms6;
        sendButton = binding.smsButton;
        adView = binding.adView;

        descriptionTv.setMovementMethod(new ScrollingMovementMethod());
    }
}
