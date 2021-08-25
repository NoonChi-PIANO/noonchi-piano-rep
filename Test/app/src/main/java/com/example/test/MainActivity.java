package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import jp.kshoji.javax.sound.midi.UsbMidiSystem;

//로그인화면, 첫화면임
public class MainActivity extends AppCompatActivity {

   String u_password, u_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        UsbMidiSystem usbMidiSystem;

        usbMidiSystem = new UsbMidiSystem(this);
        usbMidiSystem.initialize();

        if(SaveSharedPreference.getUserName(MainActivity.this).length() == 0)
        {
            // call Login Activity
            Button login=(Button) findViewById(R.id.login);
            Button signup=(Button) findViewById(R.id.signup);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                /*TextView textView = (TextView)findViewById(R.id.textView);
                EditText editText = (EditText)findViewById(R.id.editText);

                textView.setText(editText.getText());*/

                    EditText password = (EditText)findViewById(R.id.upassT);
                    EditText id = (EditText)findViewById(R.id.uidT);
                    u_id = id.getText().toString();
                    u_password = password.getText().toString();

                    String serverUrl="http://27.96.131.137/noonchi/sign/login.php";

                    SimpleMultiPartRequest smpr= new SimpleMultiPartRequest(Request.Method.POST, serverUrl, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //new AlertDialog.Builder(MainActivity.this).setMessage("응답:"+response).create().show();

                            //  Toast.makeText(MainActivity.this, response, Toast.LENGTH_SHORT).show();

                            try{
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");
                                if(success) {
                                    SaveSharedPreference.setUserName(MainActivity.this, id.getText().toString());
                                    Toast.makeText(MainActivity.this, "로그인성공", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(MainActivity.this,Home.class);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(MainActivity.this, "로그인실패", Toast.LENGTH_SHORT).show();
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

                    smpr.addStringParam("u_id", u_id);
                    smpr.addStringParam("u_password", u_password);
                    RequestQueue requestQueue= Volley.newRequestQueue(MainActivity.this);
                    requestQueue.add(smpr);



                    //로그인에 성공하면 Intent로 Home으로 이동함
                    // Intent intent=new Intent(MainActivity.this,Home.class);
                    // startActivity(intent);
                }
            });
            signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(MainActivity.this,Home.class);
                    startActivity(intent);

                    //일단 임시로 signup에 바로 저장하는거 넣음
                    //Intent intent2=new Intent(MainActivity.this,Signup.class);
                    //startActivity(intent2);
                }
            });

        }
        else
        {
            // Call Next Activity
            Intent intent=new Intent(MainActivity.this,Home.class);
            startActivity(intent);
        }

    }//onCreate

}