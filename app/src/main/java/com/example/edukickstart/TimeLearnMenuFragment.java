package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TimeLearnMenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TimeLearnMenuFragment extends Fragment {

    public TimeLearnMenuFragment() {
        // Required empty public constructor
    }

    public static TimeLearnMenuFragment newInstance() {
        return new TimeLearnMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_time_learn_menu, container, false);

        FragmentActivity activity = requireActivity();
        fragmentView.findViewById(R.id.analog_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.time_learn_fragment_container, TimeLearnAnalogFragment.newInstance())
                        .commit());
        fragmentView.findViewById(R.id.digital_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.time_learn_fragment_container, TimeLearnDigitalFragment.newInstance())
                        .commit());
        fragmentView.findViewById(R.id.week_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.time_learn_fragment_container, TimeLearnWeekFragment.newInstance())
                        .commit());
        fragmentView.findViewById(R.id.month_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.time_learn_fragment_container, TimeLearnMonthFragment.newInstance())
                        .commit());
        fragmentView.findViewById(R.id.season_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.time_learn_fragment_container, TimeLearnSeasonFragment.newInstance())
                        .commit());
        return fragmentView;
    }
}