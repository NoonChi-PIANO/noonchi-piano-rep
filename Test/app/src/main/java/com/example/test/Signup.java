package com.example.test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Signup extends AppCompatActivity {

    String s_pw,s_pwc, s_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText signID = (EditText)findViewById(R.id.signid);
        EditText signPW = (EditText)findViewById(R.id.signpw);
        EditText signPWC = (EditText)findViewById(R.id.signpwCheck);


        Button signok=(Button) findViewById(R.id.signok);
        signok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                s_id = signID.getText().toString();
                s_pw = signPW.getText().toString();
                s_pwc = signPWC.getText().toString();

                String serverUrl="http://27.96.131.137/noonchi/sign/register.php";


                SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //new AlertDialog.Builder(MainActivity.this).setMessage("응답:"+response).create().show();

                        //  Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if(success) {
                                Toast.makeText(Signup.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(Signup.this,Home.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(Signup.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
                    }
                });

                smpr.addStringParam("u_id", s_id);
                smpr.addStringParam("u_password", s_pw);
                smpr.addStringParam("u_name", "default");
                smpr.addStringParam("u_birth", "default");
                smpr.addStringParam("u_gender", "default");
                RequestQueue requestQueue= Volley.newRequestQueue(Signup.this);
                requestQueue.add(smpr);


                Intent intent=new Intent(Signup.this,MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"회원가입성공",Toast.LENGTH_LONG).show();
            }
        });
    }
}