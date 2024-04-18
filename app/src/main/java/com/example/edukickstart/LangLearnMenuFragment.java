package com.example.edukickstart;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LangLearnMenuFragment extends Fragment {

    public LangLearnMenuFragment() {
        // Required empty public constructor
    }

    public static LangLearnMenuFragment newInstance() {
        return new LangLearnMenuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_lang_learn_menu, container, false);
        FragmentActivity activity = requireActivity();

        fragmentView.findViewById(R.id.spell_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.lang_learn_fragment_container, LangLearnSpellFragment.newInstance())
                        .commit());
        fragmentView.findViewById(R.id.write_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.lang_learn_fragment_container, LangLearnWriteFragment.newInstance())
                        .commit());
        fragmentView.findViewById(R.id.direct_button)
                .setOnClickListener(view -> activity
                        .getSupportFragmentManager().beginTransaction()
                        .replace(R.id.lang_learn_fragment_container, LangLearnDirectFragment.newInstance())
                        .commit());

        return fragmentView;
    }
}