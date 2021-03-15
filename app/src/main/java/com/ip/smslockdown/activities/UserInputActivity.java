package com.ip.smslockdown.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.ip.smslockdown.adapters.UserAdapter;
import com.ip.smslockdown.databinding.UserInputBinding;
import com.ip.smslockdown.models.User;
import com.ip.smslockdown.viewmodel.UserViewModel;

import java.util.List;
import java.util.Objects;

public class UserInputActivity extends LocalizationActivity implements UserAdapter.UserClickListener {

    private static final String TAG = "UserInputActivity";
    private Toolbar toolbar;
    private UserInputBinding binding;
    private Button enterUser;
    private Button deleteUser;
    private volatile User currentUser;
    private UserViewModel userViewModel;
    private RecyclerView recyclerView;
    private TextView selectedUserTv;
    private TextView selectedUserAddressTv;
    private User clickedUserFromRv;
    private UserAdapter userAdapter;

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
            if (userViewModel.getUserByUsage(true) != null) {
                Log.d(TAG, "onCreate: not null from getUserByUsage ");
                User user = userViewModel.getUserByUsage(true);
                selectedUserTv.setText(user.getFullName());
                selectedUserAddressTv.setText(user.getAddress());
            }
        } catch (Exception e) {
            Log.d(TAG, "onCreate: trying to get last used user failed" + e.getMessage());
        }

        //setting up RV
        try {
            setUpRecyclerView();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        deleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewModel.deleteUser(clickedUserFromRv);

                if(userAdapter.getData().size()==1){
                    selectedUserTv.setText("");
                    selectedUserAddressTv.setText("");
                }
            }
        });

        enterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlertDialogActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // i need to make sure i use the last used user, even if it is a new user
        try {
            if (userViewModel.getUserByUsage(true) != null) {
                Log.d(TAG, "onCreate: not null from getUserByUsage ");
                User user = userViewModel.getUserByUsage(true);
                selectedUserTv.setText(user.getFullName());
                selectedUserAddressTv.setText(user.getAddress());
            }
        } catch (Exception e) {
            Log.d(TAG, "onResume: trying to get last used user failed" + e.getMessage());
        }
    }

    private void setUpRecyclerView() throws InterruptedException {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        userAdapter = new UserAdapter(this);
        recyclerView.setAdapter(userAdapter);
        userAdapter.setUserListener(this);
        userViewModel.getUsersList().observe(this, users -> userAdapter.setData(users));
    }

    @Override
    public void onUserClickListener(List<User> users, int position) {

        clickedUserFromRv = users.get(position);
        Log.d(TAG, "onUserClickListener: CLICKED " +users.get(position));

        User selectedUser = users.get(position);
        selectedUserTv.setText(selectedUser.getFullName());
        selectedUserAddressTv.setText(selectedUser.getAddress());

        //setting the last used field on db
        for (User user : users) {
            if (user == selectedUser) {
                user.setLastUsed(true);
            } else {
                user.setLastUsed(false);
            }

        }
        Log.d(TAG, "onUserClickListener: " + users.toString());
        userViewModel.updateUser(selectedUser.withLastUsed(true));
        userViewModel.updateUsers(users);
    }


    private void initViews(UserInputBinding binding) {
        enterUser = binding.enterUserButton;
        deleteUser = binding.deleteUserButton;
        toolbar = binding.toolBar;
        recyclerView = binding.userListRv;
        selectedUserTv = binding.userSelectedName;
        selectedUserAddressTv = binding.userSelectedAddress;
    }


}
