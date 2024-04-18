package com.example.edukickstart.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.edukickstart.AppExecutor;
import com.example.edukickstart.User;
import com.example.edukickstart.UserDao;
import com.example.edukickstart.UserDatabase;
import com.example.edukickstart.data.model.LoggedInUser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
@SuppressWarnings("rawtypes")
public class LoginDataSource {

    private final UserDao userDao;
    private final Context context;

    public LoginDataSource(Context context) {
        UserDatabase database = UserDatabase.getInstance(context);
        this.context = context;
        userDao = database.userDao();
    }

    @SuppressWarnings("unchecked")
    public Result<LoggedInUser> login(String username, String password) {
        try {
            User user = userDao.findByUsername(username);
            if (user != null && user.getPassword().equals(password)) {
                LoggedInUser loggedInUser =
                        new LoggedInUser(
                                UUID.randomUUID().toString(),
                                user.getUsername());
                logout();
                user.setLoggedIn(true);
                userDao.updateUser(user);
                Log.d("LOGIN", "Successfully logged in");
                return new Result.Success<>(loggedInUser);
            } else if (user == null) {
                user = new User();
                user.setUserId(userDao.getAll().size());
                user.setUsername(username);
                user.setPassword(password);
                user.setLoggedIn(true);
                ArrayList<String> progress = new ArrayList<>();
                ArrayList<String> highScores = new ArrayList<>();
                for (int i = 0; i < 16; i++) {
                    progress.add("0");
                    highScores.add("0");
                }
                user.setProgress(progress);
                user.setHighScores(highScores);
                userDao.insert(user);
                LoggedInUser loggedInUser =
                        new LoggedInUser(
                                UUID.randomUUID().toString(),
                                user.getUsername());
                Log.d("LOGIN", "Successfully created new user");
                return new Result.Success<>(loggedInUser);
            } else {
                Log.d("LOGIN", "Login failed, incorrect password");
                Toast.makeText(context, "Incorrect password for " + user.username, Toast.LENGTH_LONG).show();
                return new Result.Error(new Exception("Incorrect password"));
            }
        } catch (Exception e) {
            Log.d("LOGIN", "Login failed with exception " + e);
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        AppExecutor.getInstance().execute(() -> {
            User user = userDao.findLoggedInUser();
            if (user != null) {
                user.setLoggedIn(false);
                userDao.updateUser(user);
            }
        });
    }
}