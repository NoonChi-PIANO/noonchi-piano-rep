package com.example.test;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fquestion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fquestion extends Fragment {

    public Fquestion() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static Fquestion newInstance() {
        Fquestion fragment = new Fquestion();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fquestion, container, false);
    }
}