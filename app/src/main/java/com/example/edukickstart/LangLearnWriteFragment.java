package com.example.edukickstart;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Random;

public class LangLearnWriteFragment extends Fragment {

    private FrameLayout drawingLayout;
    private DrawingView drawingView;
    private ImageView backgroundImage;
    private TextView questionTextView;
    private int questionCounter = 0;
    private final double[] accuracies = new double[6];
    private double currentAcc;
    private char prevChar = '0';
    private boolean calcComplete = false;

    private Context appContext;
    private Handler mainHandler;
    private static final Object LOCK = new Object();

    private static final int DATABASE_INDEX = 9;
    private UserDao userDao;

    public LangLearnWriteFragment() {
        // Required empty public constructor
    }

    public static LangLearnWriteFragment newInstance() {
        return new LangLearnWriteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lang_learn_write, container, false);

        drawingLayout = view.findViewById(R.id.drawing_layout);
        questionTextView = view.findViewById(R.id.question_text_view);
        Button nextButton = view.findViewById(R.id.next_button);
        drawingView = drawingLayout.findViewById(R.id.drawing_view);
        UserDatabase database = UserDatabase.getInstance(requireActivity());
        userDao = database.userDao();

        appContext = requireActivity().getApplicationContext();
        mainHandler = new Handler(Looper.getMainLooper());

        nextButton.setOnClickListener(v -> {
            checkAnswer();
            if (questionCounter < 5) {
                nextQuestion();
                questionCounter++;
            } else {
                AppExecutor.getInstance().execute(() -> {
                    synchronized (LOCK) {
                        while (!calcComplete) {
                            try {
                                LOCK.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        double overallAccuracy = 0;
                        for (double value : accuracies)
                            overallAccuracy += value;
                        overallAccuracy /= 6;
                        User user = userDao.findLoggedInUser();
                        ArrayList<String> progress = user.getProgress();
                        int newWeightedAccuracy;
                        if (progress.get(DATABASE_INDEX).equals("0"))
                            newWeightedAccuracy = (int) overallAccuracy;
                        else
                            newWeightedAccuracy = (Integer.parseInt(progress.get(DATABASE_INDEX)) + (int) overallAccuracy) / 2;
                        progress.set(DATABASE_INDEX, String.valueOf(newWeightedAccuracy));
                        user.setProgress(progress);
                        userDao.updateUser(user);

                        double finalOverallAccuracy = overallAccuracy;
                        mainHandler.post(() -> Toast.makeText(appContext, "Average accuracy: " + (int) finalOverallAccuracy + "%", Toast.LENGTH_SHORT).show());
                    }
                });
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.lang_learn_fragment_container, LangLearnMenuFragment.newInstance())
                        .commit();

            }
        });

        nextQuestion();
        return view;
    }

    private void nextQuestion() {
        char currentLetter;
        do currentLetter = generateRandomLetter();
        while (currentLetter == prevChar);
        prevChar = currentLetter;

        String formattedString = String.format(getString(R.string.write_question), currentLetter);
        questionTextView.setText(formattedString);

        backgroundImage = drawingLayout.findViewById(R.id.background_image);
        backgroundImage.setImageResource(charToDrawable(currentLetter));

        drawingView.clearDrawing();
        drawingView.setDrawingCacheEnabled(true);
    }

    private void checkAnswer() {
        Toast.makeText(requireActivity(), "Calculating...", Toast.LENGTH_SHORT).show();
        // Get the drawn letter bitmap from the drawing layout
        Bitmap drawnLetterBitmap = Bitmap.createBitmap(drawingView.getDrawingCache());
        drawingView.destroyDrawingCache();
        drawingView.setDrawingCacheEnabled(false);

        // Get the outline letter bitmap for the current letter
        Bitmap outlineLetterBitmap = Bitmap.createBitmap(backgroundImage.getWidth(), backgroundImage.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outlineLetterBitmap);
        backgroundImage.draw(canvas);

        int tempQuesCounter = questionCounter;
        AppExecutor.getInstance().execute(() -> {
            try {
                assert drawnLetterBitmap.getWidth() == outlineLetterBitmap.getWidth();
                assert drawnLetterBitmap.getHeight() == outlineLetterBitmap.getHeight();

                currentAcc = calculateSimilarityPercentage(drawnLetterBitmap, outlineLetterBitmap);
                mainHandler.post(() -> Toast.makeText(appContext, "Accuracy of previous: " + (int) currentAcc + "%", Toast.LENGTH_SHORT).show());
                accuracies[tempQuesCounter] = currentAcc;
                synchronized (LOCK) {
                    if (tempQuesCounter == 5)
                        calcComplete = true;
                    LOCK.notify();
                }
            } catch (AssertionError e) {
                mainHandler.post(() -> Toast.makeText(appContext, "Unexpected Error", Toast.LENGTH_SHORT).show());
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.lang_learn_fragment_container, LangLearnMenuFragment.newInstance())
                        .commit();
            }
        });
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
}