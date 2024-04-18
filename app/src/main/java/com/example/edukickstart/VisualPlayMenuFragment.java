package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VisualPlayMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VisualPlayMenuFragment extends Fragment {

    public VisualPlayMenuFragment() {
        // Required empty public constructor
    }

    public static VisualPlayMenuFragment newInstance() {
        return new VisualPlayMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_visual_play_menu, container, false);
        FragmentActivity activity = requireActivity();

        fragmentView.findViewById(R.id.match_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.visual_play_fragment_container, VisualPlayMatchFragment.newInstance(false, 0, 0, 60000))
                .commit());
        fragmentView.findViewById(R.id.ball_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.visual_play_fragment_container, VisualPlayBallFragment.newInstance(false, 0, 0, 60000))
                .commit());
        fragmentView.findViewById(R.id.mix_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.visual_play_fragment_container, VisualPlayMixFragment.newInstance())
                .commit());

        return fragmentView;
    }
}