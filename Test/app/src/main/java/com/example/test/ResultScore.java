package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import static com.example.test.GameActivity.switch_music;
import static com.example.test.GameActivity.excellent;
import static com.example.test.GameActivity.good;
import static com.example.test.GameActivity.bad;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class ResultScore extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_result);

        ImageView result_image;
        TextView result_title;
        Button home_bt;
        TextView excellent_view;
        TextView good_view;
        TextView bad_view;

        result_image = findViewById(R.id.result_image);
        result_title=findViewById(R.id.result_title);
        home_bt = findViewById(R.id.result_home);
        excellent_view = findViewById(R.id.excellent_score);
        good_view = findViewById(R.id.good_score);
        bad_view = findViewById(R.id.bad_score);


        switch(switch_music){
            case 0:{
                result_image.setImageResource(R.drawable.butterfly);
                result_title.setText("나비야");
                break;
            }
            case 1:{
                result_image.setImageResource(R.drawable.shanz);
                result_title.setText("오 샹제리제");
                break;
            }
            case 2:{
                result_image.setImageResource(R.drawable.summer);
                result_title.setText("Summer");
                break;
            }
            default:
                result_image.setImageResource(R.drawable.summer);
                result_title.setText("선택 곡");
                break;

        }


        excellent_view.setText(Integer.toString(excellent));
        good_view.setText(Integer.toString(good));
        bad_view.setText(Integer.toString(bad));

        home_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                excellent=0;
                good=0;
                bad=0;
                startActivity(new Intent(ResultScore.this,Home.class));
            }
        });

    }
    public void onBackPressed() {
        excellent=0;
        good=0;
        bad=0;
        startActivity(new Intent(ResultScore.this,Home.class));

    }
}
