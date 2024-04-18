package com.example.edukickstart;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MathPlaySubFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MathPlaySubFragment extends Fragment {

    private static final String ARG_MIX = "Mix";
    private static final String ARG_QUES = "Questions";
    private static final String ARG_ANSW = "Answers";
    private static final String ARG_TIMER = "Timer";

    private boolean mIsFromMix;
    private int mQuestionCounter;
    private int mCorrectAnswersCounter;
    private long mTimeLeft, timeLeftTemp;

    private TextView timer;
    private TextView display;
    private EditText answerView;
    private int answer;
    private boolean questionAnswered;

    private int databaseIndex = 10;
    private CountDownTimer countDownTimer;

    public MathPlaySubFragment() {
        // Required empty public constructor
    }

    public static MathPlaySubFragment newInstance(boolean isFromMix, int questionCounter, int correctAnswersCounter, long timeLeft) {
        MathPlaySubFragment fragment = new MathPlaySubFragment();
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
        View view = inflater.inflate(R.layout.fragment_math_play_sub, container, false);

        timer = view.findViewById(R.id.timer);
        timer.setText(String.valueOf((int) (mTimeLeft / 1000)));

        display = view.findViewById(R.id.display);
        answerView = view.findViewById(R.id.answer_edit_text);
        Button nextButton = view.findViewById(R.id.next_button);

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

        nextButton.setOnClickListener(v -> nextFunc());
        answerView.setOnEditorActionListener((textView, i, keyEvent) -> {
            nextFunc();
            return true;
        });

        answerView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!answerView.getText().toString().matches("^\\d+$") && !answerView.getText().toString().isEmpty())
                    answerView.setError("Please enter a valid number");
            }
        });

        if (mIsFromMix)
            showQuestion();

        return view;
    }

    private void showQuestion() {
        answerView.setText("");
        answerView.setHint("Enter your answer here");
        Random random = new Random();
        int a, b;

        int difficulty = mQuestionCounter / 5;

        switch (difficulty) {
            case 0:
                a = random.nextInt(10);
                b = random.nextInt(10);
                break;
            case 1:
                a = random.nextInt(100);
                b = random.nextInt(10);
                break;
            case 2:
                a = random.nextInt(100);
                b = random.nextInt(100);
                break;
            default:
                a = random.nextInt((int) Math.pow(10, difficulty + 1));
                b = random.nextInt((int) Math.pow(10, difficulty));
                break;
        }

        if (a < b) {
            int temp = a;
            a = b;
            b = temp;
        }

        String displayString = a + " - " + b;
        display.setText(displayString);
        answer = a - b;
    }

    private void checkAnswer() {
        questionAnswered = false;
        if (answerView.getText().toString().isEmpty())
            Toast.makeText(requireActivity(), "Please enter an answer", Toast.LENGTH_SHORT).show();
        else if (!answerView.getText().toString().matches("^\\d+$"))
            Toast.makeText(requireActivity(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
        else if (Integer.parseInt(answerView.getText().toString()) == answer) {
            questionAnswered = true;
            mCorrectAnswersCounter++;
        } else {
            questionAnswered = true;
            Toast.makeText(requireActivity(), "Correct answer was: " + answer, Toast.LENGTH_SHORT).show();
        }
    }

    private void nextFunc() {
        checkAnswer();
        if (questionAnswered) {
            View focusView = requireActivity().getCurrentFocus();
            if (focusView != null) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
            if (mIsFromMix) {
                mQuestionCounter++;
                if (new Random().nextBoolean())
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.math_play_fragment_container, MathPlayAddFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                            .commit();
                else
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.math_play_fragment_container, MathPlayMultiplyFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                            .commit();
            } else {
                showQuestion();
                mQuestionCounter++;
            }
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
                    databaseIndex = 12;
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
                        .replace(R.id.math_play_fragment_container, MathPlayMenuFragment.newInstance())
                        .commit();
            }
        };

        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}