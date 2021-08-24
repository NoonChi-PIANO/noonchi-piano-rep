package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

import jp.kshoji.javax.sound.midi.UsbMidiSystem;

public class Home extends AppCompatActivity {
    ViewpagerAdapter adapter;
    ViewPager2 viewPager;
    ImageButton setting;
    TabLayout tabLayout;

    UsbMidiSystem usbMidiSystem;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        usbMidiSystem = new UsbMidiSystem(this);
        usbMidiSystem.initialize();

        setting=(ImageButton)findViewById(R.id.setting);
        viewPager = findViewById(R.id.viewpager);
        tabLayout=findViewById(R.id.tablayout);

        //Toast.makeText(Home.this, SaveSharedPreference.getUserName(Home.this) + "(으)로 로그인 되었습니다", Toast.LENGTH_SHORT).show();

        adapter = new ViewpagerAdapter(this);
        viewPager.setAdapter(adapter);

        //TabLayout 타이틀
        final List<String> tabElement=Arrays.asList("연습하기","점수보기","악보올리기","사용방법");
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                TextView textView=new TextView(Home.this);
                textView.setText(tabElement.get(position));
                tab.setCustomView(textView);
            }
        }).attach();


        final String[] array={"피아노 크기설정","정보수정"}; //setting버튼 클릭 시 다이얼로그 목록
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(Home.this);
                dialog.setTitle("목록을 선택해주세요");
                dialog.setIcon(R.drawable.ic_baseline_settings_24);
                dialog.setItems(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i==0){ //피아노 크기설정
                            Intent intent=new Intent(Home.this,PianoSetting.class);
                            startActivity(intent);
                        }
                        else if(i==1){ //정보수정정
                           Intent intent=new Intent(Home.this,Setting.class);
                            startActivity(intent);
                        }
                    }
                });dialog.show();
            }
        });



    }
}