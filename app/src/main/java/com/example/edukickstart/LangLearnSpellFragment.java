package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class LangLearnSpellFragment extends Fragment {

    private final Button[] optionButtons = new Button[3];
    private int selectedButton = 0, questionCounter = 0, correctAnswers = 0, difficulty;
    private ArrayList<Integer> alreadyDone;
    private boolean optionSelected;
    private String answer;
    private String[][] wordLists;

    private UserDao userDao;
    private static final int DATABASE_INDEX = 8;

    public LangLearnSpellFragment() {
        // Required empty public constructor
    }

    public static LangLearnSpellFragment newInstance() {
        return new LangLearnSpellFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lang_learn_spell, container, false);

        optionButtons[0] = view.findViewById(R.id.option_button_one);
        optionButtons[1] = view.findViewById(R.id.option_button_two);
        optionButtons[2] = view.findViewById(R.id.option_button_three);
        Button nextButton = view.findViewById(R.id.next_button);

        wordLists = new String[][]{
                getResources().getStringArray(R.array.word_list_easy),
                getResources().getStringArray(R.array.word_list_medium),
                getResources().getStringArray(R.array.word_list_hard),
                getResources().getStringArray(R.array.word_list_difficult),
                getResources().getStringArray(R.array.word_list_very_difficult)
        };

        UserDatabase database = UserDatabase.getInstance(requireActivity());
        userDao = database.userDao();
        int acc = Integer.parseInt(userDao.findLoggedInUser().getProgress().get(DATABASE_INDEX));

        if (acc < 20)
            difficulty = 0;
        else if (acc < 40)
            difficulty = 1;
        else if (acc < 60)
            difficulty = 2;
        else if (acc < 80)
            difficulty = 3;
        else
            difficulty = 4;
        alreadyDone = new ArrayList<>();

        optionButtons[0].setOnClickListener(v -> {
            selectedButton = 1;
            optionButtons[0].setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
            optionButtons[1].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[2].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
        });

        optionButtons[1].setOnClickListener(v -> {
            selectedButton = 2;
            optionButtons[0].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[1].setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
            optionButtons[2].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
        });

        optionButtons[2].setOnClickListener(v -> {
            selectedButton = 3;
            optionButtons[0].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[1].setBackgroundColor(getResources().getColor(R.color.green_700, requireActivity().getTheme()));
            optionButtons[2].setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
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

        showNextQuestion();

        return view;
    }

    private void showNextQuestion() {
        selectedButton = 0;
        for (int i = 0; i < 3; i++)
            optionButtons[i].setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));

        Random random = new Random();
        int wordIndex;
        do wordIndex = random.nextInt(wordLists[difficulty].length);
        while (alreadyDone.contains(wordIndex));
        String[] words = wordLists[difficulty][wordIndex].split(",");

        answer = words[0];
        alreadyDone.add(wordIndex);

        List<String> list = Arrays.asList(words);
        Collections.shuffle(list);
        list.toArray(words);

        for (int i = 0; i < 3; i++) {
            optionButtons[i].setText(words[i]);
        }
    }

    private void checkAnswer() {
        optionSelected = true;
        if (selectedButton == 0) {
            optionSelected = false;
            Toast.makeText(requireActivity(), "Please select an option", Toast.LENGTH_SHORT).show();
        } else if (optionButtons[selectedButton - 1].getText().toString().equals(answer))
            correctAnswers++;
        else
            Toast.makeText(requireActivity(), "Correct answer was " + answer, Toast.LENGTH_SHORT).show();
    }
}