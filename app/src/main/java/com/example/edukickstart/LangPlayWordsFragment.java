package com.example.edukickstart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LangPlayWordsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LangPlayWordsFragment extends Fragment {

    private static final String ARG_MIX = "Mix";
    private static final String ARG_QUES = "Questions";
    private static final String ARG_ANSW = "Answers";
    private static final String ARG_TIMER = "Timer";

    private boolean mIsFromMix;
    private int mQuestionCounter;
    private int mCorrectAnswersCounter;
    private long mTimeLeft, timeLeftTemp;

    private TextView timer;
    private FrameLayout drawingLayout;
    private DrawingView drawingView;
    private ImageView backgroundImage;
    private TextView questionTextView;
    private final Button[] optionButtons = new Button[3];
    private int selectedButton = 0;
    private ArrayList<Integer> alreadyDone;
    private String answer;
    private String[][] wordLists;
    private double currentAcc;
    private char prevChar = '0';
    private boolean calcComplete = false;
    private boolean isSpelling, optionSelected;

    private Context appContext;
    private Handler mainHandler;
    private int databaseIndex = 6;
    private CountDownTimer countDownTimer;
    private static final Object LOCK = new Object();

    public LangPlayWordsFragment() {
        // Required empty public constructor
    }

    public static LangPlayWordsFragment newInstance(boolean isFromMix, int questionCounter, int correctAnswersCounter, long timeLeft) {
        LangPlayWordsFragment fragment = new LangPlayWordsFragment();
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
        View view = inflater.inflate(R.layout.fragment_lang_play_words, container, false);

        timer = view.findViewById(R.id.timer);
        timer.setText(String.valueOf((int) (mTimeLeft / 1000)));

        drawingLayout = view.findViewById(R.id.drawing_layout);
        questionTextView = view.findViewById(R.id.question_text_view);
        drawingView = drawingLayout.findViewById(R.id.drawing_view);

        optionButtons[0] = view.findViewById(R.id.option_button_one);
        optionButtons[1] = view.findViewById(R.id.option_button_two);
        optionButtons[2] = view.findViewById(R.id.option_button_three);

        Button nextButton = view.findViewById(R.id.next_button);

        appContext = requireActivity().getApplicationContext();
        mainHandler = new Handler(Looper.getMainLooper());

        wordLists = new String[][]{
                getResources().getStringArray(R.array.word_list_easy),
                getResources().getStringArray(R.array.word_list_medium),
                getResources().getStringArray(R.array.word_list_hard),
                getResources().getStringArray(R.array.word_list_difficult),
                getResources().getStringArray(R.array.word_list_very_difficult)
        };

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

        if (mIsFromMix) {
            isSpelling = new Random().nextBoolean();
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

                    isSpelling = true;
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

        nextButton.setOnClickListener(v -> {
            checkAnswer();
            if (optionSelected) {
                if (mIsFromMix) {
                    synchronized (LOCK) {
                        while (!calcComplete) {
                            try {
                                LOCK.wait();
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        mQuestionCounter++;
                        requireActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.lang_play_fragment_container, LangPlayDirectFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                                .commit();
                    }
                } else {
                    isSpelling = !isSpelling;
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
                    databaseIndex = 8;
                UserDatabase database = UserDatabase.getInstance(requireActivity().getApplicationContext());
                UserDao userDao = database.userDao();
                AppExecutor.getInstance().execute(() -> {
                    synchronized (LOCK) {
                        while (!calcComplete) {
                            try {
                                LOCK.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        int score = mCorrectAnswersCounter * 100 - (mQuestionCounter - mCorrectAnswersCounter) * 10;
                        User user = userDao.findLoggedInUser();
                        ArrayList<String> scores = user.getHighScores();
                        if (Integer.parseInt(scores.get(databaseIndex)) < score) {
                            scores.set(databaseIndex, String.valueOf(score));
                            user.setHighScores(scores);
                            userDao.updateUser(user);
                            mainHandler.post(() -> Toast.makeText(appContext, "Correct answers: " + mCorrectAnswersCounter + " out of " + mQuestionCounter, Toast.LENGTH_SHORT).show());
                            mainHandler.post(() -> Toast.makeText(appContext, "Score: " + score, Toast.LENGTH_SHORT).show());
                            mainHandler.post(() -> Toast.makeText(appContext, "New high score!", Toast.LENGTH_SHORT).show());
                        }
                    }
                });

                // Replace the fragment with TimePlayMenuFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.lang_play_fragment_container, LangPlayMenuFragment.newInstance())
                        .commit();
            }
        };

        countDownTimer.start();
    }

    private void showQuestion() {
        if (isSpelling) {
            calcComplete = true;

            drawingLayout.setVisibility(View.GONE);
            drawingView.setVisibility(View.GONE);
            questionTextView.setText(R.string.question_spell);
            questionTextView.setTextSize(36f);

            selectedButton = 0;
            int difficulty = mQuestionCounter / 3;
            if (difficulty > 4)
                difficulty = 4;
            for (Button button : optionButtons) {
                button.setVisibility(View.VISIBLE);
                button.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
            }

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
        } else {
            calcComplete = true;

            drawingLayout.setVisibility(View.VISIBLE);
            drawingView.setVisibility(View.VISIBLE);

            for (Button button : optionButtons)
                button.setVisibility(View.GONE);

            char currentLetter;
            do currentLetter = generateRandomLetter();
            while (currentLetter == prevChar);
            prevChar = currentLetter;

            String formattedString = String.format(getString(R.string.write_question), currentLetter);
            questionTextView.setText(formattedString);
            questionTextView.setTextSize(24f);

            backgroundImage = drawingLayout.findViewById(R.id.background_image);
            backgroundImage.setImageResource(charToDrawable(currentLetter));

            drawingView.clearDrawing();
            drawingView.setDrawingCacheEnabled(true);
        }
    }

    private void checkAnswer() {
        if (isSpelling) {
            optionSelected = true;
            if (selectedButton == 0) {
                optionSelected = false;
                Toast.makeText(requireActivity(), "Please select an option", Toast.LENGTH_SHORT).show();
            } else if (optionButtons[selectedButton - 1].getText().toString().equals(answer))
                mCorrectAnswersCounter++;
            else
                Toast.makeText(requireActivity(), "Correct answer was " + answer, Toast.LENGTH_SHORT).show();
        } else {
            optionSelected = true;
            Toast.makeText(requireActivity(), "Calculating...", Toast.LENGTH_SHORT).show();
            calcComplete = false;

            // Get the drawn letter bitmap from the drawing layout
            Bitmap drawnLetterBitmap = Bitmap.createBitmap(drawingView.getDrawingCache());
            drawingView.destroyDrawingCache();
            drawingView.setDrawingCacheEnabled(false);

            // Get the outline letter bitmap for the current letter
            Bitmap outlineLetterBitmap = Bitmap.createBitmap(backgroundImage.getWidth(), backgroundImage.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(outlineLetterBitmap);
            backgroundImage.draw(canvas);

            AppExecutor.getInstance().execute(() -> {
                try {
                    assert drawnLetterBitmap.getWidth() == outlineLetterBitmap.getWidth();
                    assert drawnLetterBitmap.getHeight() == outlineLetterBitmap.getHeight();

                    currentAcc = calculateSimilarityPercentage(drawnLetterBitmap, outlineLetterBitmap);
                    if (currentAcc > 75) {
                        mainHandler.post(() -> Toast.makeText(appContext, "Sufficient accuracy of: " + (int) currentAcc + "%", Toast.LENGTH_SHORT).show());
                        mCorrectAnswersCounter++;
                    } else
                        mainHandler.post(() -> Toast.makeText(appContext, "Insufficient accuracy of: " + (int) currentAcc + "%", Toast.LENGTH_SHORT).show());
                    synchronized (LOCK) {
                        calcComplete = true;
                        LOCK.notify();
                    }
                } catch (AssertionError e) {
                    mainHandler.post(() -> Toast.makeText(appContext, "Unexpected Error", Toast.LENGTH_SHORT).show());
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.lang_play_fragment_container, LangPlayMenuFragment.newInstance())
                            .commit();
                }
            });
        }
    }

    private int charToDrawable(char character) {
        switch (Character.toLowerCase(character)) {
            case 'a':
                return R.drawable.letter_a;
            case 'b':
                return R.drawable.letter_b;
            case 'c':
                return R.drawable.letter_c;
            case 'd':
                return R.drawable.letter_d;
            case 'e':
                return R.drawable.letter_e;
            case 'f':
                return R.drawable.letter_f;
            case 'g':
                return R.drawable.letter_g;
            case 'h':
                return R.drawable.letter_h;
            case 'i':
                return R.drawable.letter_i;
            case 'j':
                return R.drawable.letter_j;
            case 'k':
                return R.drawable.letter_k;
            case 'l':
                return R.drawable.letter_l;
            case 'm':
                return R.drawable.letter_m;
            case 'n':
                return R.drawable.letter_n;
            case 'o':
                return R.drawable.letter_o;
            case 'p':
                return R.drawable.letter_p;
            case 'q':
                return R.drawable.letter_q;
            case 'r':
                return R.drawable.letter_r;
            case 's':
                return R.drawable.letter_s;
            case 't':
                return R.drawable.letter_t;
            case 'u':
                return R.drawable.letter_u;
            case 'v':
                return R.drawable.letter_v;
            case 'w':
                return R.drawable.letter_w;
            case 'x':
                return R.drawable.letter_x;
            case 'y':
                return R.drawable.letter_y;
            case 'z':
                return R.drawable.letter_z;
            default:
                return 0;
        }
    }

    private double calculateSimilarityPercentage(Bitmap bitmap1, Bitmap bitmap2) {
        int totalPixels = 0;
        int matchingPixels = 0;

        for (int x = 0; x < bitmap1.getWidth(); x++) {
            for (int y = 0; y < bitmap1.getHeight(); y++) {
                int pixel1 = bitmap1.getPixel(x, y);
                int pixel2 = bitmap2.getPixel(x, y);

                int alpha1 = Color.alpha(pixel1);
                int alpha2 = Color.alpha(pixel2);

                if (alpha1 == 0 && alpha2 == 0) {
                    continue;
                }

                totalPixels++;

                if (alpha1 > 0)
                    alpha1 = 1;
                if (alpha2 > 0)
                    alpha2 = 1;

                if (alpha1 == alpha2) {
                    matchingPixels++;
                }
            }
        }

        int finalTotalPixels = totalPixels;
        int finalMatchingPixels = matchingPixels;

        // Leniency calculation
        double acc = finalMatchingPixels * 1.7;
        if (acc > finalTotalPixels)
            acc = finalTotalPixels * 1.0;
        acc /= finalTotalPixels;
        acc *= 100;

        Log.d("IMAGE", "Total pixels: " + finalTotalPixels + " compared to " + finalMatchingPixels);
        return acc;
    }

    private char generateRandomLetter() {
        Random random = new Random();
        return (char) (random.nextInt(26) + 'A');
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}