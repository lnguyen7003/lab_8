package com.example.lab7application;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class DetailsFragment extends Fragment {
    private static final String ARG_CHARACTER = "character";

    private Character character;

    public DetailsFragment() {
        // Required empty public constructor
    }

    public static DetailsFragment newInstance(Character character) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CHARACTER, (Parcelable) character);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            character = getArguments().getParcelable(ARG_CHARACTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        // Find the TextViews by their IDs
        TextView heightTextView = view.findViewById(R.id.heightTextView);
        TextView massTextView = view.findViewById(R.id.massTextView);

        // Set the character's height and mass in the TextViews
        if (character != null) {
            heightTextView.setText(character.getHeight());
            massTextView.setText(character.getMass());
        }

        return view;
    }
}
