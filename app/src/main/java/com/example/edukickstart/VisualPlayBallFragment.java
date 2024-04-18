package com.example.edukickstart;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisualPlayBallFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisualPlayBallFragment extends Fragment {

    private static final String ARG_MIX = "Mix";
    private static final String ARG_QUES = "Questions";
    private static final String ARG_ANSW = "Answers";
    private static final String ARG_TIMER = "Timer";

    private boolean mIsFromMix;
    private int mQuestionCounter;
    private int mCorrectAnswersCounter;
    private long mTimeLeft, timeLeftTemp;

    private TextView timer, subTimer;
    private FrameLayout ballFrame;
    private ImageView ballImageView;

    private ObjectAnimator animatorX, animatorY;
    private boolean timerStarted, isRunning;
    private CountDownTimer ballTouchTimer;
    private int tmp = 0;

    private long timerDuration = 5000;
    private int databaseIndex = 14;
    private CountDownTimer countDownTimer, subTimerCount;

    public VisualPlayBallFragment() {
        // Required empty public constructor
    }

    public static VisualPlayBallFragment newInstance(boolean isFromMix, int questionCounter, int correctAnswersCounter, long timeLeft) {
        VisualPlayBallFragment fragment = new VisualPlayBallFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_MIX, isFromMix);
        args.putInt(ARG_QUES, questionCounter);
        args.putInt(ARG_ANSW, correctAnswersCounter);
        args.putLong(ARG_TIMER, timeLeft);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mIsFromMix = getArguments().getBoolean(ARG_MIX);
            mQuestionCounter = getArguments().getInt(ARG_QUES);
            mCorrectAnswersCounter = getArguments().getInt(ARG_ANSW);
            mTimeLeft = getArguments().getLong(ARG_TIMER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_visual_play_ball, container, false);

        timer = view.findViewById(R.id.timer);
        timer.setText(String.valueOf((int) (mTimeLeft / 1000)));

        subTimer = view.findViewById(R.id.sub_timer);
        ballFrame = view.findViewById(R.id.ball_frame);
        ballImageView = ballFrame.findViewById(R.id.ball_image_view);
        subTimer.setText(String.valueOf((int) (timerDuration / 1000)));
        timerStarted = false;
        isRunning = false;

        if (mIsFromMix) {
            startTimer();
            subTimer.setVisibility(View.VISIBLE);
        } else {
            TextView startupView = view.findViewById(R.id.startup);
            startupView.setVisibility(View.VISIBLE);

            timerDuration = 60000;

            LinearLayout layout = (LinearLayout) view;
            View[] views = new View[layout.getChildCount() - 1];
            for (int i = 1; i < layout.getChildCount(); i++) {
                views[i - 1] = layout.getChildAt(i);
            }

            for (View child : views)
                child.setVisibility(View.GONE);

            Animation fadeIn1 = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_in);
            Animation fadeIn2 = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_in);
            Animation fadeOut1 = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_out);
            Animation fadeOut2 = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_out);

            fadeIn1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startupView.setVisibility(View.VISIBLE);
                    startupView.startAnimation(fadeOut1);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            fadeOut1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startupView.setVisibility(View.INVISIBLE);
                    startupView.setText(R.string.go);
                    startupView.startAnimation(fadeIn2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            fadeIn2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startupView.setVisibility(View.VISIBLE);
                    startupView.startAnimation(fadeOut2);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            fadeOut2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    startupView.setVisibility(View.GONE);
                    layout.setBackgroundColor(getResources().getColor(R.color.white, requireActivity().getTheme()));

                    for (View child : views)
                        child.setVisibility(View.VISIBLE);
                    subTimer.setVisibility(View.GONE);

                    showQuestion();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            view.setBackgroundColor(getResources().getColor(R.color.black, requireActivity().getTheme()));
            view.getBackground().setAlpha(32);

            startupView.setText(R.string.ready);
            startupView.setTextSize(60f);
            startupView.startAnimation(fadeIn1);
        }

        if (mIsFromMix)
            showQuestion();

        return view;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showQuestion() {
        subTimer.setText(String.valueOf((int) (timerDuration / 1000)));
        ballImageView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (!timerStarted) {
                        timerStarted = true;
                        startSubTimer();
                        startAnimation();
                        if (!mIsFromMix)
                            startTimer();
                    }
                    startBallTime();
                    break;
                case MotionEvent.ACTION_UP:
                    stopBallTime();
                    break;
            }
            return true;
        });
    }

    private void startAnimation() {
        // Set the initial position of the ball
        ballImageView.setTranslationX(0);
        ballImageView.setTranslationY(0);

        // Calculate the maximum translation values based on the screen size
        int maxTranslationX = ballFrame.getWidth() - ballImageView.getWidth();
        int maxTranslationY = ballFrame.getHeight() - ballImageView.getHeight();

        // Create the animator for horizontal movement
        animatorX = ObjectAnimator.ofFloat(ballImageView, "translationX", maxTranslationX);
        animatorX.setDuration(5000);
        animatorX.setInterpolator(new LinearInterpolator());
        animatorX.setRepeatCount(ValueAnimator.INFINITE);
        animatorX.setRepeatMode(ValueAnimator.REVERSE);

        // Create the animator for vertical movement
        animatorY = ObjectAnimator.ofFloat(ballImageView, "translationY", maxTranslationY);
        animatorY.setDuration(3000);
        animatorY.setInterpolator(new LinearInterpolator());
        animatorY.setRepeatCount(ValueAnimator.INFINITE);
        animatorY.setRepeatMode(ValueAnimator.REVERSE);

        // Start the animation
        animatorX.start();
        animatorY.start();
    }

    private void startSubTimer() {
        subTimerCount = new CountDownTimer(timerDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mQuestionCounter++;
                long secondsRemaining = millisUntilFinished / 1000;
                subTimer.setText(String.valueOf(secondsRemaining));
            }

            @Override
            public void onFinish() {
                animatorX.cancel();
                animatorY.cancel();
                timerStarted = false;
                if (mIsFromMix)
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.visual_play_fragment_container, VisualPlayMatchFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                            .commit();
            }
        };
        subTimerCount.start();
    }

    private void startBallTime() {
        if (!isRunning) {
            ballTouchTimer = new CountDownTimer(Long.MAX_VALUE, 100) {
                @Override
                public void onTick(long l) {
                    tmp++;
                    if (tmp == 10) {
                        mCorrectAnswersCounter += 1;
                        tmp = 0;
                    }
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

    private void startTimer() {
        final int[] secondInterval = {0};
        countDownTimer = new CountDownTimer(mTimeLeft, 100) {
            public void onTick(long millisUntilFinished) {
                timeLeftTemp = millisUntilFinished;
                secondInterval[0]++;
                if (secondInterval[0] == 10) {
                    int secondsLeft = (int) millisUntilFinished / 1000;
                    timer.setText(String.valueOf(secondsLeft));
                    secondInterval[0] = 0;
                }
            }

            public void onFinish() {
                if (mIsFromMix)
                    databaseIndex = 15;
                Handler mainHandler = new Handler(Looper.getMainLooper());
                Context appContext = requireActivity().getApplicationContext();
                UserDatabase database = UserDatabase.getInstance(requireActivity().getApplicationContext());
                UserDao userDao = database.userDao();
                int score = mCorrectAnswersCounter * 100 - (mQuestionCounter - mCorrectAnswersCounter) * 10;
                AppExecutor.getInstance().execute(() -> {
                    User user = userDao.findLoggedInUser();
                    ArrayList<String> scores = user.getHighScores();
                    mainHandler.post(() -> Toast.makeText(appContext, "Correct answers: " + mCorrectAnswersCounter + " out of " + mQuestionCounter, Toast.LENGTH_SHORT).show());
                    mainHandler.post(() -> Toast.makeText(appContext, "Score: " + score, Toast.LENGTH_SHORT).show());
                    if (Integer.parseInt(scores.get(databaseIndex)) < score) {
                        scores.set(databaseIndex, String.valueOf(score));
                        user.setHighScores(scores);
                        userDao.updateUser(user);
                        mainHandler.post(() -> Toast.makeText(appContext, "New high score!", Toast.LENGTH_SHORT).show());
                    }
                });

                // Replace the fragment with TimePlayMenuFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.visual_play_fragment_container, VisualPlayMenuFragment.newInstance())
                        .commit();
            }
        };

        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
        subTimerCount.cancel();
    }
}