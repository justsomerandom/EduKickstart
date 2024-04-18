package com.example.edukickstart;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TimeLearnMonthFragment extends Fragment {

    private TextView questionTextView;
    private LinearLayout reorderLayout;
    private EditText monthEditText;

    private String[] months;
    private String correctMonth;
    private boolean isReorderQuestion;
    private int questionCounter = 0;
    private int correctAnswers = 0;
    private boolean questionAnswered;
    private static final int DATABASE_INDEX = 3;

    public TimeLearnMonthFragment() {
        // Required empty public constructor
    }

    public static TimeLearnMonthFragment newInstance() {
        return new TimeLearnMonthFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_learn_month, container, false);

        questionTextView = view.findViewById(R.id.question_text_view);
        reorderLayout = view.findViewById(R.id.reorder_layout);
        monthEditText = view.findViewById(R.id.month_edit_text);
        Button nextButton = view.findViewById(R.id.next_button);

        nextButton.setOnClickListener(v -> nextFunc());
        monthEditText.setOnEditorActionListener((textView, i, keyEvent) -> {
            nextFunc();
            return true;
        });

        // Initialize the array of months
        months = getResources().getStringArray(R.array.months);
        isReorderQuestion = false;

        // Show the initial question
        showNextQuestion();

        return view;
    }

    private void showNextQuestion() {
        if (isReorderQuestion) {
            showReorderQuestion();
        } else {
            showCompleteMonthQuestion();
        }
    }

    private void showReorderQuestion() {
        questionTextView.setText(R.string.reorder_months);
        monthEditText.setVisibility(View.GONE);

        // Shuffle the array of months
        shuffleArray(months);

        // Clear the existing TextViews in the reorderLayout
        reorderLayout.removeAllViews();

        // Add TextViews for each month in the shuffled order
        for (String month : months) {
            TextView monthTextView = createMonthTextView(month, 24);
            reorderLayout.addView(monthTextView);
            setupDragAndDrop(monthTextView);
        }
    }


    private void showCompleteMonthQuestion() {
        questionTextView.setText(R.string.complete_month);

        // Clear the existing TextViews in the reorderLayout
        reorderLayout.removeAllViews();

        // Choose a random month
        Random random = new Random();
        correctMonth = months[random.nextInt(months.length)];

        // Get the first three letters of the random month
        String firstThreeLetters = correctMonth.substring(0, 3);

        // Create a TextView for the first three letters of the month
        TextView monthTextView = createMonthTextView(firstThreeLetters, 86);
        reorderLayout.addView(monthTextView);

        // Show the EditText for the user to enter the complete month
        monthEditText.setVisibility(View.VISIBLE);
        monthEditText.setText("");
    }

    private void checkAnswer() {
        questionAnswered = true;
        if (isReorderQuestion) {
            int correctMonths = 0;
            months = getResources().getStringArray(R.array.months);
            for (int i = 0; i < months.length; i++) {
                TextView textView = (TextView) reorderLayout.getChildAt(i);
                if (textView.getText().toString().equals(months[i]))
                    correctMonths++;
            }
            if (correctMonths == 12)
                correctAnswers++;
            else
                Toast.makeText(requireActivity().getApplicationContext(), 12 - correctMonths + " months were in the wrong position", Toast.LENGTH_SHORT).show();
        } else {
            if (monthEditText.getText().toString().matches("")) {
                questionAnswered = false;
                Toast.makeText(requireActivity().getApplicationContext(), "Please enter your answer", Toast.LENGTH_SHORT).show();
            } else if (monthEditText.getText().toString().equalsIgnoreCase(correctMonth))
                correctAnswers++;
            else
                Toast.makeText(requireActivity().getApplicationContext(), "Correct answer was: " + correctMonth, Toast.LENGTH_SHORT).show();
        }
    }

    private void nextFunc() {
        checkAnswer();
        if (questionAnswered) {
            View focusView = requireActivity().getCurrentFocus();
            if (focusView != null) {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
            if (questionCounter < 4) {
                isReorderQuestion = !isReorderQuestion;
                questionCounter++;
                showNextQuestion();
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
                // Replace the fragment with TimeLearnMenuFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.time_learn_fragment_container, TimeLearnMenuFragment.newInstance())
                        .commit();
            }
        }
    }

    private TextView createMonthTextView(String text, int textSize) {
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
}
