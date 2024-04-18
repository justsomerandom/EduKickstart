package com.example.edukickstart;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TimePlayCalendarFragment extends Fragment {

    private static final String ARG_MIX = "Mix";
    private static final String ARG_QUES = "Questions";
    private static final String ARG_ANSW = "Answers";
    private static final String ARG_TIMER = "Timer";

    private boolean mIsFromMix;
    private int mQuestionCounter;
    private int mCorrectAnswersCounter;
    private long mTimeLeft;

    private TextView timer, questionTextView;
    private CountDownTimer countDownTimer;
    private LinearLayout reorderLayout;
    private Button optionButton1, optionButton2, optionButton3, altButton1, altButton2, nextButton;
    private EditText answerEditText;

    private int questionVariant, subQuestionVariant, correctButton, selectedButton = 0;
    private boolean optionSelected;
    private String[] months;
    private String answer;

    private final String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    private final ArrayList<Seasons> seasons = new ArrayList<>();
    private final Seasons summer = Seasons.SUMMER;
    private final Seasons spring = Seasons.SPRING;
    private final Seasons autumn = Seasons.AUTUMN;
    private final Seasons winter = Seasons.WINTER;
    private final Random random = new Random();
    private long timeLeftTemp;

    private int databaseIndex = 1;

    public TimePlayCalendarFragment() {
        // Required empty public constructor
    }

    public static TimePlayCalendarFragment newInstance(boolean isFromMix, int questionCounter, int correctAnswersCounter, long timeLeft) {
        TimePlayCalendarFragment fragment = new TimePlayCalendarFragment();
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
        View view = inflater.inflate(R.layout.fragment_time_play_calendar, container, false);

        timer = view.findViewById(R.id.timer);
        timer.setText(String.valueOf((int) (mTimeLeft / 1000)));
        questionTextView = view.findViewById(R.id.question_text_view);
        reorderLayout = view.findViewById(R.id.reorder_layout);
        optionButton1 = view.findViewById(R.id.option_button_one);
        optionButton2 = view.findViewById(R.id.option_button_two);
        optionButton3 = view.findViewById(R.id.option_button_three);
        altButton1 = view.findViewById(R.id.alt_button_one);
        altButton2 = view.findViewById(R.id.alt_button_two);
        answerEditText = view.findViewById(R.id.answer_edit_text);
        nextButton = view.findViewById(R.id.next_button);

        seasons.add(summer);
        seasons.add(spring);
        seasons.add(autumn);
        seasons.add(winter);
        months = getResources().getStringArray(R.array.months);

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

        altButton1.setOnClickListener(v1 -> {
            selectedButton = 1;
            checkAnswer();
            if (mIsFromMix) {
                mQuestionCounter++;
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.time_play_fragment_container, TimePlayClocksFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                        .commit();
            } else {
                questionVariant = random.nextInt(3);
                showNextQuestion();
                mQuestionCounter++;
            }
        });
        altButton2.setOnClickListener(v2 -> {
            selectedButton = 2;
            checkAnswer();
            if (mIsFromMix) {
                mQuestionCounter++;
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.time_play_fragment_container, TimePlayClocksFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                        .commit();
            } else {
                questionVariant = random.nextInt(3);
                showNextQuestion();
                mQuestionCounter++;
            }
        });

        if (mIsFromMix) {
            questionVariant = random.nextInt(3);
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

                    questionVariant = random.nextInt(3);
                    startTimer();
                    showNextQuestion();
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

        nextButton.setOnClickListener(v -> nextFunc());
        answerEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            nextFunc();
            return true;
        });

        if (mIsFromMix)
            showNextQuestion();

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
                    databaseIndex = 2;
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
                        .replace(R.id.time_play_fragment_container, TimePlayMenuFragment.newInstance())
                        .commit();
            }
        };

        countDownTimer.start();
    }

    private void showNextQuestion() {
        reorderLayout.removeAllViews();
        switch (questionVariant) {
            case 0:
                optionButton1.setVisibility(View.VISIBLE);
                optionButton2.setVisibility(View.VISIBLE);
                optionButton3.setVisibility(View.VISIBLE);
                answerEditText.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);

                altButton1.setVisibility(View.GONE);
                altButton2.setVisibility(View.GONE);

                showWeekQuestion();
                break;
            case 1:
                answerEditText.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);

                altButton1.setVisibility(View.GONE);
                altButton2.setVisibility(View.GONE);
                optionButton1.setVisibility(View.GONE);
                optionButton2.setVisibility(View.GONE);
                optionButton3.setVisibility(View.GONE);

                showMonthQuestion();
                break;
            case 2:

                altButton1.setVisibility(View.VISIBLE);
                altButton2.setVisibility(View.VISIBLE);

                optionButton1.setVisibility(View.GONE);
                optionButton2.setVisibility(View.GONE);
                optionButton3.setVisibility(View.GONE);
                answerEditText.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);

                showSeasonQuestion();
                break;
        }
    }

    private void showWeekQuestion() {
        subQuestionVariant = random.nextInt(3);

        selectedButton = 0;
        optionButton1.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
        optionButton2.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));
        optionButton3.setBackgroundColor(getResources().getColor(R.color.green_500, requireActivity().getTheme()));

        switch (subQuestionVariant) {
            case 0:
                // Set the question text
                questionTextView.setText(R.string.day_number_question);

                // Generate a random day
                int dayIndex1 = random.nextInt(daysOfWeek.length);
                TextView weekTextView1 = createTextView(daysOfWeek[dayIndex1], 74);
                reorderLayout.addView(weekTextView1);
                answer = Integer.toString(dayIndex1 + 1);

                if (answer.equals("Wednesday"))
                    weekTextView1.setTextSize(64f);
                else
                    weekTextView1.setTextSize(74f);

                // Set the options
                String[] options1 = new String[3];
                options1[0] = Integer.toString(dayIndex1 + 1);
                options1[1] = Integer.toString(random.nextInt(daysOfWeek.length) + 1);
                options1[2] = Integer.toString(random.nextInt(daysOfWeek.length) + 1);
                while (options1[1].equals(options1[0]))
                    options1[1] = Integer.toString(random.nextInt(daysOfWeek.length) + 1);
                while (options1[2].equals(options1[0]) || options1[2].equals(options1[1]))
                    options1[2] = Integer.toString(random.nextInt(daysOfWeek.length) + 1);

                // Shuffle the options randomly
                shuffleArray(options1);
                optionButton1.setText(options1[0]);
                optionButton2.setText(options1[1]);
                optionButton3.setText(options1[2]);

                for (int i = 0; i < 3; i++) {
                    if (options1[i].equals(answer))
                        correctButton = i + 1;
                }

                // Show the buttons and hide the EditText
                optionButton1.setVisibility(View.VISIBLE);
                optionButton2.setVisibility(View.VISIBLE);
                optionButton3.setVisibility(View.VISIBLE);
                answerEditText.setVisibility(View.GONE);
                break;
            case 1:
                // Set the question text
                questionTextView.setText(R.string.day_name_question);

                // Generate a random day
                int dayIndex2 = random.nextInt(daysOfWeek.length);
                TextView weekTextView2 = createTextView(Integer.toString(dayIndex2 + 1), 86);
                reorderLayout.addView(weekTextView2);
                answer = daysOfWeek[dayIndex2];

                // Set the options
                String[] options2 = new String[3];
                options2[0] = daysOfWeek[dayIndex2];
                options2[1] = daysOfWeek[random.nextInt(daysOfWeek.length)];
                options2[2] = daysOfWeek[random.nextInt(daysOfWeek.length)];
                while (options2[1].equals(options2[0]))
                    options2[1] = daysOfWeek[random.nextInt(daysOfWeek.length)];
                while (options2[2].equals(options2[0]) || options2[2].equals(options2[1]))
                    options2[2] = daysOfWeek[random.nextInt(daysOfWeek.length)];

                // Shuffle the options randomly
                shuffleArray(options2);
                optionButton1.setText(options2[0]);
                optionButton2.setText(options2[1]);
                optionButton3.setText(options2[2]);

                for (int i = 0; i < 3; i++) {
                    if (options2[i].equals(answer))
                        correctButton = i + 1;
                }

                // Show the buttons and hide the EditText
                optionButton1.setVisibility(View.VISIBLE);
                optionButton2.setVisibility(View.VISIBLE);
                optionButton3.setVisibility(View.VISIBLE);
                answerEditText.setVisibility(View.GONE);
                break;
            case 2:
                // Set the question text
                questionTextView.setText(R.string.day_complete_question);

                // Generate a random day
                Random random = new Random();
                int dayIndex3 = random.nextInt(daysOfWeek.length);

                // Set the answer
                answer = daysOfWeek[dayIndex3];
                TextView weekTextView3 = createTextView(answer.substring(0, 2), 86);
                reorderLayout.addView(weekTextView3);

                // Clear the EditText and set the hint
                answerEditText.setText("");
                answerEditText.setHint("Enter the day of the week");

                // Show the EditText and hide the RadioGroup
                optionButton1.setVisibility(View.GONE);
                optionButton2.setVisibility(View.GONE);
                optionButton3.setVisibility(View.GONE);
                answerEditText.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showMonthQuestion() {
        subQuestionVariant = random.nextInt(2);

        if (subQuestionVariant == 0) {
            questionTextView.setText(R.string.reorder_months);
            answerEditText.setVisibility(View.GONE);

            // Shuffle the array of months
            shuffleArray(months);

            // Add TextViews for each month in the shuffled order
            for (String month : months) {
                TextView monthTextView = createTextView(month, 24);
                reorderLayout.addView(monthTextView);
                setupDragAndDrop(monthTextView);
            }
        } else {
            questionTextView.setText(R.string.complete_month);

            // Choose a random month
            answer = months[random.nextInt(months.length)];

            // Get the first three letters of the random month
            String firstThreeLetters = answer.substring(0, 3);

            // Create a TextView for the first three letters of the month
            TextView monthTextView = createTextView(firstThreeLetters, 86);
            reorderLayout.addView(monthTextView);

            // Show the EditText for the user to enter the complete month
            answerEditText.setVisibility(View.VISIBLE);
            answerEditText.setText("");
        }
    }

    private void showSeasonQuestion() {
        subQuestionVariant = random.nextInt(3);

        switch (subQuestionVariant) {
            case 0:
                int index1 = random.nextInt(3);
                int index2 = random.nextInt(3);
                while (index2 == index1)
                    index2 = random.nextInt(3);

                Seasons season = seasons.get(random.nextInt(4));

                String[] keywords = season.getKeywords();
                answer = season.getName();

                TextView keyTextView1 = createTextView(keywords[index1], 64);
                TextView keyTextView2 = createTextView(keywords[index2], 64);
                reorderLayout.addView(keyTextView1);
                reorderLayout.addView(keyTextView2);

                questionTextView.setText(R.string.seasons_keyword_question);

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

                altButton1.setText(tempSeasons.get(0).getName());
                altButton2.setText(tempSeasons.get(1).getName());
                break;
            case 1:
                Collections.shuffle(seasons);
                Seasons season1 = seasons.get(0);
                Seasons season2 = seasons.get(1);
                String warmOrCold;
                int type = random.nextInt(2);

                answer = season1.getName();
                if (type == 0) {
                    warmOrCold = "warmer";
                    if (season2.getTemperature() > season1.getTemperature())
                        answer = season2.getName();
                }
                else {
                    warmOrCold = "colder";
                    if (season2.getTemperature() < season1.getTemperature())
                        answer = season2.getName();
                }

                String quesString = String.format(getResources().getString(R.string.season_temp_question), warmOrCold);

                TextView seasonTextView1 = createTextView(season1.getName(), 64);
                TextView seasonTextView2 = createTextView(season2.getName(), 64);
                reorderLayout.addView(seasonTextView1);
                reorderLayout.addView(seasonTextView2);

                questionTextView.setText(quesString);

                altButton1.setText(season1.getName());
                altButton2.setText(season2.getName());
                break;
            case 2:
                Collections.shuffle(seasons);
                Seasons seasonTemp1 = seasons.get(0);
                Seasons seasonTemp2 = seasons.get(1);

                if (seasonTemp1.isAdjacent() == seasonTemp2.isAdjacent())
                    answer = "No";
                else answer = "Yes";

                TextView seasonTempTextView1 = createTextView(seasonTemp1.getName(), 64);
                TextView seasonTempTextView2 = createTextView(seasonTemp2.getName(), 64);
                reorderLayout.addView(seasonTempTextView1);
                reorderLayout.addView(seasonTempTextView2);

                questionTextView.setText(R.string.season_adj_question);

                altButton1.setText(R.string.yes);
                altButton2.setText(R.string.no);
                break;
        }
    }

    private void checkAnswer() {
        switch (questionVariant) {
            case 0:
                switch (subQuestionVariant) {
                    case 0:
                    case 1:
                        optionSelected = true;
                        if (selectedButton == 0) {
                            optionSelected = false;
                            Toast.makeText(getActivity(), "Please select one of the options", Toast.LENGTH_SHORT).show();
                        } else if (selectedButton == correctButton)
                            mCorrectAnswersCounter++;
                        else
                            Toast.makeText(getActivity(), "Correct answer: " + answer, Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        optionSelected = true;
                        if (answerEditText.getText().toString().matches("")) {
                            optionSelected = false;
                            Toast.makeText(getActivity(), "Please enter your answer", Toast.LENGTH_SHORT).show();
                        } else if (answerEditText.getText().toString().equalsIgnoreCase(answer))
                            mCorrectAnswersCounter++;
                        else
                            Toast.makeText(getActivity(), "Correct answer: " + answer, Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        optionSelected = false;
                        Toast.makeText(getActivity(), "Unexpected Error", Toast.LENGTH_SHORT).show();
                        break;
                }
                break;
            case 1:
                optionSelected = true;
                if (subQuestionVariant == 0) {
                    int correctMonths = 0;
                    months = getResources().getStringArray(R.array.months);
                    for (int i = 0; i < months.length; i++) {
                        TextView textView = (TextView) reorderLayout.getChildAt(i);
                        if (textView.getText().toString().equals(months[i]))
                            correctMonths++;
                    }
                    mCorrectAnswersCounter += correctMonths;
                    mQuestionCounter += 11;
                    if (correctMonths != 12)
                        Toast.makeText(requireActivity().getApplicationContext(), 12 - correctMonths + " months were in the wrong position", Toast.LENGTH_SHORT).show();
                } else {
                    if (answerEditText.getText().toString().matches("")) {
                        optionSelected = false;
                        Toast.makeText(requireActivity().getApplicationContext(), "Please enter your answer", Toast.LENGTH_SHORT).show();
                    } else if (answerEditText.getText().toString().equalsIgnoreCase(answer))
                        mCorrectAnswersCounter++;
                    else
                        Toast.makeText(requireActivity().getApplicationContext(), "Correct answer was: " + answer, Toast.LENGTH_SHORT).show();
                }
                break;
            case 2:
                Button pressedButton;
                optionSelected = true;
                if (selectedButton == 1){
                    pressedButton = altButton1;
                } else {
                    pressedButton = altButton2;
                }
                if (pressedButton.getText().toString().equals(answer))
                    mCorrectAnswersCounter++;
                else
                    Toast.makeText(requireActivity().getApplicationContext(), "Correct answer was: " + answer, Toast.LENGTH_SHORT).show();
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
            if (mIsFromMix) {
                mQuestionCounter++;
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.time_play_fragment_container, TimePlayClocksFragment.newInstance(mIsFromMix, mQuestionCounter, mCorrectAnswersCounter, timeLeftTemp))
                        .commit();
            } else {
                questionVariant = random.nextInt(3);
                showNextQuestion();
                mQuestionCounter++;
            }
        }
    }

    private TextView createTextView(String text, int textSize) {
        TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setPadding(8, 8, 8, 8);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextSize(textSize);
        textView.setText(text);
        return textView;
    }

    private void setupDragAndDrop(TextView textView) {
        textView.setOnLongClickListener(v -> {
            // Set the dragged view as the local state
            ClipData.Item item = new ClipData.Item("");
            ClipData dragData = new ClipData("", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);
            View.DragShadowBuilder dragShadow = new View.DragShadowBuilder(textView);
            textView.startDragAndDrop(dragData, dragShadow, textView, 0);
            return true;
        });

        textView.setOnDragListener((v, event) -> {
            int action = event.getAction();
            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // Check if the event is a valid drop location
                    return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Set the background color to indicate a potential drop location
                    v.setBackgroundColor(getResources().getColor(R.color.grey_200, requireActivity().getTheme()));
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    // Reset the background color
                    v.setBackgroundColor(getResources().getColor(android.R.color.transparent, requireActivity().getTheme()));
                    return true;
                case DragEvent.ACTION_DROP:
                    // Get the dragged view and its original parent
                    View draggedView = (View) event.getLocalState();
                    ViewGroup draggedParent = (ViewGroup) draggedView.getParent();

                    // Get the target parent and its child count
                    ViewGroup targetParent = (ViewGroup) v.getParent();
                    int targetChildCount = targetParent.getChildCount();

                    // Calculate the index for inserting the dragged view
                    int draggedIndex = 0;
                    for (int i = 0; i < targetChildCount; i++) {
                        View child = targetParent.getChildAt(i);
                        if (child.equals(v)) {
                            draggedIndex = i;
                            break;
                        }
                    }

                    // Remove the dragged view from its original parent
                    draggedParent.removeView(draggedView);

                    // Add the dragged view to the target parent at the calculated index
                    targetParent.addView(draggedView, draggedIndex);

                    // Recursively reapply drag and drop listeners to the target parent's children
                    applyDragAndDropListeners(targetParent);
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    // Reset the background color
                    v.setBackgroundColor(getResources().getColor(android.R.color.transparent, requireActivity().getTheme()));

                    // Set the TextView's visibility to visible after dragging
                    v.setVisibility(View.VISIBLE);
                    return true;
                default:
                    return false;
            }
        });
    }

    private void applyDragAndDropListeners(ViewGroup parent) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (child instanceof TextView) {
                TextView textView = (TextView) child;
                setupDragAndDrop(textView); // Apply drag and drop listeners
            }
        }
    }

    private void shuffleArray(String[] array) {
        List<String> list = Arrays.asList(array);
        Collections.shuffle(list);
        list.toArray(array);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
    }
}