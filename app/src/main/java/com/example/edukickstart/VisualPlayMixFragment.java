package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisualPlayMixFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisualPlayMixFragment extends Fragment {

    public VisualPlayMixFragment() {
        // Required empty public constructor
    }

    public static VisualPlayMixFragment newInstance() {
        return new VisualPlayMixFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_visual_play_mix, container, false);

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

                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.visual_play_fragment_container, VisualPlayMatchFragment.newInstance(true, 0, 0, 60000))
                        .commit();
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

        return view;
    }
}