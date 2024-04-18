package com.example.edukickstart;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LangPlayDirectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LangPlayDirectFragment extends Fragment {

    private static final String ARG_MIX = "Mix";
    private static final String ARG_QUES = "Questions";
    private static final String ARG_ANSW = "Answers";
    private static final String ARG_TIMER = "Timer";

    private boolean mIsFromMix;
    private int mQuestionCounter;
    private int mCorrectAnswersCounter;
    private long mTimeLeft, timeLeftTemp;

    private TextView timer;
    private ImageView imageView;
    private final Button[] optionButtons = new Button[4];
    private int selectedButton = 0;
    private String answer;
    private boolean optionSelected, compassQuestion;

    private static final String[][] DIRECTIONS = new String[][] {
            {"Up", "Right", "Left", "Down"},
            {"North", "East", "West", "South"}
    };
    private static final int[] ARROWS = new int[] {
            R.drawable.ic_up,
            R.drawable.ic_right,
            R.drawable.ic_left,
            R.drawable.ic_down
    };

    private int databaseIndex = 7;
    private CountDownTimer countDownTimer;

    public LangPlayDirectFragment() {
        // Required empty public constructor
    }

    public static LangPlayDirectFragment newInstance(boolean isFromMix, int questionCounter, int correctAnswersCounter, long timeLeft) {
        LangPlayDirectFragment fragment = new LangPlayDirectFragment();
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
        View view = inflater.inflate(R.layout.fragment_lang_play_direct, container, false);

        timer = view.findViewById(R.id.timer);
        timer.setText(String.valueOf((int) (mTimeLeft / 1000)));

        imageView = view.findViewById(R.id.image_view);
        optionButtons[0] = view.findViewById(R.id.option_button_one);
        optionButtons[1] = view.findViewById(R.id.option_button_two);
        optionButtons[2] = view.findViewById(R.id.option_button_three);
        optionButtons[3] = view.findViewById(R.id.option_button_four);
        Button nextButton = view.findViewById(R.id.next_button);

        optionButtons[0].setOnClickListener(v -> {
            selectedButton = 1;
            optionButtons[0].setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
            optionButtons[1].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[2].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[3].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
        });

        optionButtons[1].setOnClickListener(v -> {
            selectedButton = 2;
            optionButtons[0].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[1].setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
            optionButtons[2].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[3].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
        });

        optionButtons[2].setOnClickListener(v -> {
            selectedButton = 3;
            optionButtons[0].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[1].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[2].setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
            optionButtons[3].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
        });

        optionButtons[3].setOnClickListener(v -> {
            selectedButton = 4;
            optionButtons[0].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[1].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[2].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[3].setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
        });

        if (mIsFromMix) {
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

                    startTimer();
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

        nextButton.setOnClickListener(v -> {
            checkAnswer();
            if (optionSelected) {
                if (mIsFromMix) {
                    mQuestionCounter++;
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.lang_play_fragment_container, LangPlayWordsFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                            .commit();
                } else {
                    showQuestion();
                    mQuestionCounter++;
                }
            }
        });

        if (mIsFromMix)
            showQuestion();

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
                    databaseIndex = 8;
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
                        .replace(R.id.lang_play_fragment_container, LangPlayMenuFragment.newInstance())
                        .commit();
            }
        };

        countDownTimer.start();
    }

    private void showQuestion() {
        selectedButton = 0;
        for (int i = 0; i < 4; i++)
            optionButtons[i].setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));

        String[] directions;
        int variant = 0;
        int direction = new Random().nextInt(4);

        if (compassQuestion)
            variant = 1;

        directions = DIRECTIONS[variant];
        answer = directions[direction];

        imageView.setImageResource(ARROWS[direction]);

        String[] shuffledDirections = Arrays.copyOf(directions, directions.length); // Create a copy of the array
        List<String> list = Arrays.asList(shuffledDirections);
        Collections.shuffle(list);

        for (int i = 0; i < shuffledDirections.length; i++)
            optionButtons[i].setText(shuffledDirections[i]);

        compassQuestion = !compassQuestion;
    }

    private void checkAnswer() {
        optionSelected = true;
        if (selectedButton == 0) {
            optionSelected = false;
            Toast.makeText(requireActivity(), "Please select one of the options", Toast.LENGTH_SHORT).show();
        } else if (optionButtons[selectedButton - 1].getText().toString().equals(answer))
            mCorrectAnswersCounter++;
        else
            Toast.makeText(requireActivity(), "Correct answer was: " + answer, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}