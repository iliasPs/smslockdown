package com.ip.smslockdown1.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ip.smslockdown1.db.UserRepository;
import com.ip.smslockdown1.models.User;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class UserViewModel extends AndroidViewModel {

    private static final String TAG = "UserViewModel";
    private final UserRepository userRepository = UserRepository.getInstance();
    private final LiveData<List<User>> usersList;

    public UserViewModel(@NonNull Application application) {
        super(application);

        userRepository.init(application);
        usersList = userRepository.getAllUsers();
        Log.d(TAG, "UserViewModel: " + usersList.getValue());
    }

    public synchronized LiveData<List<User>> getUsersList() {
        return usersList;
    }

    public synchronized void insert(User user) {
        Log.d(TAG, "insert: " + user.getFullName());
        userRepository.insertUser(user);
    }

    public synchronized void deleteUser(User user) {

        userRepository.deleteUser(user);
    }

    public synchronized User getUserByEdit(boolean edit) {

        try {
            return userRepository.getUserByEdit(edit);
        } catch (ExecutionException | InterruptedException e) {
            Log.d(TAG, "getUserByEdit:  Error while getting editable user " + e.getMessage());
        }
        return null;
    }


    public synchronized User getUserByUsage(boolean isLastUsed) {

        try {
            return userRepository.getUserByLastUsed(isLastUsed);
        } catch (Exception e) {
            Log.d(TAG, "getUserByUsage: " + e.getMessage());
        }
        return null;
    }

    public synchronized void updateUser(User user) {

        userRepository.updateUser(user);
    }

    public synchronized void updateUsers(List<User> users) {
        userRepository.updateUsers(users);
    }

}
