package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MemoryLearnMenuFragment extends Fragment {

    public MemoryLearnMenuFragment() {
        // Required empty public constructor
    }

    public static MemoryLearnMenuFragment newInstance() {
        return new MemoryLearnMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_memory_learn_menu, container, false);
        FragmentActivity activity = requireActivity();

        fragmentView.findViewById(R.id.forward_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.memory_learn_fragment_container, MemoryLearnForwardFragment.newInstance())
                        .commit());
        fragmentView.findViewById(R.id.backward_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.memory_learn_fragment_container, MemoryLearnBackwardFragment.newInstance())
                        .commit());
        fragmentView.findViewById(R.id.images_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.memory_learn_fragment_container, MemoryLearnImageFragment.newInstance())
                        .commit());

        return fragmentView;
    }
}