package com.example.edukickstart;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MemoryLearnImageFragment extends Fragment {

    private ImageView imageView;
    private TextView timer, questionView;
    private ImageButton optionButton1, optionButton2, optionButton3;
    private Button startButton, nextButton;

    private int questionCounter = 0, correctAnswers = 0, selectedButton = 0, answer;
    private boolean optionSelected;

    private static final int DATABASE_INDEX = 7;
    private static final int[] DRAWABLES = {
            R.drawable.ic_art,
            R.drawable.ic_blender,
            R.drawable.ic_cake,
            R.drawable.ic_chair,
            R.drawable.ic_cloud,
            R.drawable.ic_cookie,
            R.drawable.ic_croissant,
            R.drawable.ic_diamond,
            R.drawable.ic_hanger,
            R.drawable.ic_house,
            R.drawable.ic_laptop,
            R.drawable.ic_moon,
            R.drawable.ic_party,
            R.drawable.ic_person,
            R.drawable.ic_rabbit,
            R.drawable.ic_scale,
            R.drawable.ic_sun,
            R.drawable.ic_umbrella,
            R.drawable.ic_wand,
            R.drawable.ic_wrench
    };
    private int[] drawableIds;

    public MemoryLearnImageFragment() {
        // Required empty public constructor
    }

    public static MemoryLearnImageFragment newInstance() {
        return new MemoryLearnImageFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_memory_learn_image, container, false);

        timer = view.findViewById(R.id.timer);
        imageView = view.findViewById(R.id.image_view);
        questionView = view.findViewById(R.id.question_text_view);

        optionButton1 = view.findViewById(R.id.option_button_one);
        optionButton2 = view.findViewById(R.id.option_button_two);
        optionButton3 = view.findViewById(R.id.option_button_three);

        startButton = view.findViewById(R.id.start_button);
        nextButton = view.findViewById(R.id.next_button);

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

        startButton.setOnClickListener(v -> startTimer());

        nextButton.setOnClickListener(v -> {
            checkAnswer();
            if (questionCounter < 4) {
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
                        .replace(R.id.memory_learn_fragment_container, MemoryLearnMenuFragment.newInstance())
                        .commit();
            }
        });

        showNextQuestion();

        return view;
    }

    private void showNextQuestion() {
        selectedButton = 0;

        imageView.setVisibility(View.VISIBLE);
        timer.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);

        optionButton1.setVisibility(View.GONE);
        optionButton2.setVisibility(View.GONE);
        optionButton3.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);

        timer.setText("3");
        questionView.setText(R.string.image_memorise);
        answer = DRAWABLES[new Random().nextInt(DRAWABLES.length)];

        imageView.setImageResource(answer);
    }

    private void checkAnswer() {
        optionSelected = true;
        if (selectedButton == 0) {
            optionSelected = false;
        } else if (drawableIds[selectedButton - 1] == answer) {
            correctAnswers++;
        } else {
            List<Integer> list = new ArrayList<>();
            for (int value : drawableIds)
                list.add(value);
            Toast.makeText(requireActivity(), "Correct answer was button " + (list.indexOf(answer) + 1), Toast.LENGTH_SHORT).show();
        }
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
                imageView.setVisibility(View.GONE);

                optionButton1.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
                optionButton2.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
                optionButton3.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));

                optionButton1.setVisibility(View.VISIBLE);
                optionButton2.setVisibility(View.VISIBLE);
                optionButton3.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                questionView.setText(R.string.images_memory);

                Random random = new Random();
                drawableIds = new int[]{
                        answer,
                        DRAWABLES[random.nextInt(DRAWABLES.length)],
                        DRAWABLES[random.nextInt(DRAWABLES.length)],
                };

                while (drawableIds[1] == answer)
                    drawableIds[1] = DRAWABLES[random.nextInt(DRAWABLES.length)];
                while (drawableIds[2] == answer || drawableIds[2] == drawableIds[1])
                    drawableIds[2] = DRAWABLES[random.nextInt(DRAWABLES.length)];

                List<Integer> list = new ArrayList<>();
                for (int value : drawableIds)
                    list.add(value);
                Collections.shuffle(list);
                for (int i = 0; i < drawableIds.length; i++) {
                    drawableIds[i] = list.get(i);
                }

                optionButton1.setImageResource(drawableIds[0]);
                optionButton2.setImageResource(drawableIds[1]);
                optionButton3.setImageResource(drawableIds[2]);
            }
        };

        countDownTimer.start();
    }
}