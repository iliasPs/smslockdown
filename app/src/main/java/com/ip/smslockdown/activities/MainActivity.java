package com.ip.smslockdown.activities;

import android.content.Intent;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.gson.Gson;
import com.ip.smslockdown.R;
import com.ip.smslockdown.databinding.ActivityMainBinding;
import com.ip.smslockdown.helpers.SmsHelper;
import com.ip.smslockdown.helpers.TimerHelper;
import com.ip.smslockdown.models.SmsCode;
import com.ip.smslockdown.models.User;
import com.ip.smslockdown.viewmodel.UserViewModel;

public class MainActivity extends LocalizationActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private final Gson gson = new Gson();
    public User user;
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
    private String smsToSend;
    private UserViewModel userViewModel;

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
        userViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(UserViewModel.class);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        try {
            if (userViewModel.getUserByUsage(true) != null) {
                user = userViewModel.getUserByUsage(true);
                userName.setText(user.getFullName());
                userAddress.setText(user.getAddress());
            }
        } catch (Exception e) {
            Log.d(TAG, "onCreate: trying to get last used user failed" + e.getMessage());
        }

        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            String[] smsArray = getResources().getStringArray(R.array.sms_desc);
            int id = radioGroup1.getCheckedRadioButtonId();
            descriptionTv.setText(smsArray[((SmsCode) radioGroup1.findViewById(id).getTag()).code - 1]);
            smsToSend = SmsHelper.createSms(user, radioGroup1.findViewById(id).getTag());
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

        sendButton.setOnClickListener(v -> {
            SmsHelper.sendSms(smsToSend, getApplicationContext());
            Log.d(TAG, "sms to send " + smsToSend);
            TimerHelper.createTimer(getApplicationContext());
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

    private void initViews(ActivityMainBinding binding) {

        toolbar = binding.toolBar;
        descriptionTv = binding.descriptionText;
        userName = binding.userName;
        userAddress = binding.userAddressTv;
        radioButton1 = binding.sms1;
        radioButton2 = binding.sms2;
        radioButton3 = binding.sms3;
        radioButton4 = binding.sms4;
        radioButton5 = binding.sms5;
        radioButton6 = binding.sms6;
        sendButton = binding.smsButton;
        adView = binding.adView;

        SmsCode code = SmsCode.builder().build();
        radioButton1.setTag(code.withCode(1));
        radioButton2.setTag(code.withCode(2));
        radioButton3.setTag(code.withCode(3));
        radioButton4.setTag(code.withCode(4));
        radioButton5.setTag(code.withCode(5));
        radioButton6.setTag(code.withCode(6));

        descriptionTv.setMovementMethod(new ScrollingMovementMethod());
    }

}
