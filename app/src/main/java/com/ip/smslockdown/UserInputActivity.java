package com.ip.smslockdown;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.gson.Gson;
import com.ip.smslockdown.databinding.UserInputBinding;
import com.ip.smslockdown.models.User;

import java.util.Objects;

public class UserInputActivity extends LocalizationActivity {

    private final Gson gson = new Gson();
    private Toolbar toolbar;
    private UserInputBinding binding;
    private EditText userNameEditText;
    private EditText userAddressEditText;
    private Button enterUser;
    private Button deleteUser;
    private String name;
    private String address;
    private User user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UserInputBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initViews(binding);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setLanguage(getCurrentLanguage());

        // checking if user exists in cache
        if (User.builder().build().getUserFromCache(getApplicationContext()) != null) {
            userAddressEditText.setText(User.builder().build().getUserFromCache(getApplicationContext()).getAddress());
            userNameEditText.setText(User.builder().build().getUserFromCache(getApplicationContext()).getFullName());
        }

        userNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                name = userNameEditText.getText().toString();
                user = User.builder().fullName(name).build();
            }
        });

        userAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                address = userAddressEditText.getText().toString();
                if (user != null) {
                    user.setAddress(address);
                }
            }
        });

        Log.d("user inp", " user" + name + address);

        enterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                if (user == null || user.getFullName().isEmpty() || user.getAddress().isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.error_saving_user, Toast.LENGTH_LONG).show();
                } else {
                    user.saveUserToCache(getApplicationContext());
                    intent.putExtra("user", gson.toJson(user));
                    startActivity(intent);
                }
            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAddressEditText.getText().clear();
                userNameEditText.getText().clear();
                user.deleteUserFromCache(getApplicationContext());
            }
        });

    }

    private void initViews(UserInputBinding binding) {
        userNameEditText = binding.userNameEdit;
        userAddressEditText = binding.userAddress;
        enterUser = binding.enterUserButton;
        deleteUser = binding.deleteUserButton;
        toolbar = binding.toolBar;
    }
}
