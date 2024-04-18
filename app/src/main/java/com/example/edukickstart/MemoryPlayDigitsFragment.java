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
 * Use the {@link MemoryPlayDigitsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemoryPlayDigitsFragment extends Fragment {

    private static final String ARG_MIX = "Mix";
    private static final String ARG_QUES = "Questions";
    private static final String ARG_ANSW = "Answers";
    private static final String ARG_TIMER = "Timer";

    private boolean mIsFromMix;
    private int mQuestionCounter;
    private int mCorrectAnswersCounter;
    private long mTimeLeft , timeLeftTemp;

    private TextView timer, subTimer, display, questionView;
    private EditText answerView;
    private Button startButton, nextButton;
    private String answer;
    private boolean questionAnswered, isBackwards;

    private CountDownTimer countDownTimer;
    private int databaseIndex = 3;

    public MemoryPlayDigitsFragment() {
        // Required empty public constructor
    }

    public static MemoryPlayDigitsFragment newInstance(boolean isFromMix, int questionCounter, int correctAnswersCounter, long timeLeft) {
        MemoryPlayDigitsFragment fragment = new MemoryPlayDigitsFragment();
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
        View view = inflater.inflate(R.layout.fragment_memory_play_digits, container, false);

        timer = view.findViewById(R.id.timer);
        timer.setText(String.valueOf((int) (mTimeLeft / 1000)));

        subTimer = view.findViewById(R.id.sub_timer);
        display = view.findViewById(R.id.display);
        questionView = view.findViewById(R.id.question_text_view);
        answerView = view.findViewById(R.id.answer_edit_text);

        startButton = view.findViewById(R.id.start_button);
        nextButton = view.findViewById(R.id.next_button);

        isBackwards = new Random().nextBoolean();

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

                    isBackwards = new Random().nextBoolean();
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

        startButton.setOnClickListener(v -> startSubTimer());
        nextButton.setOnClickListener(v -> nextFunc());
        answerView.setOnEditorActionListener((textView, i, keyEvent) -> {
            nextFunc();
            return true;
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
                    databaseIndex = 5;
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
                        .replace(R.id.memory_play_fragment_container, MemoryPlayMenuFragment.newInstance())
                        .commit();
            }
        };

        countDownTimer.start();
    }

    private void showQuestion() {
        int difficulty = mQuestionCounter / 3;

        subTimer.setVisibility(View.VISIBLE);
        display.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);

        answerView.setText("");
        answerView.setHint("Enter the digits");

        answerView.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);

        subTimer.setText("3");
        questionView.setText(R.string.digits_memory);

        Random random = new Random();
        int numberOfDigits = random.nextInt(3) + 3 + difficulty;
        StringBuilder answerBuilder = new StringBuilder();

        for (int i = 0; i < numberOfDigits; i++)
            answerBuilder.append(random.nextInt(10));

        answer = answerBuilder.toString();
        display.setText(answer);
        display.setTextSize(300f/numberOfDigits);
    }

    private void checkAnswer() {
        if (isBackwards) {
            StringBuilder backwardBuilder = new StringBuilder();
            for (int i = 0; i < answer.length(); i++)
                backwardBuilder.append(answer.substring(answer.length() - 1 - i, answer.length() - i));
            answer = backwardBuilder.toString();
        }

        String givenAnswer = answerView.getText().toString();
        questionAnswered = true;
        if (givenAnswer.isEmpty()) {
            questionAnswered = false;
            Toast.makeText(requireActivity(), "Please enter your answer", Toast.LENGTH_SHORT).show();
        } else if (givenAnswer.equals(answer))
            mCorrectAnswersCounter++;
        else
            Toast.makeText(requireActivity(), "Correct answer was: " + answer, Toast.LENGTH_SHORT).show();
    }

    private void startSubTimer() {
        startButton.setVisibility(View.GONE);

        CountDownTimer subTimerCountDown = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) millisUntilFinished / 1000;
                subTimer.setText(String.valueOf(secondsLeft));
            }

            public void onFinish() {
                subTimer.setVisibility(View.GONE);
                display.setVisibility(View.GONE);
                answerView.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                if (isBackwards)
                    questionView.setText(R.string.backward_digits_question);
                else
                    questionView.setText(R.string.forward_digits_question);
            }
        };

        subTimerCountDown.start();
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
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.memory_play_fragment_container, MemoryPlayImageFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                        .commit();
            } else {
                isBackwards = new Random().nextBoolean();
                showQuestion();
                mQuestionCounter++;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}