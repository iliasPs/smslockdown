package com.ip.smslockdown1.activities;

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
import com.ip.smslockdown1.adapters.UserAdapter;
import com.ip.smslockdown1.databinding.UserInputBinding;
import com.ip.smslockdown1.helpers.AppExecutors;
import com.ip.smslockdown1.models.User;
import com.ip.smslockdown1.viewmodel.UserViewModel;

import java.util.List;
import java.util.Objects;

public class UserInputActivity extends LocalizationActivity implements UserAdapter.UserClickListener {

    private static final String TAG = "UserInputActivity";
    private Toolbar toolbar;
    private UserInputBinding binding;
    private Button enterUser;
    private Button deleteUser;
    private UserViewModel userViewModel;
    private RecyclerView recyclerView;
    private TextView selectedUserTv;
    private TextView selectedUserAddressTv;
    private UserAdapter userAdapter;
    private Button editUser;
    private User userClickedFromList;

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
            final User[] user = new User[1];
            AppExecutors.getInstance().diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    user[0] = userViewModel.getUserByUsage(true);
                }
            });

            if (user[0] != null) {
                Log.d(TAG, "onCreate: not null from getUserByUsage ");
                userClickedFromList = user[0];
                selectedUserTv.setText(userClickedFromList.getFullName());
                selectedUserAddressTv.setText(userClickedFromList.getAddress());
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
                userViewModel.deleteUser(userViewModel.getUserByUsage(true));
                selectedUserTv.setText("");
                selectedUserAddressTv.setText("");
            }
        });

        enterUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AlertDialogActivity.class);
                startActivity(intent);
            }
        });

        editUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userClickedFromList != null) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            userViewModel.updateUser(userClickedFromList.withToBeEdited(true));
                        }
                    });
                    Intent intent = new Intent(getApplicationContext(), AlertDialogActivity.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserEditable();

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
        userAdapter = new UserAdapter(this, this);
        recyclerView.setAdapter(userAdapter);
        userAdapter.setUserListener(this);
        updateUserEditable();
        userViewModel.getUsersList().observe(this, (List<User> users) -> {
            userAdapter.setData(users);
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        updateUserEditable();
    }

    private void updateUserEditable() {
        //setting all users to edit=false
        List<User> users = userViewModel.getUsersList().getValue();
        if (users != null) {
            for (User user : users) {
                user.setToBeEdited(false);
            }
            userViewModel.updateUsers(users);
        }
    }

    @Override
    public void onUserClickListener(List<User> users, int position) {

        userClickedFromList = users.get(position);
        selectedUserTv.setText(userClickedFromList.getFullName());
        selectedUserAddressTv.setText(userClickedFromList.getAddress());

        //setting the last used field on db
        for (User user : users) {
            if (user == userClickedFromList) {
                user.setLastUsed(true);
            } else {
                user.setLastUsed(false);
            }

        }
        Log.d(TAG, "onUserClickListener: " + users.toString());
        userViewModel.updateUser(userClickedFromList.withLastUsed(true));
        userViewModel.updateUsers(users);
    }


    private void initViews(UserInputBinding binding) {
        editUser = binding.editUserButton;
        enterUser = binding.enterUserButton;
        deleteUser = binding.deleteUserButton;
        toolbar = binding.toolBar;
        recyclerView = binding.userListRv;
        selectedUserTv = binding.userSelectedName;
        selectedUserAddressTv = binding.userSelectedAddress;
    }


}
