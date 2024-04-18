package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MathLearnMenuFragment extends Fragment {

    public MathLearnMenuFragment() {
        // Required empty public constructor
    }

    public static MathLearnMenuFragment newInstance() {
        return new MathLearnMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_math_learn_menu, container, false);
        FragmentActivity activity = requireActivity();

        fragmentView.findViewById(R.id.add_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.math_learn_fragment_container, MathLearnAddFragment.newInstance())
                        .commit());
        fragmentView.findViewById(R.id.sub_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.math_learn_fragment_container, MathLearnSubFragment.newInstance())
                        .commit());
        fragmentView.findViewById(R.id.multiply_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.math_learn_fragment_container, MathLearnMultiplyFragment.newInstance())
                        .commit());

        return fragmentView;
    }
}