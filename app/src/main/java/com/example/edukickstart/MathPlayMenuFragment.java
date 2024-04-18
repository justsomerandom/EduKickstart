package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MathPlayMenuFragment extends Fragment {

    public MathPlayMenuFragment() {
        // Required empty public constructor
    }

    public static MathPlayMenuFragment newInstance() {
        return new MathPlayMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_math_play_menu, container, false);
        FragmentActivity activity = requireActivity();

        view.findViewById(R.id.add_button).setOnClickListener(v -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.math_play_fragment_container, MathPlayAddFragment.newInstance(false, 0, 0, 60000))
                .commit());
        view.findViewById(R.id.sub_button).setOnClickListener(v -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.math_play_fragment_container, MathPlaySubFragment.newInstance(false, 0, 0, 60000))
                .commit());
        view.findViewById(R.id.multiply_button).setOnClickListener(v -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.math_play_fragment_container, MathPlayMultiplyFragment.newInstance(false, 0, 0, 60000))
                .commit());
        view.findViewById(R.id.mix_button).setOnClickListener(v -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.math_play_fragment_container, MathPlayMixFragment.newInstance())
                .commit());

        return view;
    }
}