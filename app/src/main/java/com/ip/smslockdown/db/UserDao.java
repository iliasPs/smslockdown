package com.ip.smslockdown.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.ip.smslockdown.models.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    LiveData<List<User>> getAll();

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    LiveData<List<User>> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    User loadUserById(int userIds);

    @Query("SELECT * FROM user WHERE last_used=:lastUsed")
    User loadUserByUsage(boolean lastUsed);

    @Update
    void updateUser(User user);

    @Query("SELECT * FROM user WHERE full_name= :fullName AND address= :address")
    User getUserFromNameAndAddress(String fullName, String address);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertUser(User user);

    @Delete
    void delete(User user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void updateUsers(List<User> users);
}

