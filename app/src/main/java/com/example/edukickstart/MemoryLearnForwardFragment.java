package com.example.edukickstart;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MemoryLearnForwardFragment extends Fragment {

    private TextView timer, display, questionView;
    private EditText answerView;
    private Button startButton, nextButton;
    private int questionCounter = 0, correctAnswers = 0, difficulty;
    private String answer;
    private boolean questionAnswered;

    private UserDao userDao;
    private static final int DATABASE_INDEX = 5;

    public MemoryLearnForwardFragment() {
        // Required empty public constructor
    }

    public static MemoryLearnForwardFragment newInstance() {
        return new MemoryLearnForwardFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_memory_learn_forward, container, false);

        timer = view.findViewById(R.id.timer);
        display = view.findViewById(R.id.display);
        questionView = view.findViewById(R.id.question_text_view);
        answerView = view.findViewById(R.id.answer_edit_text);

        startButton = view.findViewById(R.id.start_button);
        nextButton = view.findViewById(R.id.next_button);

        UserDatabase database = UserDatabase.getInstance(requireActivity());
        userDao = database.userDao();
        ArrayList<String> progress = userDao.findLoggedInUser().getProgress();
        int accuracy = Integer.parseInt(progress.get(DATABASE_INDEX));

        if (accuracy < 20)
            difficulty = 0;
        else if (accuracy < 40)
            difficulty = 1;
        else if (accuracy < 60)
            difficulty = 2;
        else if (accuracy < 80)
            difficulty = 3;
        else
            difficulty = 4;

        startButton.setOnClickListener(v1 -> startTimer());
        nextButton.setOnClickListener(v2 -> nextFunc());
        answerView.setOnEditorActionListener((textView, i, keyEvent) -> {
            nextFunc();
            return true;
        });

        showQuestion();

        return view;
    }

    private void showQuestion() {
        timer.setVisibility(View.VISIBLE);
        display.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);

        answerView.setText("");
        answerView.setHint("Enter the digits");
        answerView.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);

        timer.setText("3");
        questionView.setText(R.string.digits_memory);

        Random random = new Random();
        int numberOfDigits = random.nextInt(3) + 3 + difficulty;
        StringBuilder answerBuilder = new StringBuilder();

        for (int i = 0; i < numberOfDigits; i++) {
            answerBuilder.append(random.nextInt(10));
        }

        answer = answerBuilder.toString();
        display.setText(answer);
        display.setTextSize(300f/numberOfDigits);
    }

    private void checkAnswer() {
        String givenAnswer = answerView.getText().toString();
        questionAnswered = true;
        if (givenAnswer.isEmpty()) {
            questionAnswered = false;
            Toast.makeText(requireActivity(), "Please enter your answer", Toast.LENGTH_SHORT).show();
        } else if (givenAnswer.equals(answer))
            correctAnswers++;
        else
            Toast.makeText(requireActivity(), "Correct answer was: " + answer, Toast.LENGTH_SHORT).show();
    }

    private void startTimer() {
        startButton.setVisibility(View.GONE);

        CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) millisUntilFinished / 1000;
                timer.setText(String.valueOf(secondsLeft));
            }

            public void onFinish() {
                timer.setVisibility(View.GONE);
                display.setVisibility(View.GONE);
                answerView.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                questionView.setText(R.string.forward_digits_question);
            }
        };

        countDownTimer.start();
    }

    private void nextFunc() {
        checkAnswer();
        if (questionAnswered) {
            View focusView = requireActivity().getCurrentFocus();
            if (focusView != null) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
            if (questionCounter < 4) {
                showQuestion();
                questionCounter++;
            } else {
                AppExecutor.getInstance().execute(() -> {
                    User user = userDao.findLoggedInUser();
                    ArrayList<String> progress = user.getProgress();
                    int newWeightedAccuracy;
                    if (progress.get(DATABASE_INDEX).equals("0"))
                        newWeightedAccuracy = correctAnswers * 20;
                    else
                        newWeightedAccuracy = (Integer.parseInt(progress.get(DATABASE_INDEX)) + correctAnswers * 20) / 2;
                    progress.set(DATABASE_INDEX, String.valueOf(newWeightedAccuracy));
                    user.setProgress(progress);
                    userDao.updateUser(user);
                });
                // Display the final score or navigate to the menu fragment
                Toast.makeText(getActivity(), "Correct answers: " + correctAnswers + " out of 5", Toast.LENGTH_SHORT).show();
                // Replace the fragment with TimeLearnMenuFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.memory_learn_fragment_container, MemoryLearnMenuFragment.newInstance())
                        .commit();
            }
        }
    }
}