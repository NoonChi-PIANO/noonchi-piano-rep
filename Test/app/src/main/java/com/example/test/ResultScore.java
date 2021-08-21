package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import static com.example.test.GameActivity.excellent;
import static com.example.test.GameActivity.good;
import static com.example.test.GameActivity.bad;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ResultScore extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_result);

        TextView excellent_view;
        TextView good_view;
        TextView bad_view;

        excellent_view = findViewById(R.id.excellent_score);
        good_view = findViewById(R.id.good_score);
        bad_view = findViewById(R.id.bad_score);


        Intent myIntent = getIntent();

        excellent_view.setText(Integer.toString(excellent));
        good_view.setText(Integer.toString(good));
        bad_view.setText(Integer.toString(bad));

    }
}
