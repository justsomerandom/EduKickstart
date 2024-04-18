package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TimeLearnDigitalFragment extends Fragment {

    private TextView digitalClock;
    private Button optionButton1;
    private Button optionButton2;
    private Button optionButton3;
    private int selectedButton = 0;
    private List<String> digitalTimes;
    private String correctDigitalTime;
    private boolean optionSelected;
    private int questionCounter = 0;
    private int correctAnswersCounter = 0;
    private static final int DATABASE_INDEX = 1;

    public TimeLearnDigitalFragment() {
        // Required empty public constructor
    }

    public static TimeLearnDigitalFragment newInstance() {
        return new TimeLearnDigitalFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_learn_digital, container, false);

        digitalClock = view.findViewById(R.id.digital_clock);
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
                break;
        }

        if (optionSelected) {
            if (selectedOption.equals(correctDigitalTime)) {
                correctAnswersCounter++;
            } else {
                Toast.makeText(getActivity(), "Correct answer: " + correctDigitalTime, Toast.LENGTH_SHORT).show();
            }
        }
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