package com.ip.smslockdown.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.ip.smslockdown.helpers.AppExecutors;
import com.ip.smslockdown.models.User;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

public class UserRepository {

    private static final String TAG = "UserRepository";
    private final AppExecutors appExecutors = AppExecutors.getInstance();
    private final UserDao userDao;
    private LiveData<List<User>> usersList;

    public UserRepository(Application application) {

        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        usersList = userDao.getAll();
    }

    public LiveData<List<User>> getAllUsers() {

        return usersList;
    }

    public void insertUser(final User user) {

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userDao.insertUser(user);
            }
        });

    }

    public void deleteUser(final User user) {

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userDao.delete(user);
            }
        });

    }

    public void updateUser(final User user) {

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userDao.updateUser(user);
            }
        });

    }

    public User getUserByLastUsed(boolean lastUsed) throws Exception {

        FutureTask<User> futureTask = new FutureTask<>(new getUserByLastUsedTask(lastUsed));
        Thread t = new Thread(futureTask);
        t.start();

        return futureTask.get();
    }

    public void updateUsers(final List<User> users) {

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                userDao.updateUsers(users);
            }
        });

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

}
