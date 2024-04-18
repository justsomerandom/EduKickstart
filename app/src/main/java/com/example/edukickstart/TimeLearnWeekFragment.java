package com.example.edukickstart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TimeLearnWeekFragment extends Fragment {

    private TextView mainQuestion;
    private TextView questionTextView;
    private Button optionButton1;
    private Button optionButton2;
    private Button optionButton3;
    private EditText answerEditText;
    private int selectedButton = 0;
    private int correctButton = 0;
    private boolean optionSelected;
    private int variant;
    private String answer;
    private int questionCounter = 0;
    private int correctAnswersCounter = 0;

    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private static final int DATABASE_INDEX = 2;

    public TimeLearnWeekFragment() {
        // Required empty public constructor
    }

    public static TimeLearnWeekFragment newInstance() {
        return new TimeLearnWeekFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_learn_week, container, false);

        // Initialize views
        mainQuestion = view.findViewById(R.id.main_question_view);
        questionTextView = view.findViewById(R.id.question_text);
        optionButton1 = view.findViewById(R.id.option_button_one);
        optionButton2 = view.findViewById(R.id.option_button_two);
        optionButton3 = view.findViewById(R.id.option_button_three);
        answerEditText = view.findViewById(R.id.answer_edit_text);
        Button nextButton = view.findViewById(R.id.next_button);

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

        // Set click listener for Next Button and EditText enter
        nextButton.setOnClickListener(v -> nextFunc());
        answerEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            nextFunc();
            return true;
        });

        // Generate and display the initial question
        generateQuestion();

        return view;
    }

    private void generateQuestion() {
        selectedButton = 0;
        optionButton1.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
        optionButton2.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
        optionButton3.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));

        // Generate a random question variant
        Random random = new Random();
        variant = random.nextInt(3); // 0 for day number, 1 for day name, 2 for fill in the blank

        // Update the UI based on the question variant
        switch (variant) {
            case 0:
                generateQuestionDayNumber();
                break;
            case 1:
                generateQuestionDayName();
                break;
            case 2:
                generateQuestionFillInTheBlank();
                break;
        }
    }

    private void generateQuestionDayNumber() {
        // Set the question text
        questionTextView.setText(R.string.day_number_question);

        // Generate a random day
        Random random = new Random();
        int dayIndex = random.nextInt(daysOfWeek.length);
        mainQuestion.setText(daysOfWeek[dayIndex]);
        answer = Integer.toString(dayIndex + 1);

        if (answer.equals("Wednesday"))
            mainQuestion.setTextSize(64f);
        else
            mainQuestion.setTextSize(74f);

        // Set the options
        String[] options = new String[3];
        options[0] = Integer.toString(dayIndex + 1);
        options[1] = Integer.toString(random.nextInt(daysOfWeek.length) + 1);
        options[2] = Integer.toString(random.nextInt(daysOfWeek.length) + 1);
        while (options[1].equals(options[0]))
            options[1] = Integer.toString(random.nextInt(daysOfWeek.length) + 1);
        while (options[2].equals(options[0]) || options[2].equals(options[1]))
            options[2] = Integer.toString(random.nextInt(daysOfWeek.length) + 1);

        // Shuffle the options randomly
        shuffleArray(options);
        optionButton1.setText(options[0]);
        optionButton2.setText(options[1]);
        optionButton3.setText(options[2]);

        for (int i = 0; i < 3; i++) {
            if (options[i].equals(answer))
                correctButton = i + 1;
        }

        // Show the buttons and hide the EditText
        optionButton1.setVisibility(View.VISIBLE);
        optionButton2.setVisibility(View.VISIBLE);
        optionButton3.setVisibility(View.VISIBLE);
        answerEditText.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void generateQuestionDayName() {
        // Set the question text
        questionTextView.setText(R.string.day_name_question);

        // Generate a random day
        Random random = new Random();
        int dayIndex = random.nextInt(daysOfWeek.length);
        mainQuestion.setText(Integer.toString(dayIndex + 1));
        answer = daysOfWeek[dayIndex];

        // Set the options
        String[] options = new String[3];
        options[0] = daysOfWeek[dayIndex];
        options[1] = daysOfWeek[random.nextInt(daysOfWeek.length)];
        options[2] = daysOfWeek[random.nextInt(daysOfWeek.length)];
        while (options[1].equals(options[0]))
            options[1] = daysOfWeek[random.nextInt(daysOfWeek.length)];
        while (options[2].equals(options[0]) || options[2].equals(options[1]))
            options[2] = daysOfWeek[random.nextInt(daysOfWeek.length)];

        // Shuffle the options randomly
        shuffleArray(options);
        optionButton1.setText(options[0]);
        optionButton2.setText(options[1]);
        optionButton3.setText(options[2]);

        for (int i = 0; i < 3; i++) {
            if (options[i].equals(answer))
                correctButton = i + 1;
        }

        // Show the buttons and hide the EditText
        optionButton1.setVisibility(View.VISIBLE);
        optionButton2.setVisibility(View.VISIBLE);
        optionButton3.setVisibility(View.VISIBLE);
        answerEditText.setVisibility(View.GONE);
    }

    private void generateQuestionFillInTheBlank() {
        // Set the question text
        questionTextView.setText(R.string.day_complete_question);

        // Generate a random day
        Random random = new Random();
        int dayIndex = random.nextInt(daysOfWeek.length);

        // Set the answer
        answer = daysOfWeek[dayIndex];
        mainQuestion.setText(answer.substring(0, 2));

        // Clear the EditText and set the hint
        answerEditText.setText("");
        answerEditText.setHint("Enter the day of the week");

        // Show the EditText and hide the RadioGroup
        optionButton1.setVisibility(View.GONE);
        optionButton2.setVisibility(View.GONE);
        optionButton3.setVisibility(View.GONE);
        answerEditText.setVisibility(View.VISIBLE);
    }

    private void checkAnswer() {
        switch (variant) {
            case 0:
            case 1:
                optionSelected = true;
                if (selectedButton == 0) {
                    optionSelected = false;
                    Toast.makeText(getActivity(), "Please select one of the options", Toast.LENGTH_SHORT).show();
                } else if (selectedButton == correctButton)
                    correctAnswersCounter++;
                else
                    Toast.makeText(getActivity(), "Correct answer: " + answer, Toast.LENGTH_SHORT).show();
                break;
            case 2:
                optionSelected = true;
                if (answerEditText.getText().toString().matches("")) {
                    optionSelected = false;
                    Toast.makeText(getActivity(), "Please enter your answer", Toast.LENGTH_SHORT).show();
                } else if (answerEditText.getText().toString().equalsIgnoreCase(answer))
                    correctAnswersCounter++;
                else
                    Toast.makeText(getActivity(), "Correct answer: " + answer, Toast.LENGTH_SHORT).show();
                break;
            default:
                optionSelected = false;
                Toast.makeText(getActivity(), "Unexpected Error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void nextFunc() {
        checkAnswer();
        if (optionSelected) {
            View focusView = requireActivity().getCurrentFocus();
            if (focusView != null) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
            if (questionCounter < 9) {
                generateQuestion();
                questionCounter++;
            } else {
                UserDatabase database = UserDatabase.getInstance(requireActivity().getApplicationContext());
                UserDao userDao = database.userDao();
                AppExecutor.getInstance().execute(() -> {
                    User user = userDao.findLoggedInUser();
                    ArrayList<String> progress = user.getProgress();
                    int newWeightedAccuracy;
                    if (progress.get(DATABASE_INDEX).equals("0"))
                        newWeightedAccuracy = correctAnswersCounter * 10;
                    else
                        newWeightedAccuracy = (Integer.parseInt(progress.get(DATABASE_INDEX)) + correctAnswersCounter * 10) / 2;
                    progress.set(DATABASE_INDEX, String.valueOf(newWeightedAccuracy));
                    user.setProgress(progress);
                    userDao.updateUser(user);
                });
                // Display the final score or navigate to the menu fragment
                Toast.makeText(getActivity(), "Correct answers: " + correctAnswersCounter + " out of 10", Toast.LENGTH_SHORT).show();
                // Replace the fragment with TimeLearnMenuFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.time_learn_fragment_container, TimeLearnMenuFragment.newInstance())
                        .commit();
            }
        }
    }

    private void shuffleArray(String[] array) {
        List<String> list = Arrays.asList(array);
        Collections.shuffle(list);
        list.toArray(array);
    }
}
