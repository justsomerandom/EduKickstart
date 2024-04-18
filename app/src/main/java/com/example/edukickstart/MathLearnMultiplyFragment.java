package com.example.edukickstart;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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

public class MathLearnMultiplyFragment extends Fragment {

    private TextView display;
    private EditText answerView;
    private int questionCounter = 0, correctAnswers = 0, answer, difficulty;
    private boolean questionAnswered;

    private static final int DATABASE_INDEX = 13;
    private UserDao userDao;

    public MathLearnMultiplyFragment() {
        // Required empty public constructor
    }

    public static MathLearnMultiplyFragment newInstance() {
        return new MathLearnMultiplyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_math_learn_multiply, container, false);

        display = view.findViewById(R.id.display);
        answerView = view.findViewById(R.id.answer_edit_text);
        Button nextButton = view.findViewById(R.id.next_button);

        UserDatabase database = UserDatabase.getInstance(requireActivity());
        userDao = database.userDao();
        int acc = Integer.parseInt(userDao.findLoggedInUser().getProgress().get(DATABASE_INDEX));
        if (acc < 20)
            difficulty = 0;
        else if (acc < 50)
            difficulty = 1;
        else
            difficulty = 2;

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

        nextQuestion();

        return view;
    }

    private void nextQuestion() {
        answerView.setText("");
        answerView.setHint("Enter your answer here");
        Random random = new Random();
        int a, b;

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

        String displayString = a + " x " + b;
        display.setText(displayString);
        answer = a * b;
    }

    private void checkAnswer() {
        questionAnswered = false;
        if (answerView.getText().toString().isEmpty())
            Toast.makeText(requireActivity(), "Please enter an answer", Toast.LENGTH_SHORT).show();
        else if (!answerView.getText().toString().matches("^\\d+$"))
            Toast.makeText(requireActivity(), "Please enter a valid number", Toast.LENGTH_SHORT).show();
        else if (Integer.parseInt(answerView.getText().toString()) == answer) {
            questionAnswered = true;
            correctAnswers++;
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
            if (questionCounter < 4) {
                nextQuestion();
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
                        .replace(R.id.math_learn_fragment_container, MathLearnMenuFragment.newInstance())
                        .commit();
            }
        }
    }
}