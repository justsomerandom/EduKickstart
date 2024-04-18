package com.example.edukickstart;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User WHERE id LIKE :id")
    User findById(int id);

    @Query("SELECT * FROM User WHERE username LIKE :username")
    User findByUsername(String username);

    @Query("SELECT * FROM User WHERE loggedIn LIKE 1")
    User findLoggedInUser();

    @Insert
    void insert(User user);

    @Insert
    void insertAll(User... users);

    @Update
    void updateUser(User user);

    @Delete
    void delete(User user);

    @Delete
    void deleteAll(User... users);
}
