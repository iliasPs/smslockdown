package com.ip.smslockdown.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ip.smslockdown.db.UserRepository;
import com.ip.smslockdown.models.User;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private static final String TAG = "UserViewModel";
    private final UserRepository userRepository;
    private final LiveData<List<User>> usersList;

    public UserViewModel(@NonNull Application application) {
        super(application);

        userRepository = new UserRepository(application);
        usersList = userRepository.getAllUsers();
        Log.d(TAG, "UserViewModel: " + usersList.getValue());
    }

    public LiveData<List<User>> getUsersList(){
        return usersList;
    }

    public void insert(User user){
        Log.d(TAG, "insert: " +user.getFullName());
        userRepository.insertUser(user);
    }

    public void deleteUser(User user){

        userRepository.deleteUser(user);
    }


    public User getUserByUsage(boolean isLastUsed)  {

        try {
            return userRepository.getUserByLastUsed(isLastUsed);
        } catch (Exception e) {
            Log.d(TAG, "getUserByUsage: " + e.getMessage());
        }
        return null;
    }

    public void updateUser(User user){

        userRepository.updateUser(user);
    }

    public void updateUsers(List<User> users){
        userRepository.updateUsers(users);
    }

}
