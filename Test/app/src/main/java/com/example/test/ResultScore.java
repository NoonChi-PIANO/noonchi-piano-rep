package com.example.test;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.test.GameActivity.switch_music;
import static com.example.test.GameActivity.excellent;
import static com.example.test.GameActivity.good;
import static com.example.test.GameActivity.bad;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultScore extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_result);

        //SharedPrefManager.getInstance(this).getUsername()
        //여기다가 디비 추가문을 넣을거에용!
        String serverUrl="http://27.96.131.137/noonchi/sign/score.php";
        //String userIDforScore = SaveSharedPreference.getUserName(ResultScore.this);
        //String userIDforScore = SharedPrefManager.getInstance(this).getUsername();


        SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //new AlertDialog.Builder(ResultScore.this).setMessage("응답:"+response).create().show();

                //  Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();

                try{
                    JSONObject jsonObject = new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success) {
                        //일반 리스폰스
                    }else{
                        //에러발생시
                        Toast.makeText(ResultScore.this, "DB ERROR2", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ResultScore.this, "DB ERROR", Toast.LENGTH_SHORT).show();
            }
        });

        smpr.addStringParam("id",  SaveSharedPreference.getUserName(ResultScore.this));
        smpr.addStringParam("bad",  Integer.toString(bad));
        smpr.addStringParam("good",  Integer.toString(good));
        smpr.addStringParam("excellent",  Integer.toString(excellent));
        RequestQueue requestQueue= Volley.newRequestQueue(ResultScore.this);
        requestQueue.add(smpr);
        //디비 추가 끝!

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
