package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeLearnAnalogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeLearnAnalogFragment extends Fragment {

    private AnalogClockView analogClock;
    private Button optionButton1;
    private Button optionButton2;
    private Button optionButton3;
    private int selectedButton = 0;
    private List<String> digitalTimes;
    private String correctDigitalTime;
    private boolean optionSelected;
    private int questionCounter = 0;
    private int correctAnswersCounter = 0;
    private static final int DATABASE_INDEX = 0;

    public TimeLearnAnalogFragment() {
        // Required empty public constructor
    }

    public static TimeLearnAnalogFragment newInstance() {
        return new TimeLearnAnalogFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_learn_analog, container, false);
        analogClock = view.findViewById(R.id.analog_clock);
        optionButton1 = view.findViewById(R.id.option_button_one);
        optionButton2 = view.findViewById(R.id.option_button_two);
        optionButton3 = view.findViewById(R.id.option_button_three);
        Button nextButton = view.findViewById(R.id.next_button);

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
            if (questionCounter < 9) {
                if (optionSelected) {
                    showNextQuestion();
                    questionCounter++;
                }
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
        });

        showNextQuestion();

        return view;
    }

    private void showNextQuestion() {
        selectedButton = 0;
        optionButton1.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
        optionButton2.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
        optionButton3.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));

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
                correctAnswersCounter++;
            } else {
                Toast.makeText(getActivity(), "Correct answer: " + correctDigitalTime, Toast.LENGTH_SHORT).show();
            }
        }
    }
}