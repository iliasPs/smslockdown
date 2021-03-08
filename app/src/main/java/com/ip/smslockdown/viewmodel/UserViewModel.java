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
    private UserRepository userRepository;
    private LiveData<List<User>> usersList;

    public UserViewModel(@NonNull Application application) {
        super(application);

        userRepository = new UserRepository(application);
        usersList = userRepository.getAllUsers();
    }

    public LiveData<List<User>> getUsersList(){
        return usersList;
    }

    public void insert(User user){

        userRepository.insertUser(user);
    }

    public void deleteUser(User user){

        userRepository.deleteUser(user);
    }

    public Boolean checkUserExist(User user){

      return userRepository.isUserInDb(user);
    }

    public User getUser(int id) throws Exception {

       return userRepository.getUser(id);
    }

    public User getUserByUsage(boolean isLastUsed)  {


        try {
            return userRepository.getUserByLastUsed(isLastUsed);
        } catch (Exception e) {
            Log.d(TAG, "getUserByUsage: " + e.getMessage());
        }
        return null;
    }

    public void updateUser(User user, boolean lastUsed){

        userRepository.updateUser(user, lastUsed);
    }

    public User getUserByDetails(User user) throws Exception {

        return userRepository.getUserFromNameAndAddress(user.getFullName(), user.getAddress());
    }

}
