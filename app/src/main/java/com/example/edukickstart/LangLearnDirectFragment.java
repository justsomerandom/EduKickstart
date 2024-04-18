package com.example.edukickstart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LangLearnDirectFragment extends Fragment {

    private ImageView imageView;
    private final Button[] optionButtons = new Button[4];
    private int questionCounter = 0, correctAnswers = 0, selectedButton = 0;
    private String answer;
    private boolean optionSelected, compassQuestion;

    private static final int DATABASE_INDEX = 10;

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

    public LangLearnDirectFragment() {
        // Required empty public constructor
    }

    public static LangLearnDirectFragment newInstance() {
        return new LangLearnDirectFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lang_learn_direct, container, false);

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

        nextButton.setOnClickListener(v -> {
            checkAnswer();
            if (questionCounter < 4) {
                if (optionSelected) {
                    showNextQuestion();
                    questionCounter++;
                }
            } else {
                AppExecutor.getInstance().execute(() -> {
                    UserDatabase database = UserDatabase.getInstance(requireActivity());
                    UserDao userDao = database.userDao();
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
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.lang_learn_fragment_container, LangLearnMenuFragment.newInstance())
                        .commit();
            }
        });

        compassQuestion = false;
        showNextQuestion();

        return view;
    }

    private void showNextQuestion() {
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
            correctAnswers++;
        else
            Toast.makeText(requireActivity(), "Correct answer was: " + answer, Toast.LENGTH_SHORT).show();
    }
}