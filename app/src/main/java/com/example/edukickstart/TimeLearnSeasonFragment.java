package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeLearnSeasonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeLearnSeasonFragment extends Fragment {

    private final ArrayList<Seasons> seasons = new ArrayList<>();
    private final Seasons summer = Seasons.SUMMER;
    private final Seasons spring = Seasons.SPRING;
    private final Seasons autumn = Seasons.AUTUMN;
    private final Seasons winter = Seasons.WINTER;
    private TextView mainQuestion1, mainQuestion2, questionText;
    private Button optionsButton1, optionsButton2;
    private int questionCounter;
    private int correctAnswers;
    private String correctAnswer;
    private static final int DATABASE_INDEX = 4;

    public TimeLearnSeasonFragment() {
        // Required empty public constructor
    }

    public static TimeLearnSeasonFragment newInstance() {
        return new TimeLearnSeasonFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_learn_season, container, false);

        mainQuestion1 = view.findViewById(R.id.main_question_view_one);
        mainQuestion2 = view.findViewById(R.id.main_question_view_two);
        questionText = view.findViewById(R.id.question_text);

        optionsButton1 = view.findViewById(R.id.option_button_one);
        optionsButton2 = view.findViewById(R.id.option_button_two);

        seasons.add(summer);
        seasons.add(spring);
        seasons.add(autumn);
        seasons.add(winter);

        optionsButton1.setOnClickListener(v1 -> {
            checkAnswer(v1);
            questionCheck();
        });
        optionsButton2.setOnClickListener(v2 -> {
            checkAnswer(v2);
            questionCheck();
        });

        nextQuestion();

        return view;
    }

    private void nextQuestion() {
        Random random = new Random();
        switch (random.nextInt(3)) {
            case 0:
                int i = random.nextInt(3);
                int j = random.nextInt(3);
                while (j == i)
                    j = random.nextInt(3);
                generateKeywordQuestion(seasons.get(random.nextInt(4)), i, j);
                break;
            case 1:
                generateTemperatureQuestion(random.nextInt(2));
                break;
            case 2:
                generateAdjacentQuestion();
                break;
            default:
                break;
        }
    }

    private void generateKeywordQuestion(Seasons season, int index1, int index2) {
        String[] keywords = season.getKeywords();
        correctAnswer = season.getName();

        mainQuestion1.setText(keywords[index1]);
        mainQuestion2.setText(keywords[index2]);

        questionText.setText(R.string.seasons_keyword_question);

        ArrayList<Seasons> tempSeasons = new ArrayList<Seasons>() {
            {
                add(Seasons.AUTUMN);
                add(Seasons.SPRING);
            }
        };
        tempSeasons.set(0, season);
        tempSeasons.set(1, seasons.get(new Random().nextInt(4)));
        while (tempSeasons.get(1) == tempSeasons.get(0))
            tempSeasons.set(1, seasons.get(new Random().nextInt(4)));
        Collections.shuffle(tempSeasons);

        optionsButton1.setText(tempSeasons.get(0).getName());
        optionsButton2.setText(tempSeasons.get(1).getName());
    }

    private void generateTemperatureQuestion(int type) {
        Collections.shuffle(seasons);
        Seasons season1 = seasons.get(0);
        Seasons season2 = seasons.get(1);
        String warmOrCold;

        correctAnswer = season1.getName();
        if (type == 0) {
            warmOrCold = "warmer";
            if (season2.getTemperature() > season1.getTemperature())
                correctAnswer = season2.getName();
        }
        else {
            warmOrCold = "colder";
            if (season2.getTemperature() < season1.getTemperature())
                correctAnswer = season2.getName();
        }

        String quesString = String.format(getResources().getString(R.string.season_temp_question), warmOrCold);

        mainQuestion1.setText(season1.getName());
        mainQuestion2.setText(season2.getName());

        questionText.setText(quesString);

        optionsButton1.setText(season1.getName());
        optionsButton2.setText(season2.getName());
    }

    private void generateAdjacentQuestion() {
        Collections.shuffle(seasons);
        Seasons season1 = seasons.get(0);
        Seasons season2 = seasons.get(1);

        if (season1.isAdjacent() == season2.isAdjacent())
            correctAnswer = "No";
        else correctAnswer = "Yes";


        mainQuestion1.setText(season1.getName());
        mainQuestion2.setText(season2.getName());

        questionText.setText(R.string.season_adj_question);

        optionsButton1.setText(R.string.yes);
        optionsButton2.setText(R.string.no);
    }

    private void checkAnswer(View view) {
        Button pressedButton = (Button) view;
        if (pressedButton.getText().toString().equals(correctAnswer))
            correctAnswers++;
        else
            Toast.makeText(requireActivity().getApplicationContext(), "Correct answer was: " + correctAnswer, Toast.LENGTH_SHORT).show();
    }

    private void questionCheck() {
        if (questionCounter < 9) {
            nextQuestion();
            questionCounter++;
        } else {
            UserDatabase database = UserDatabase.getInstance(requireActivity().getApplicationContext());
            UserDao userDao = database.userDao();
            AppExecutor.getInstance().execute(() -> {
                User user = userDao.findLoggedInUser();
                ArrayList<String> progress = user.getProgress();
                int newWeightedAccuracy;
                if (progress.get(DATABASE_INDEX).equals("0"))
                    newWeightedAccuracy = correctAnswers * 10;
                else
                    newWeightedAccuracy = (Integer.parseInt(progress.get(DATABASE_INDEX)) + correctAnswers * 10) / 2;
                progress.set(DATABASE_INDEX, String.valueOf(newWeightedAccuracy));
                user.setProgress(progress);
                userDao.updateUser(user);
            });
            // Display the final score or navigate to the menu fragment
            Toast.makeText(getActivity(), "Correct answers: " + correctAnswers + " out of 10", Toast.LENGTH_SHORT).show();
            // Replace the fragment with TimeLearnMenuFragment
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.time_learn_fragment_container, TimeLearnMenuFragment.newInstance())
                    .commit();
        }
    }
}