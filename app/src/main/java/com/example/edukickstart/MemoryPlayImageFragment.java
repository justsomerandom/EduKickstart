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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemoryPlayImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemoryPlayImageFragment extends Fragment {

    private static final String ARG_MIX = "Mix";
    private static final String ARG_QUES = "Questions";
    private static final String ARG_ANSW = "Answers";
    private static final String ARG_TIMER = "Timer";

    private boolean mIsFromMix;
    private int mQuestionCounter;
    private int mCorrectAnswersCounter;
    private long mTimeLeft , timeLeftTemp;

    private ImageView imageView;
    private TextView timer, subTimer, questionView;
    private ImageButton optionButton1, optionButton2, optionButton3;
    private Button startButton, nextButton;

    private int selectedButton = 0, answer;
    private boolean optionSelected;

    private int databaseIndex = 4;
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
    private CountDownTimer countDownTimer;

    public MemoryPlayImageFragment() {
        // Required empty public constructor
    }

    public static MemoryPlayImageFragment newInstance(boolean isFromMix, int questionCounter, int correctAnswersCounter, long timeLeft) {
        MemoryPlayImageFragment fragment = new MemoryPlayImageFragment();
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
        View view = inflater.inflate(R.layout.fragment_memory_play_image, container, false);

        timer = view.findViewById(R.id.timer);
        timer.setText(String.valueOf((int) (mTimeLeft / 1000)));

        subTimer = view.findViewById(R.id.sub_timer);
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
        nextButton.setOnClickListener(v -> {
            checkAnswer();
            if (optionSelected) {
                if (mIsFromMix) {
                    mQuestionCounter++;
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.memory_play_fragment_container, MemoryPlayDigitsFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                            .commit();
                } else {
                    showQuestion();
                    mQuestionCounter++;
                }
            }
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
        selectedButton = 0;

        imageView.setVisibility(View.VISIBLE);
        subTimer.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.VISIBLE);

        optionButton1.setVisibility(View.GONE);
        optionButton2.setVisibility(View.GONE);
        optionButton3.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);

        subTimer.setText("3");
        questionView.setText(R.string.image_memorise);
        answer = DRAWABLES[new Random().nextInt(DRAWABLES.length)];

        imageView.setImageResource(answer);
    }

    private void checkAnswer() {
        optionSelected = true;
        if (selectedButton == 0) {
            optionSelected = false;
        } else if (drawableIds[selectedButton - 1] == answer) {
            mCorrectAnswersCounter++;
        } else {
            List<Integer> list = new ArrayList<>();
            for (int value : drawableIds)
                list.add(value);
            Toast.makeText(requireActivity(), "Correct answer was button " + (list.indexOf(answer) + 1), Toast.LENGTH_SHORT).show();
        }
    }

    private void startSubTimer() {
        startButton.setVisibility(View.GONE);

        CountDownTimer subTimerCount = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) millisUntilFinished / 1000;
                subTimer.setText(String.valueOf(secondsLeft));
            }

            public void onFinish() {
                subTimer.setVisibility(View.GONE);
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

        subTimerCount.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}