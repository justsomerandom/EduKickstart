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
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisualPlayMatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisualPlayMatchFragment extends Fragment {

    private static final String ARG_MIX = "Mix";
    private static final String ARG_QUES = "Questions";
    private static final String ARG_ANSW = "Answers";
    private static final String ARG_TIMER = "Timer";

    private boolean mIsFromMix;
    private int mQuestionCounter;
    private int mCorrectAnswersCounter;
    private long mTimeLeft, timeLeftTemp;

    private TextView timer;
    private final ImageButton[] imageButtons = new ImageButton[18];
    private boolean buttonPressed;
    private int pressedButton;

    private final int[] drawablesQues = new int[9];
    private final int[] drawableRes = new int[18];

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

    private int databaseIndex = 13;
    private CountDownTimer countDownTimer;

    public VisualPlayMatchFragment() {
        // Required empty public constructor
    }

    public static VisualPlayMatchFragment newInstance(boolean isFromMix, int questionCounter, int correctAnswersCounter, long timeLeft) {
        VisualPlayMatchFragment fragment = new VisualPlayMatchFragment();
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
        View view = inflater.inflate(R.layout.fragment_visual_play_match, container, false);

        timer = view.findViewById(R.id.timer);
        timer.setText(String.valueOf((int) (mTimeLeft / 1000)));

        GridLayout layout1 = view.findViewById(R.id.grid_layout);

        imageButtons[0] = layout1.findViewById(R.id.image_button_one);
        imageButtons[1] = layout1.findViewById(R.id.image_button_two);
        imageButtons[2] = layout1.findViewById(R.id.image_button_three);
        imageButtons[3] = layout1.findViewById(R.id.image_button_four);
        imageButtons[4] = layout1.findViewById(R.id.image_button_five);
        imageButtons[5] = layout1.findViewById(R.id.image_button_six);
        imageButtons[6] = layout1.findViewById(R.id.image_button_seven);
        imageButtons[7] = layout1.findViewById(R.id.image_button_eight);
        imageButtons[8] = layout1.findViewById(R.id.image_button_nine);
        imageButtons[9] = layout1.findViewById(R.id.image_button_ten);
        imageButtons[10] = layout1.findViewById(R.id.image_button_eleven);
        imageButtons[11] = layout1.findViewById(R.id.image_button_twelve);
        imageButtons[12] = layout1.findViewById(R.id.image_button_thirteen);
        imageButtons[13] = layout1.findViewById(R.id.image_button_fourteen);
        imageButtons[14] = layout1.findViewById(R.id.image_button_fifteen);
        imageButtons[15] = layout1.findViewById(R.id.image_button_sixteen);
        imageButtons[16] = layout1.findViewById(R.id.image_button_seventeen);
        imageButtons[17] = layout1.findViewById(R.id.image_button_eighteen);

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

        if (mIsFromMix)
            showQuestion();

        for (int i = 0; i < 18; i++) {
            int temp = i;
            imageButtons[temp].setOnClickListener(v -> {
                imageButtons[temp].setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
                if (buttonPressed) {
                    if (pressedButton == temp) {
                        deselectButton(imageButtons[temp]);
                    } else {
                        checkMatch(imageButtons[pressedButton], imageButtons[temp]);
                        mQuestionCounter++;
                        if (checkFinished()) {
                            if (mIsFromMix)
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.visual_play_fragment_container, VisualPlayBallFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                                        .commit();
                            else
                                showQuestion();
                        }
                    }
                } else {
                    pressedButton = temp;
                    buttonPressed = true;
                }
            });
        }

        showQuestion();
        return view;
    }

    private boolean checkFinished() {
        int invisibleButtons = 0;
        for (ImageButton button : imageButtons) {
            if (!button.isEnabled())
                invisibleButtons++;
        }

        return invisibleButtons == imageButtons.length;
    }

    private void checkMatch(ImageButton button1, ImageButton button2) {
        if (getDrawableRes(button1) == getDrawableRes(button2)) {
            Animation fadeOutAnim = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_out);
            fadeOutAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    button1.setAlpha(0.0f);
                    button2.setAlpha(0.0f);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            button1.setEnabled(false);
            button2.setEnabled(false);
            button1.startAnimation(fadeOutAnim);
            button2.startAnimation(fadeOutAnim);
            mCorrectAnswersCounter++;
        } else {
            button1.setBackgroundColor(getResources().getColor(R.color.white, requireActivity().getTheme()));
            button2.setBackgroundColor(getResources().getColor(R.color.white, requireActivity().getTheme()));
            Toast.makeText(requireActivity(), "These images do not match", Toast.LENGTH_SHORT).show();
        }
        buttonPressed = false;
    }

    private void deselectButton(ImageButton button) {
        button.setBackgroundColor(getResources().getColor(R.color.white, requireActivity().getTheme()));
        buttonPressed = false;
    }

    private void showQuestion() {
        int[] drawablesTemp = Arrays.copyOf(DRAWABLES, DRAWABLES.length);

        List<Integer> list = new ArrayList<>();
        for (int drawable : drawablesTemp)
            list.add(drawable);
        Collections.shuffle(list);

        for (int i = 0; i < 9; i++)
            drawablesQues[i] = list.get(i);

        list.clear();
        for(int drawable : drawablesQues) {
            list.add(drawable);
            list.add(drawable);
        }
        Collections.shuffle(list);

        Animation fadeInAnim = AnimationUtils.loadAnimation(requireActivity(), R.anim.fade_in);
        fadeInAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                for (ImageButton button : imageButtons)
                    button.setAlpha(1.0f);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        for (int i = 0; i < 18; i++) {
            imageButtons[i].startAnimation(fadeInAnim);
            imageButtons[i].setBackgroundColor(getResources().getColor(R.color.white, requireActivity().getTheme()));
            imageButtons[i].setEnabled(true);
            imageButtons[i].setImageResource(list.get(i));
            setDrawableRes(i, list.get(i));
        }
    }

    private void setDrawableRes(int index, int value) {
        drawableRes[index] = value;
    }

    private int getDrawableRes(ImageButton button) {
        List<ImageButton> tmp = Arrays.asList(Arrays.copyOf(imageButtons, imageButtons.length));
        return drawableRes[tmp.indexOf(button)];
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
                    databaseIndex = 15;
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
                        .replace(R.id.visual_play_fragment_container, VisualPlayMenuFragment.newInstance())
                        .commit();
            }
        };

        countDownTimer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}