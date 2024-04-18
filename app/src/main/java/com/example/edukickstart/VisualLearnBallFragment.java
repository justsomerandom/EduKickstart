package com.example.edukickstart;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class VisualLearnBallFragment extends Fragment {

    private static final long TIMER_DURATION = 30000; // 30 seconds
    private static final int DATABASE_INDEX = 15;

    private TextView timer;
    private FrameLayout ballFrame;
    private ImageView ballImageView;

    private ObjectAnimator animator;
    private long fingerOnBallTime;
    private boolean timerStarted, isRunning;
    private CountDownTimer ballTouchTimer;

    public VisualLearnBallFragment() {
        // Required empty public constructor
    }

    public static VisualLearnBallFragment newInstance() {
        return new VisualLearnBallFragment();
    }

    @SuppressLint({"ClickableViewAccessibility"})
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_visual_learn_ball, container, false);

        timer = view.findViewById(R.id.timer);
        ballFrame = view.findViewById(R.id.ball_frame);
        ballImageView = ballFrame.findViewById(R.id.ball_image_view);
        timer.setText(String.valueOf((int) (TIMER_DURATION / 1000)));
        timerStarted = false;
        isRunning = false;

        // Set up touch listener for the ball view
        ballImageView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!timerStarted) {
                        timerStarted = true;
                        startTimer();
                        startAnimation();
                    }
                    startBallTime();
                    break;
                case MotionEvent.ACTION_UP:
                    stopBallTime();
                    break;
            }
            return true;
        });

        return view;
    }

    private void startAnimation() {
        // Set the initial position of the ball
        ballImageView.setTranslationX(0);
        ballImageView.setTranslationY(0);

        // Calculate the maximum translation values based on the screen size
        int maxTranslationX = ballFrame.getWidth() - ballImageView.getWidth();
        int maxTranslationY = ballFrame.getHeight() - ballImageView.getHeight();

        // Create the animator for horizontal movement
        animator = ObjectAnimator.ofFloat(ballImageView, "translationX", maxTranslationX);
        animator.setDuration(5000);
        animator.setInterpolator(new LinearInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);

        // Create the animator for vertical movement
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(ballImageView, "translationY", maxTranslationY);
        animatorY.setDuration(3000);
        animatorY.setInterpolator(new LinearInterpolator());
        animatorY.setRepeatCount(ValueAnimator.INFINITE);
        animatorY.setRepeatMode(ValueAnimator.REVERSE);

        // Start the animation
        animator.start();
        animatorY.start();
    }

    private void startTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(TIMER_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                timer.setText(String.valueOf(secondsRemaining));
            }

            @Override
            public void onFinish() {
                animator.cancel();
                int percentage = (int) ((fingerOnBallTime * 100) / TIMER_DURATION);
                UserDatabase database = UserDatabase.getInstance(requireActivity());
                UserDao userDao = database.userDao();
                AppExecutor.getInstance().execute(() -> {
                    User user = userDao.findLoggedInUser();
                    ArrayList<String> progress = user.getProgress();
                    int newWeightedAccuracy;
                    if (progress.get(DATABASE_INDEX).equals("0"))
                        newWeightedAccuracy = percentage;
                    else
                        newWeightedAccuracy = (Integer.parseInt(progress.get(DATABASE_INDEX)) + percentage) / 2;
                    progress.set(DATABASE_INDEX, String.valueOf(newWeightedAccuracy));
                    user.setProgress(progress);
                    userDao.updateUser(user);
                });
                Toast.makeText(requireActivity(), "Percentage held: " + percentage + "%", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.visual_learn_fragment_container, VisualLearnMenuFragment.newInstance())
                        .commit();
            }
        };
        countDownTimer.start();
    }

    private void startBallTime() {
        if (!isRunning) {
            ballTouchTimer = new CountDownTimer(Long.MAX_VALUE, 100) {
                @Override
                public void onTick(long l) {
                    fingerOnBallTime += 100;
                }

                @Override
                public void onFinish() {

                }
            };
            ballTouchTimer.start();
            isRunning = true;
        }
    }

    private void stopBallTime() {
        if (isRunning) {
            ballTouchTimer.cancel();
            isRunning = false;
        }
    }
}