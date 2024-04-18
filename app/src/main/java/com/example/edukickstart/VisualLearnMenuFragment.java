package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VisualLearnMenuFragment extends Fragment {

    public VisualLearnMenuFragment() {
        // Required empty public constructor
    }

    public static VisualLearnMenuFragment newInstance() {
        return new VisualLearnMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_visual_learn_menu, container, false);
        FragmentActivity activity = requireActivity();

        fragmentView.findViewById(R.id.match_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.visual_learn_fragment_container, VisualLearnMatchFragment.newInstance())
                .commit());

        fragmentView.findViewById(R.id.ball_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.visual_learn_fragment_container, VisualLearnBallFragment.newInstance())
                .commit());

        return fragmentView;
    }
}