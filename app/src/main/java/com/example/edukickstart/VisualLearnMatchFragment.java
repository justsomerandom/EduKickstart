package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VisualLearnMatchFragment extends Fragment {

    private final ImageButton[] imageButtons = new ImageButton[18];
    private final double[] accuracies = new double[3];
    private boolean buttonPressed;
    private int questionCounter = 0, buttonPressCounter = 0, correctCounter = 0, pressedButton;

    private final int[] drawablesQues = new int[9];
    private final int[] drawableRes = new int[18];

    private static final int DATABASE_INDEX = 14;
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
    public VisualLearnMatchFragment() {
        // Required empty public constructor
    }

    public static VisualLearnMatchFragment newInstance() {
        return new VisualLearnMatchFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_visual_learn_match, container, false);

        imageButtons[0] = view.findViewById(R.id.image_button_one);
        imageButtons[1] = view.findViewById(R.id.image_button_two);
        imageButtons[2] = view.findViewById(R.id.image_button_three);
        imageButtons[3] = view.findViewById(R.id.image_button_four);
        imageButtons[4] = view.findViewById(R.id.image_button_five);
        imageButtons[5] = view.findViewById(R.id.image_button_six);
        imageButtons[6] = view.findViewById(R.id.image_button_seven);
        imageButtons[7] = view.findViewById(R.id.image_button_eight);
        imageButtons[8] = view.findViewById(R.id.image_button_nine);
        imageButtons[9] = view.findViewById(R.id.image_button_ten);
        imageButtons[10] = view.findViewById(R.id.image_button_eleven);
        imageButtons[11] = view.findViewById(R.id.image_button_twelve);
        imageButtons[12] = view.findViewById(R.id.image_button_thirteen);
        imageButtons[13] = view.findViewById(R.id.image_button_fourteen);
        imageButtons[14] = view.findViewById(R.id.image_button_fifteen);
        imageButtons[15] = view.findViewById(R.id.image_button_sixteen);
        imageButtons[16] = view.findViewById(R.id.image_button_seventeen);
        imageButtons[17] = view.findViewById(R.id.image_button_eighteen);

        for (int i = 0; i < 18; i++) {
            int temp = i;
            imageButtons[temp].setOnClickListener(v -> {
                imageButtons[temp].setBackgroundColor(getResources().getColor(R.color.grey_700, requireActivity().getTheme()));
                if (buttonPressed) {
                    if (pressedButton == temp) {
                        deselectButton(imageButtons[temp]);
                    } else {
                        checkMatch(imageButtons[pressedButton], imageButtons[temp]);
                        if (checkFinished()) {
                            if (questionCounter < 2) {
                                nextQuestion();
                                questionCounter++;
                            } else {
                                UserDatabase database = UserDatabase.getInstance(requireActivity());
                                UserDao userDao = database.userDao();
                                double overallAccuracy = 0;
                                for (double value : accuracies)
                                    overallAccuracy += value;
                                overallAccuracy /= 3;
                                int overallInt = (int) overallAccuracy;
                                AppExecutor.getInstance().execute(() -> {
                                    User user = userDao.findLoggedInUser();
                                    ArrayList<String> progress = user.getProgress();
                                    int newWeightedAccuracy;
                                    if (progress.get(DATABASE_INDEX).equals("0"))
                                        newWeightedAccuracy = overallInt;
                                    else
                                        newWeightedAccuracy = (Integer.parseInt(progress.get(DATABASE_INDEX)) + overallInt) / 2;
                                    progress.set(DATABASE_INDEX, String.valueOf(newWeightedAccuracy));
                                    user.setProgress(progress);
                                    userDao.updateUser(user);
                                });
                                // Display the final score or navigate to the menu fragment
                                Toast.makeText(getActivity(), "Average accuracy: " + overallInt, Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.visual_learn_fragment_container, VisualLearnMenuFragment.newInstance())
                                        .commit();
                            }
                        }
                    }
                } else {
                    pressedButton = temp;
                    buttonPressed = true;
                    buttonPressCounter++;
                }
            });
        }

        nextQuestion();
        return view;
    }

    private void nextQuestion() {
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
            correctCounter++;
        } else {
            button1.setBackgroundColor(getResources().getColor(R.color.white, requireActivity().getTheme()));
            button2.setBackgroundColor(getResources().getColor(R.color.white, requireActivity().getTheme()));
            Toast.makeText(requireActivity(), "These images do not match", Toast.LENGTH_SHORT).show();
        }
        buttonPressed = false;
    }

    private boolean checkFinished() {
        int invisibleButtons = 0;
        for (ImageButton button : imageButtons) {
            if (!button.isEnabled())
                invisibleButtons++;
        }

        if (invisibleButtons == imageButtons.length) {
            double tempAcc = correctCounter;
            tempAcc /= buttonPressCounter;
            accuracies[questionCounter] = tempAcc * 100;
            return true;
        }

        return false;
    }

    private void deselectButton(ImageButton button) {
        button.setBackgroundColor(getResources().getColor(R.color.white, requireActivity().getTheme()));
        buttonPressed = false;
        buttonPressCounter--;
    }

    private void setDrawableRes(int index, int value) {
        drawableRes[index] = value;
    }

    private int getDrawableRes(ImageButton button) {
        List<ImageButton> tmp = Arrays.asList(Arrays.copyOf(imageButtons, imageButtons.length));
        return drawableRes[tmp.indexOf(button)];
    }
}