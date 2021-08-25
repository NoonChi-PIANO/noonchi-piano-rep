package com.example.test;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Fscore extends Fragment {

    public Fscore() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static Fscore newInstance() {
        Fscore fragment = new Fscore();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for getActivity() fragment
        return inflater.inflate(R.layout.fragment_fscore, container, false);
    }
}