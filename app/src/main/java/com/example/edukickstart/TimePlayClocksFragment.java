package com.example.edukickstart;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimePlayClocksFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimePlayClocksFragment extends Fragment {

    private static final String ARG_MIX = "Mix";
    private static final String ARG_QUES = "Questions";
    private static final String ARG_ANSW = "Answers";
    private static final String ARG_TIMER = "Timer";

    private boolean mIsFromMix;
    private int mQuestionCounter;
    private int mCorrectAnswersCounter;
    private long mTimeLeft;

    private AnalogClockView analogClock;
    private TextView digitalClock, timer;
    private Button optionButton1;
    private Button optionButton2;
    private Button optionButton3;
    private int selectedButton = 0;
    private List<String> digitalTimes;
    private String correctDigitalTime;
    private boolean optionSelected, analogClockQuestion;
    private long timeLeftTemp;

    private CountDownTimer countDownTimer;
    private int databaseIndex = 0;

    public TimePlayClocksFragment() {
        // Required empty public constructor
    }

    public static TimePlayClocksFragment newInstance(boolean isFromMix, int questionCounter, int correctAnswersCounter, long timeLeft) {
        TimePlayClocksFragment fragment = new TimePlayClocksFragment();
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
        View view = inflater.inflate(R.layout.fragment_time_play_clocks, container, false);

        timer = view.findViewById(R.id.timer);
        timer.setText(String.valueOf((int) (mTimeLeft / 1000)));
        analogClock = view.findViewById(R.id.analog_clock);
        digitalClock = view.findViewById(R.id.digital_clock);
        optionButton1 = view.findViewById(R.id.option_button_one);
        optionButton2 = view.findViewById(R.id.option_button_two);
        optionButton3 = view.findViewById(R.id.option_button_three);
        Button nextButton = view.findViewById(R.id.next_button);

        if (mIsFromMix) {
            analogClockQuestion = new Random().nextBoolean();
            startTimer();
        } else {
            TextView startupView = view.findViewById(R.id.startup);
            startupView.setVisibility(View.VISIBLE);

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

                    analogClockQuestion = true;
                    startTimer();
                    showNextQuestion();
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

        digitalTimes = new ArrayList<String>() {
            {
                add("0:00");
                add("0:00");
                add("0:00");
            }
        };

        optionButton1.setOnClickListener(v -> {
            selectedButton = 1;
            optionButton1.setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
            optionButton2.setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButton3.setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
        });

        optionButton2.setOnClickListener(v -> {
            selectedButton = 2;
            optionButton1.setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButton2.setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
            optionButton3.setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
        });

        optionButton3.setOnClickListener(v -> {
            selectedButton = 3;
            optionButton1.setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButton2.setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButton3.setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
        });

        nextButton.setOnClickListener(v -> {
            checkAnswer();
            if (optionSelected) {
                if (mIsFromMix) {
                    mQuestionCounter++;
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.time_play_fragment_container, TimePlayCalendarFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                            .commit();
                }
                else {
                    analogClockQuestion = !analogClockQuestion;
                    showNextQuestion();
                    mQuestionCounter++;
                }
            }
        });

        if (mIsFromMix)
            showNextQuestion();


        return view;
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
                    databaseIndex = 2;
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
                        .replace(R.id.time_play_fragment_container, TimePlayMenuFragment.newInstance())
                        .commit();
            }
        };

        countDownTimer.start();
    }

    private void showNextQuestion() {
        if (analogClockQuestion)
            generateAnalogClockQuestion();
        else
            generateDigitalClockQuestion();
    }

    private void generateAnalogClockQuestion() {
        selectedButton = 0;
        optionButton1.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
        optionButton2.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
        optionButton3.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));

        analogClock.setVisibility(View.VISIBLE);
        digitalClock.setVisibility(View.GONE);

        // Generate a random time for the analog clock
        int hours = new Random().nextInt(12);
        int minutes = new Random().nextInt(12) * 5;
        String fillerZero = "";

        // Set the generated time on the analog clock view
        analogClock.setTime(hours, minutes);
        int correctHours = hours;
        int correctMinutes = minutes;
        if (hours == 0)
            hours = 12;
        if (minutes < 10)
            fillerZero = "0";
        correctDigitalTime = hours + ":" + fillerZero + minutes;
        digitalTimes.set(0, correctDigitalTime);

        // Generate two more random options
        for (int i = 1; i <= 2; i++) {
            hours = new Random().nextInt(12);
            minutes = new Random().nextInt(12) * 5;
            while (hours == correctHours)
                hours = new Random().nextInt(12);
            while (minutes == correctMinutes)
                minutes = new Random().nextInt(12) * 5;
            if (hours == 0)
                hours = 12;
            if (minutes < 10)
                fillerZero = "0";
            else
                fillerZero = "";
            digitalTimes.set(i, hours + ":" + fillerZero + minutes);
        }

        // Generate three different digital times as multiple-choice options
        List<String> options = digitalTimes;

        Collections.shuffle(options); // Shuffle the options

        // Set the digital times on the buttons in a random order
        optionButton1.setText(options.get(0));
        optionButton2.setText(options.get(1));
        optionButton3.setText(options.get(2));
    }

    private void generateDigitalClockQuestion() {
        selectedButton = 0;
        optionButton1.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
        optionButton2.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
        optionButton3.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));

        analogClock.setVisibility(View.GONE);
        digitalClock.setVisibility(View.VISIBLE);

        // Generate a random time for the digital clock
        int hours = new Random().nextInt(24);
        int minutes = new Random().nextInt(12) * 5;
        String fillerZero1 = "";
        String fillerZero2 = "";

        int correctHours = hours;
        int correctMinutes = minutes;
        if (hours < 10)
            fillerZero1 = "0";
        if (minutes < 10)
            fillerZero2 = "0";
        correctDigitalTime = timeToString(hours, minutes);
        String digitalClockDisplay = fillerZero1 + hours + ":" + fillerZero2 + minutes;
        digitalClock.setText(digitalClockDisplay);
        digitalTimes.set(0, correctDigitalTime);

        // Generate two more random options
        for (int i = 1; i <= 2; i++) {
            hours = new Random().nextInt(12);
            minutes = new Random().nextInt(12) * 5;
            while (hours == correctHours)
                hours = new Random().nextInt(12);
            while (minutes == correctMinutes)
                minutes = new Random().nextInt(12) * 5;
            digitalTimes.set(i, timeToString(hours, minutes));
        }

        // Generate three different digital times as multiple-choice options
        List<String> options = digitalTimes;

        Collections.shuffle(options); // Shuffle the options

        // Set the digital times on the buttons in a random order
        optionButton1.setText(options.get(0));
        optionButton2.setText(options.get(1));
        optionButton3.setText(options.get(2));
    }

    private void checkAnswer() {
        String selectedOption = "";
        optionSelected = false;

        switch (selectedButton) {
            case 0:
                Toast.makeText(getActivity(), "Please select one of the options", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                selectedOption = (String) optionButton1.getText();
                optionSelected = true;
                break;
            case 2:
                selectedOption = (String) optionButton2.getText();
                optionSelected = true;
                break;
            case 3:
                selectedOption = (String) optionButton3.getText();
                optionSelected = true;
                break;
            default:
                Toast.makeText(getActivity(), "Unexpected error", Toast.LENGTH_SHORT).show();
        }

        if (optionSelected) {
            if (selectedOption.equals(correctDigitalTime)) {
                mCorrectAnswersCounter++;
            } else {
                Toast.makeText(getActivity(), "Correct answer: " + correctDigitalTime, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }

    /**
     * Method to translate digital time into words
     *
     * @param hours   the hours for the time (0-11)
     * @param minutes the minutes for the time (0-55, in intervals of 5)
     */
    private String timeToString(int hours, int minutes) {
        String hoursTime;
        String minutesTime;

        switch (hours) {
            case 13:
            case 1:
                hoursTime = "One";
                break;
            case 14:
            case 2:
                hoursTime = "Two";
                break;
            case 15:
            case 3:
                hoursTime = "Three";
                break;
            case 16:
            case 4:
                hoursTime = "Four";
                break;
            case 17:
            case 5:
                hoursTime = "Five";
                break;
            case 18:
            case 6:
                hoursTime = "Six";
                break;
            case 19:
            case 7:
                hoursTime = "Seven";
                break;
            case 20:
            case 8:
                hoursTime = "Eight";
                break;
            case 21:
            case 9:
                hoursTime = "Nine";
                break;
            case 22:
            case 10:
                hoursTime = "Ten";
                break;
            case 23:
            case 11:
                hoursTime = "Eleven";
                break;
            case 0:
            case 12:
                hoursTime = "Twelve";
                break;

            default:
                hoursTime = "";
                Toast.makeText(requireActivity().getApplicationContext(), "Unexpected Error", Toast.LENGTH_SHORT).show();
                Log.d("ERROR", "Unexpected value for hours: " + hours);
                break;
        }

        switch (minutes) {
            case 0:
                minutesTime = " o'clock";
                break;
            case 5:
                minutesTime = " o' five";
                break;
            case 10:
                minutesTime = " ten";
                break;
            case 15:
                minutesTime = " fifteen";
                break;
            case 20:
                minutesTime = " twenty";
                break;
            case 25:
                minutesTime = " twenty-five";
                break;
            case 30:
                minutesTime = " thirty";
                break;
            case 35:
                minutesTime = " thirty-five";
                break;
            case 40:
                minutesTime = " forty";
                break;
            case 45:
                minutesTime = " forty-five";
                break;
            case 50:
                minutesTime = " fifty";
                break;
            case 55:
                minutesTime = " fifty-five";
                break;
            default:
                minutesTime = "";
                Toast.makeText(requireActivity().getApplicationContext(), "Unexpected Error", Toast.LENGTH_SHORT).show();
                Log.d("ERROR", "Unexpected value for minutes: " + minutes);
                break;
        }

        return hoursTime + minutesTime;
    }
}