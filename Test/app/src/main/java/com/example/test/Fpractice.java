package com.example.test;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fpractice#newInstance} factory method to
 * create an instance of this fragment.
 */

public class Fpractice extends Fragment {
    public static boolean music_selected = false;
    LinearLayout testButton;
   public Fpractice() {
        // Required empty public constructor
   }


    // TODO: Rename and change types and number of parameters
    public static Fpractice newInstance() {
        Fpractice fragment = new Fpractice();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);


        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_fpractice, container, false);
        testButton = (LinearLayout)rootView.findViewById(R.id.testbutton);

        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(getActivity(),GameActivity.class);
                myIntent.putExtra("select",0);
                getActivity().startActivity(myIntent);
            }

        });

        // Inflate the layout for this fragment
        //return view;
        return rootView;

    }
}