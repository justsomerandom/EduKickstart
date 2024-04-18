package com.example.edukickstart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.edukickstart.data.LoginDataSource;
import com.example.edukickstart.data.LoginRepository;
import com.example.edukickstart.ui.login.LoginActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ScrollView statView;
    private TextView userContentView;
    private final UserDatabase database = UserDatabase.getInstance(this);
    private final UserDao userDao = database.userDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statView = findViewById(R.id.stats_container);
        userContentView = findViewById(R.id.table_user_content);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateStatsTable();
    }

    private void updateStatsTable() {
        String percentage = getResources().getString(R.string.stats_percentage);
        String score = getResources().getString(R.string.stats_score);

        AppExecutor.getInstance().execute(() -> {
            User loggedInUser = userDao.findLoggedInUser();
            userContentView.setText(String.format(getResources().getString(R.string.logged_in_user), loggedInUser.getUsername()));

            String percentTemp, scoreTemp;
            TextView percentView, scoreView;
            ArrayList<TextView> overallViews = new ArrayList<TextView>() {
                {
                    add(statView.findViewById(R.id.table_time_overall_content));
                    add(statView.findViewById(R.id.table_memory_overall_content));
                    add(statView.findViewById(R.id.table_lang_overall_content));
                    add(statView.findViewById(R.id.table_maths_overall_content));
                    add(statView.findViewById(R.id.table_visual_overall_content));
                }
            };
            ArrayList<Integer> overalls = new ArrayList<Integer>() {
                {
                    add(0);
                    add(0);
                    add(0);
                    add(0);
                    add(0);
                }
            };

            for (int i = 0; i < 16; i++) {
                if (i < 5)
                    overalls.set(0, overalls.get(0) + Integer.parseInt(loggedInUser.getProgress().get(i)));
                else if (i < 8)
                    overalls.set(1, overalls.get(1) + Integer.parseInt(loggedInUser.getProgress().get(i)));
                else if (i < 11)
                    overalls.set(2, overalls.get(2) + Integer.parseInt(loggedInUser.getProgress().get(i)));
                else if (i < 14)
                    overalls.set(3, overalls.get(3) + Integer.parseInt(loggedInUser.getProgress().get(i)));
                else
                    overalls.set(4, overalls.get(4) + Integer.parseInt(loggedInUser.getProgress().get(i)));

                switch (i) {
                    case 0:
                        percentView = statView.findViewById(R.id.table_analog_content);
                        scoreView = statView.findViewById(R.id.table_clocks_content);
                        break;
                    case 1:
                        percentView = statView.findViewById(R.id.table_digital_content);
                        scoreView = statView.findViewById(R.id.table_calendar_content);
                        break;
                    case 2:
                        percentView = statView.findViewById(R.id.table_week_content);
                        scoreView = statView.findViewById(R.id.table_time_mix_content);
                        break;
                    case 3:
                        percentView = statView.findViewById(R.id.table_month_content);
                        scoreView = statView.findViewById(R.id.table_digits_content);
                        break;
                    case 4:
                        percentView = statView.findViewById(R.id.table_season_content);
                        scoreView = statView.findViewById(R.id.table_images_play_content);
                        break;
                    case 5:
                        percentView = statView.findViewById(R.id.table_forward_content);
                        scoreView = statView.findViewById(R.id.table_memory_mix_content);
                        break;
                    case 6:
                        percentView = statView.findViewById(R.id.table_backward_content);
                        scoreView = statView.findViewById(R.id.table_words_content);
                        break;
                    case 7:
                        percentView = statView.findViewById(R.id.table_images_content);
                        scoreView = statView.findViewById(R.id.table_direct_play_content);
                        break;
                    case 8:
                        percentView = statView.findViewById(R.id.table_spell_content);
                        scoreView = statView.findViewById(R.id.table_lang_mix_content);
                        break;
                    case 9:
                        percentView = statView.findViewById(R.id.table_write_content);
                        scoreView = statView.findViewById(R.id.table_add_play_content);
                        break;
                    case 10:
                        percentView = statView.findViewById(R.id.table_direct_content);
                        scoreView = statView.findViewById(R.id.table_sub_play_content);
                        break;
                    case 11:
                        percentView = statView.findViewById(R.id.table_add_content);
                        scoreView = statView.findViewById(R.id.table_multiply_play_content);
                        break;
                    case 12:
                        percentView = statView.findViewById(R.id.table_sub_content);
                        scoreView = statView.findViewById(R.id.table_maths_mix_content);
                        break;
                    case 13:
                        percentView = statView.findViewById(R.id.table_multiply_content);
                        scoreView = statView.findViewById(R.id.table_match_play_content);
                        break;
                    case 14:
                        percentView = statView.findViewById(R.id.table_match_content);
                        scoreView = statView.findViewById(R.id.table_ball_play_content);
                        break;
                    case 15:
                        percentView = statView.findViewById(R.id.table_ball_content);
                        scoreView = statView.findViewById(R.id.table_visual_mix_content);
                        break;
                    default:
                        continue;
                }
                if (percentView != null) {
                    percentTemp = String.format(percentage, loggedInUser.getProgress().get(i));
                    percentView.setText(percentTemp);
                }
                scoreTemp = String.format(score, loggedInUser.getHighScores().get(i));
                scoreView.setText(scoreTemp);
            }

            overalls.set(0, overalls.get(0) / 5);
            overalls.set(1, overalls.get(1) / 3);
            overalls.set(2, overalls.get(2) / 3);
            overalls.set(3, overalls.get(3) / 3);
            overalls.set(4, overalls.get(4) / 2);

            for (int i = 0; i < 5; i++)
                overallViews.get(i).setText(String.format(getResources().getString(R.string.stats_overall_percent), overalls.get(i)));
        });
    }

    public void learnMenu(View view) {
        Intent i = new Intent(this, LearnActivity.class);
        startActivity(i);
    }

    public void playMenu(View view) {
        Intent i = new Intent(this, PlayActivity.class);
        startActivity(i);
    }

    public void statTable(View view) {
        if (statView != null) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_bottom);
            statView.startAnimation(animation);
            statView.setVisibility(View.VISIBLE);
            statView.setZ(10.0f);
        }
    }

    public void logOut(View view) {
        LoginRepository loginRepository = LoginRepository.getInstance(new LoginDataSource(this));
        loginRepository.logout();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void dismissTable(View view) {
        if(statView != null) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
            statView.startAnimation(animation);
            statView.setVisibility(View.GONE);
        }
    }
}