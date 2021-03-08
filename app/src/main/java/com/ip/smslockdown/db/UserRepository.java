package com.ip.smslockdown.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.ip.smslockdown.models.User;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class UserRepository {

    private UserDao userDao;
    private LiveData<List<User>> usersList;

    public UserRepository(Application application) {

        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        usersList = userDao.getAll();
    }

    public LiveData<List<User>> getAllUsers() {

        return usersList;
    }

    public void insertUser(User user) {

        new insertUserTask(user).start();
    }

    public void deleteUser(User user) {

        new deleteUserTask(user).start();
    }

    public void updateUser(User user, boolean lastUsed) {

        new updateUserTask(user, lastUsed);
    }

    public Boolean isUserInDb(User user) {

        try {
            return new checkIfUserExists(user).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUser(int userId) throws Exception {

        return new getUserTask(userId).call();
    }

    public User getUserByLastUsed(boolean lastUsed) throws Exception {

        FutureTask<User> futureTask = new FutureTask<>(new getUserByLastUsedTask(lastUsed));
        Thread t = new Thread(futureTask);
        t.start();

        return futureTask.get();
    }

    public User getUserFromNameAndAddress(String fullName, String address) throws Exception {

        return new getUserFromDetails(fullName, address).call();
    }

    private class getUserFromDetails implements Callable<User> {

        String fullName;
        String address;

        public getUserFromDetails(String fullName, String address) {
            this.fullName = fullName;
            this.address = address;

        }

        @Override
        public User call() throws Exception {
            return userDao.getUserFromNameAndAddress(fullName, address);
        }
    }

    private class getUserByLastUsedTask implements Callable<User> {

        boolean lastUsed;

        public getUserByLastUsedTask(boolean lastUsed) {
            this.lastUsed = lastUsed;
        }

        @Override
        public User call() throws Exception {
            return userDao.loadUserByUsage(lastUsed);
        }
    }

    private class getUserTask implements Callable<User> {

        int userId;

        public getUserTask(int userId) {
            this.userId = userId;
        }

        @Override
        public User call() throws Exception {
            return userDao.loadUserById(userId);
        }
    }

    private class insertUserTask extends Thread {

        User user;

        public insertUserTask(User user) {
            this.user = user;
        }

        @Override
        public void run() {

            userDao.insertUser(user);
        }
    }

    private class checkIfUserExists implements Callable<Boolean> {

        User user;

        public checkIfUserExists(User user) {

            this.user = user;
        }

        @Override
        public Boolean call() throws Exception {
            return userDao.loadUserById(user.getUid()) != null;
        }
    }

    private class deleteUserTask extends Thread {

        User user;

        public deleteUserTask(User user) {
            this.user = user;
        }

        @Override
        public void run() {

            userDao.delete(user);
        }
    }

    private class updateUserTask extends Thread {

        User user;
        boolean lastUsed;

        public updateUserTask(User user, boolean lastUsed) {
            this.user = user;
            this.lastUsed = lastUsed;
        }

        @Override
        public void run() {

            userDao.updateUser(user.getUid(), lastUsed);
        }
    }

}
