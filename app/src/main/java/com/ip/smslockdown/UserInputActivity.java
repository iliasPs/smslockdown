package com.ip.smslockdown;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.gson.Gson;
import com.ip.smslockdown.databinding.AlertUserInputDialogBinding;
import com.ip.smslockdown.databinding.UserInputBinding;
import com.ip.smslockdown.models.User;
import com.ip.smslockdown.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserInputActivity extends LocalizationActivity {

    private static final String TAG = "UserInputActivity";
    private final Gson gson = new Gson();
    private Toolbar toolbar;
    private UserInputBinding binding;
    private TextView userNameTv;
    private TextView userAddressTv;
    private Button enterUser;
    private Button deleteUser;
    private Button confirmationButton;
    private volatile User currentUser;
    private Spinner userNameSpinner;
    private Spinner userAddressSpinner;
    private UserViewModel userViewModel;

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
        userViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(UserViewModel.class);


        //trying to get last used User from db
        try {
            if (userViewModel.getUserByUsage(true) != null) {
                currentUser = userViewModel.getUserByUsage(true);
                userNameTv.setText(currentUser.getFullName());
                userAddressTv.setText(currentUser.getAddress());
            }
        } catch (Exception e) {
            userNameTv.setText("");
            userAddressTv.setText("");
            Log.d(TAG, "onCreate: Error on getting user by last used " + Arrays.toString(e.getStackTrace()));
        }

        //setting up spinners
        setUpSpinners();

        final User newUser = User.builder().build();


        enterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertDialog = new AlertDialog.Builder(UserInputActivity.this).create();
                AlertUserInputDialogBinding binding = AlertUserInputDialogBinding.inflate(getLayoutInflater());
                final EditText userNameEdit = binding.userNameEt;
                final EditText userAddressEdit = binding.userAddressEt;
                Button locate = binding.locateButton;
                Button saveUserButton = binding.saveButton;
                Button cancelButton = binding.cancelButton;

                //Open Maps
                locate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                // save user to db and fix ui
                saveUserButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        if (currentUser == null) {
                            currentUser = User.builder()
                                    .fullName(userNameEdit.getText().toString())
                                    .address(userAddressEdit.getText().toString())
                                    .build();
//                        } else {
//                            currentUser.setFullName(userNameEdit.getText().toString());
//                            currentUser.setAddress(userAddressEdit.getText().toString());
//                        }


                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

//                            if (currentUser != null) {
//                                userViewModel.updateUser(currentUser, false);
//                                Log.d(TAG, "onCreate: UPDATE user to db: " + currentUser.getFullName() + " " + currentUser.getAddress() + " " + currentUser.getUid());
//
//                            }

                            Log.d(TAG, "onCreate: Adding user to db: " + currentUser.getFullName() + " " + currentUser.getAddress() + " " + currentUser.getUid());
                            userViewModel.insert(currentUser.withLastUsed(true));
                            intent.putExtra("user", gson.toJson(currentUser));
                            startActivity(intent);

                    }
                });

                // just return
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });

                alertDialog.setView(binding.getRoot());
                alertDialog.show();

            }
        });

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAddressTv.setText("");
                userNameTv.setText("");
                userViewModel.deleteUser(currentUser);
            }
        });

    }

    private void setUpSpinners() {
        final User clickedUser = User.builder().build();
        final ArrayAdapter<String> usernameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        usernameAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        userViewModel.getUsersList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                for (User user : users) {
                    usernameAdapter.add(user.getFullName());
                }
                userNameSpinner.setAdapter(usernameAdapter);
            }
        });

        final ArrayAdapter<String> userAddressAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
        userAddressAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        userViewModel.getUsersList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                for (User user : users) {
                    userAddressAdapter.add(user.getAddress());
                }
                userAddressSpinner.setAdapter(userAddressAdapter);
            }
        });


        //spinners listeners
        userAddressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userAddressTv.setText(parent.getItemAtPosition(position).toString());
                clickedUser.setAddress(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        userNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userNameTv.setText(parent.getItemAtPosition(position).toString());
                clickedUser.setFullName(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // if the new pair name and address does not exist in db insert a new user, if it does, update this user last usage
        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (userViewModel.getUserByDetails(clickedUser) == null) {
                        userViewModel.insert(clickedUser.withLastUsed(true));
                        userViewModel.updateUser(currentUser, false);
                    } else {
                        User tempUser = userViewModel.getUserByDetails(clickedUser);
                        userViewModel.updateUser(tempUser, true);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "onClick: User Confirmation retrieval failed " + Arrays.toString(e.getStackTrace()));
                }
            }
        });
    }

    private void initViews(UserInputBinding binding) {
        userNameTv = binding.userNameTv;
        userAddressTv = binding.userAddressTv;
        enterUser = binding.enterUserButton;
        deleteUser = binding.deleteUserButton;
        confirmationButton = binding.userConfirm;
        toolbar = binding.toolBar;
        userNameSpinner = binding.dropDownUserName;
        userAddressSpinner = binding.dropDownUserAddress;
    }
}
