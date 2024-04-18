package com.example.edukickstart;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class User {
    @PrimaryKey
    public int id;

    public String username;
    public String password;
    public boolean loggedIn;
    public ArrayList<String> progress;
    public ArrayList<String> highScores;

    public int getUserId() {
        return id;
    }
    public void setUserId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }
    public void setLoggedIn(boolean loggedInStatus) {
        loggedIn = loggedInStatus;
    }

    public ArrayList<String> getProgress() {
        return progress;
    }
    public void setProgress(ArrayList<String> progressScores) {
        progress = progressScores;
    }

    public ArrayList<String> getHighScores() {
        return highScores;
    }
    public void setHighScores(ArrayList<String> highScores) {
        this.highScores = highScores;
    }
}