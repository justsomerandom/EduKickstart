package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LangPlayMenuFragment extends Fragment {

    public LangPlayMenuFragment() {
        // Required empty public constructor
    }

    public static LangPlayMenuFragment newInstance() {
        return new LangPlayMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_lang_play_menu, container, false);
        FragmentActivity activity = requireActivity();

        fragmentView.findViewById(R.id.words_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.lang_play_fragment_container, LangPlayWordsFragment.newInstance(false, 0, 0, 60000))
                .commit());
        fragmentView.findViewById(R.id.direct_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.lang_play_fragment_container, LangPlayDirectFragment.newInstance(false, 0, 0, 60000))
                .commit());
        fragmentView.findViewById(R.id.mix_button).setOnClickListener(view -> activity
                .getSupportFragmentManager().beginTransaction()
                .replace(R.id.lang_play_fragment_container, LangPlayMixFragment.newInstance())
                .commit());

        return fragmentView;
    }
}