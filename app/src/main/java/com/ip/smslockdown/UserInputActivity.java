package com.ip.smslockdown;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.gson.Gson;
import com.ip.smslockdown.databinding.UserInputBinding;
import com.ip.smslockdown.models.User;

import java.util.Objects;

public class UserInputActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private UserInputBinding binding;
    private EditText userNameEditText;
    private EditText userAddressEditText;
    private Button enterUser;
    private String name;
    private String address;
    private User user;
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        binding = UserInputBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        initViews(binding);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                user.setAddress(address);
            }
        });

        Log.d("user inp", " user" + name + address);

        enterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                user.saveObject(getApplicationContext());
                intent.putExtra("user", gson.toJson(user));
                startActivity(intent);
            }
        });
//TODO i need to save the user internally


        super.onCreate(savedInstanceState);
    }

    private void initViews(UserInputBinding binding) {
        userNameEditText = binding.userNameEdit;
        userAddressEditText = binding.userAddress;
        enterUser = binding.enterUserButton;
        toolbar = binding.toolBar;
    }
}
