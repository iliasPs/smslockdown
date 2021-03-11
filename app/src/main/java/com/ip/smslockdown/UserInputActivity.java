package com.ip.smslockdown;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.gson.Gson;
import com.ip.smslockdown.databinding.AlertUserInputDialogBinding;
import com.ip.smslockdown.databinding.UserInputBinding;
import com.ip.smslockdown.models.User;
import com.ip.smslockdown.viewmodel.UserViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UserInputActivity extends LocalizationActivity implements UserAdapter.UserClickListener {

    private static final String TAG = "UserInputActivity";
    private final Gson gson = new Gson();
    private Toolbar toolbar;
    private UserInputBinding binding;
    private Button enterUser;
    private Button deleteUser;
    private volatile User currentUser;
    private UserViewModel userViewModel;
    private RecyclerView recyclerView;
    private TextView selectedUserTv;
    private TextView selectedUserAddressTv;

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

        try {
            if(userViewModel.getUserByUsage(true)!=null){
                Log.d(TAG, "onCreate: not null from getUserByUsage ");
                User user = userViewModel.getUserByUsage(true);
                selectedUserTv.setText(user.getFullName());
                selectedUserAddressTv.setText(user.getAddress());
            }
        } catch (Exception e) {
            Log.d(TAG, "onCreate: trying to get last used user failed" + e.getMessage());
        }

        //trying to get last used User from db
        try {
            if (userViewModel.getUserByUsage(true) != null) {
                currentUser = userViewModel.getUserByUsage(true);
            }
        } catch (Exception e) {

            Log.d(TAG, "onCreate: Error on getting user by last used " + Arrays.toString(e.getStackTrace()));
        }

        //setting up spinners
        try {
            setUpRecyclerView();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
                        currentUser = User.builder()
                                .fullName(userNameEdit.getText().toString())
                                .address(userAddressEdit.getText().toString())
                                .build();

                        selectedUserTv.setText(currentUser.getFullName());
                        selectedUserAddressTv.setText(currentUser.getAddress());
                        Log.d(TAG, "onCreate: Adding user to db: " + currentUser.getFullName() + " " + currentUser.getAddress() + " " + currentUser.getUid());
                        userViewModel.insert(currentUser.withLastUsed(true));
                        alertDialog.cancel();

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
                userViewModel.deleteUser(currentUser);
            }
        });

    }

    private void setUpRecyclerView() throws InterruptedException {
        final User clickedUser = User.builder().build();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        final UserAdapter userAdapter = new UserAdapter(this);
        recyclerView.setAdapter(userAdapter);
        userAdapter.setUserListener(this);

        Log.d(TAG, "setUpSpinners: " + userViewModel.getUsersList().getValue());
        userViewModel.getUsersList().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                userAdapter.setData(users);
            }

        });
    }

    private void initViews(UserInputBinding binding) {
        enterUser = binding.enterUserButton;
        deleteUser = binding.deleteUserButton;
        toolbar = binding.toolBar;
        recyclerView = binding.userListRv;
        selectedUserTv = binding.userSelectedName;
        selectedUserAddressTv = binding.userSelectedAddress;
    }

    @Override
    public void onUserClickListener(List<User> users, int position) {

        User selectedUser = users.get(position);
        selectedUserTv.setText(selectedUser.getFullName());
        selectedUserAddressTv.setText(selectedUser.getAddress());

        //setting the last used field on db
        for (User user : users) {
            if (user == selectedUser) {
                user.setLastUsed(true);
            }
            else {
                user.setLastUsed(false);
            }

        }
        Log.d(TAG, "onUserClickListener: " +users.toString());
        userViewModel.updateUser(selectedUser.withLastUsed(true));
        userViewModel.updateUsers(users);
    }

}
