package com.ip.smslockdown1.db;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.ip.smslockdown1.helpers.AppExecutors;
import com.ip.smslockdown1.models.User;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class UserRepository {

    private static final String TAG = "UserRepository";
    private static UserRepository instance;
    private final AppExecutors appExecutors = AppExecutors.getInstance();
    private UserDao userDao;
    private LiveData<List<User>> usersList;

    public static UserRepository getInstance() {
        if (instance == null) {
            synchronized (UserRepository.class) {
                if (instance == null) {
                    instance = new UserRepository();
                }
            }
        }
        return instance;
    }

    public void init(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        userDao = db.userDao();
        usersList = userDao.getAll();
    }


    public synchronized LiveData<List<User>> getAllUsers() {

        return usersList;
    }

    public synchronized void insertUser(final User user) {

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "insertUser: THREAD RUN");
                userDao.insertUser(user);
            }
        });
    }

    public synchronized void deleteUser(final User user) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "deleteUser: THREAD RUN");
                userDao.delete(user);
            }
        });
    }

    public synchronized void updateUser(final User user) {

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "updateUser: THREAD RUN");
                userDao.updateUser(user);
            }
        });
    }

    public synchronized User getUserByEdit(boolean toBeEdited) throws ExecutionException, InterruptedException {

        FutureTask<User> future = new FutureTask<>(new getUserByEdit(toBeEdited));
        Thread t = new Thread(future);
        t.start();
        Log.d(TAG, "getUserByEdit: THREAD RUN");
        return future.get();
    }


    public synchronized User getUserByLastUsed(boolean lastUsed) throws Exception {

        FutureTask<User> futureTask = new FutureTask<>(new getUserByLastUsedTask(lastUsed));
        Thread t = new Thread(futureTask);
        t.start();
        Log.d(TAG, "getUserByLastUsed: THREAD RUN");
        return futureTask.get();
    }

    public synchronized void updateUsers(final List<User> users) {

        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "updateUsers: THREAD RUN");
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

    private class getUserByEdit implements Callable<User> {

        boolean toBeEdited;

        public getUserByEdit(boolean toBeEdited) {
            this.toBeEdited = toBeEdited;
        }

        @Override
        public User call() throws Exception {
            return userDao.loadUserByEdit(toBeEdited);
        }
    }

}
