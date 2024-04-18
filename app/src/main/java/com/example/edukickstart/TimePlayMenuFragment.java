package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimePlayMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimePlayMenuFragment extends Fragment {

    public TimePlayMenuFragment() {
        // Required empty public constructor
    }

    public static TimePlayMenuFragment newInstance() {
        return new TimePlayMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_time_play_menu, container, false);
        FragmentActivity activity = requireActivity();

        fragmentView.findViewById(R.id.clocks_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.time_play_fragment_container, TimePlayClocksFragment.newInstance(false, 0, 0, 60000))
                .commit());
        fragmentView.findViewById(R.id.calendar_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.time_play_fragment_container, TimePlayCalendarFragment.newInstance(false, 0, 0, 60000))
                .commit());
        fragmentView.findViewById(R.id.mix_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.time_play_fragment_container, TimePlayMixFragment.newInstance())
                .commit());

        return fragmentView;
    }
}