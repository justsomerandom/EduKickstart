package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class MemoryPlayMenuFragment extends Fragment {

    public MemoryPlayMenuFragment() {
        // Required empty public constructor
    }

    public static MemoryPlayMenuFragment newInstance() {
        return new MemoryPlayMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_memory_play_menu, container, false);
        FragmentActivity activity = requireActivity();

        fragmentView.findViewById(R.id.digits_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.memory_play_fragment_container, MemoryPlayDigitsFragment.newInstance(false, 0, 0, 60000))
                .commit());
        fragmentView.findViewById(R.id.image_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.memory_play_fragment_container, MemoryPlayImageFragment.newInstance(false, 0, 0, 60000))
                .commit());
        fragmentView.findViewById(R.id.mix_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.memory_play_fragment_container, MemoryPlayMixFragment.newInstance())
                .commit());

        return fragmentView;
    }
}