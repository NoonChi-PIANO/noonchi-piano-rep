package com.example.test;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Fscore extends Fragment {

    public Fscore() {
        // Required empty public constructor
    }
    Button refesh;

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
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_fscore, container, false);
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_fmusic, container, false);
        refesh = (Button)rootView.findViewById(R.id.uploadimg);



        refesh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //새로고침 누를시 데이터베이스에서 새로 받아옴. 이걸 처리해줘야함
            }

        });
        return rootView;
    }
}