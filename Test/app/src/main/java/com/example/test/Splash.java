package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
//그냥 스플래시 화면 (로딩)
public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(2000); //2초간 splash화면을 띄운 후, 메인액티비티 실행
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

}
