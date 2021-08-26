package com.example.test;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import jp.kshoji.javax.sound.midi.UsbMidiSystem;

import static com.example.test.Fpractice.music_selected;
import static com.example.test.DrawGunban.y_piano_upleft;

import static com.example.test.Panel.balls_right;
import static com.example.test.Panel.balls_left;

import static com.example.test.Panel.repeat_number;
import static com.example.test.Ball.velocity;




public class GameActivity extends AppCompatActivity{

    public static Panel panel;
    public static ArrayList<ArrayList> answer_right;
    public static ArrayList<ArrayList> answer_left;
    public static int sum_right_count_while; //오른손 for문 도는 개수
    public static int sum_left_count_while; // 왼손 for문 도는 개수
    public static TextView Right_scale;
    public static TextView Left_scale;
    public static TextView bad_scale;
    public static TextView answer_scale;
    public static int total_length;//곡 전체 길이(프로토콜 개수)

    public static int switch_music;

    public static int excellent;
    public static int good;
    public static int bad;


    public int music;
    public static TextView is_Correct;
    private ToggleButton play_or_stop;
    private Button repeat_start;
    private Button repeat_end;
    private Button repeat;
    private int blockSize;
    private Canvas background;
    private Paint green_line;
    private ImageView Test;
    private Button result_score;

    public static Context context_game;

    public Button StartStopBTN;
    public static TextView t2;
    private ScaleDetector2 sc2 ;
    private ScaleDetector2.RecordAudio recordTask;

    private midiRecord md2;
    private midiRecord.RecordAudio recordTask2;

    UsbMidiSystem usbMidiSystem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(GameActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
       // usbMidiSystem = new UsbMidiSystem(this);
        //usbMidiSystem.initialize();

        md2 = new midiRecord(this);
        //StartStopBTN.setText("Stop");
        md2.started=true;
        recordTask2 = md2.new RecordAudio();
        recordTask2.execute();

        final int[] selectedItem={0};
        context_game = this;
        switch_music=9;//default
        excellent=0;
        good=0;
        bad=0;
        result_score=findViewById(R.id.score_result);
        repeat=findViewById(R.id.repeat);
        repeat_end=findViewById(R.id.repeat_end);
        repeat_start=findViewById(R.id.repeat_start);
        Right_scale = findViewById(R.id.good_scale);
        Left_scale = findViewById(R.id.excellent_scale);
        bad_scale = findViewById(R.id.bad_scale);
        answer_scale=findViewById(R.id.answer_scale);
        is_Correct = findViewById((R.id.isCorrect));
        Intent myintent = getIntent();
        int selectImg= myintent.getIntExtra("select",0);
        music = myintent.getIntExtra("music",0);
        panel = new Panel(this);

        if(selectImg==0){
            insertProtocol();
        }
        else if(selectImg==1){
            insertImgProtocol();
        }


        panel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0,1));
        ViewGroup root =(ViewGroup)findViewById(R.id.game);

        root.addView(panel,2);

        blockSize =256;
        y_piano_upleft = y_piano_upleft - 218; // 160pixel = 40d
        play_or_stop = (ToggleButton)root.findViewById(R.id.btn_play_or_stop);
        play_or_stop.setChecked(true);


        Bitmap bitmap = Bitmap.createBitmap(256, 100, Bitmap.Config.ARGB_8888);
        background = new Canvas(bitmap);
        green_line = new Paint();

        green_line.setColor(Color.GREEN);
        Test = (ImageView)findViewById(R.id.test);
        Test.setImageBitmap(bitmap);

      //  StartStopBTN = (Button)findViewById(R.id.StartStopButton);



        t2 = (TextView)findViewById(R.id.HzText2);
        t2.setText("...");

        result_score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    //panel.thread.setStop(true);
                    panel.balls_right.clear();
                    panel.balls_left.clear();
                    panel.repeat_right.clear();
                    panel.repeat_left.clear();
                    panel.repeat_right_sum.clear();
                    panel.repeat_left_sum.clear();
                    panel.repeat_number=0;

                    //StartStopBTN.setText("Start");
                    md2.started=false;
                    recordTask2.cancel(true);

                    Toast.makeText(GameActivity.this, "연습을 종료합니다!", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(GameActivity.this,ResultScore.class);
                    startActivity(myIntent);


                    y_piano_upleft = y_piano_upleft + 218; // 160pixel = 40dp
                }catch (NullPointerException e){

                    //StartStopBTN.setText("Start");
                    md2.started=false;
                    recordTask2.cancel(true);

                    Toast.makeText(GameActivity.this, "연습을 종료합니다!", Toast.LENGTH_SHORT).show();
                    Intent myIntent = new Intent(GameActivity.this,ResultScore.class);
                    myIntent.putExtra("excellent",excellent);
                    myIntent.putExtra("good",good);
                    myIntent.putExtra("bad",bad);
                    startActivity(myIntent);

                    y_piano_upleft = y_piano_upleft + 218; // 160pixel = 40dp
                }
            }
        });
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel.thread.setStart(true);
            }
        });
        repeat_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel.thread.setStart_point(true);
            }
        });
        repeat_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                panel.thread.setStop(true);
                final String[] versionArray = new String[] {"1","2","3","4","5"};
                AlertDialog.Builder dlg = new AlertDialog.Builder(GameActivity.this);
                dlg.setTitle("반복 횟수 설정").setSingleChoiceItems(versionArray, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                selectedItem[0]=which;
                            }
                        }


                ).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(GameActivity.this,versionArray[selectedItem[0]],Toast.LENGTH_SHORT);
                        repeat_number=Integer.parseInt(versionArray[selectedItem[0]]);

                    }
                }).setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlg.create();
                dlg.show();

                panel.thread = new DrawBall(panel,GameActivity.this);

                panel.thread.start();
                panel.thread.setEnd_point(true);

            }
        });

       // StartStopBTN = (Button)findViewById(R.id.StartStopButton);




        //sc2.started
        //sc2 = new ScaleDetector2(this);
       // md2 = new midiRecord(this);

        /*
        StartStopBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //마이크 인식시 사용
               if(sc2.started){
                    StartStopBTN.setText("Start");
                    sc2.started=false;
                    recordTask.cancel(true);


                }else{
                    StartStopBTN.setText("Stop");
                    sc2.started=true;
                    recordTask = sc2.new RecordAudio();
                    recordTask.execute();
                }

                //midi 인식시 사용

                if(md2.started){
                    StartStopBTN.setText("Start");
                    md2.started=false;
                    recordTask2.cancel(true);

                }else{
                    StartStopBTN.setText("Stop");
                    md2.started=true;
                    recordTask2 = md2.new RecordAudio();
                    recordTask2.execute();
                }



            }
        });

         */

    }//oncreate
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (usbMidiSystem != null) {
            usbMidiSystem.terminate();
        }
    }


    public void onToggleClicked(View v) {
        boolean is_on = ((ToggleButton)v).isChecked();
        ToggleButton play_or_stop = (ToggleButton)findViewById(R.id.btn_play_or_stop);
        if(is_on) {
            //play_or_stop.setText("멈추기");

            panel.thread = new DrawBall(panel,this);
            panel.thread.start();

        } else {
            //play_or_stop.setText("연습하기");
            //score.setVisibility(View.VISIBLE);
            panel.thread.setStop(true);
        }
    }

    public void insertImgProtocol(){
        sum_right_count_while = 0;
        sum_left_count_while = 0;

        music_selected = true; // 노래를 선택했다

        insertImgRightProtocol("/storage/self/primary/PIANO/bair.txt",panel);
        answer_right = insertImgAnswerProtocol("/storage/self/primary/PIANO/bair.txt");
    }
    public void insertProtocol() {
        sum_right_count_while = 0;
        sum_left_count_while = 0;

        music_selected = true; // 노래를 선택했다

        /*
        insertRightProtocol(R.raw.butterfly_right, panel);
        //insertLeftProtocol(R.raw.elise_left, panel);
        answer_right = insertAnswerProtocol(R.raw.butterfly_right);
        //answer_left = insertAnswerProtocol(R.raw.elise_left);

         */
        switch(music) {
            case 0:
                switch_music=0;
                insertRightProtocol(R.raw.butterfly_right, panel);
                //insertLeftProtocol(R.raw.elise_left, panel);
                answer_right = insertAnswerProtocol(R.raw.butterfly_right);
                //answer_left = insertAnswerProtocol(R.raw.elise_left);
                break;

            case 1:
                switch_music=1;
                insertRightProtocol(R.raw.shanz_right, panel);
                insertLeftProtocol(R.raw.shanz_left, panel);
                answer_right = insertAnswerProtocol(R.raw.shanz_right);
                answer_left = insertAnswerProtocol(R.raw.shanz_left);
                break;

            case 2:
                switch_music=2;
                insertRightProtocol(R.raw.summer_right, panel);
                insertLeftProtocol(R.raw.summer_left, panel);
                answer_right = insertAnswerProtocol(R.raw.summer_right);
                answer_left = insertAnswerProtocol(R.raw.summer_left);
                break;
/*
            case rabbit:
                insertRightProtocol(R.raw.rabbit_right, panel);
                insertLeftProtocol(R.raw.rabbit_left, panel);
                answer_right = insertAnswerProtocol(R.raw.rabbit_right);
                answer_left = insertAnswerProtocol(R.raw.rabbit_left);
                break;

 */

        }


    }
    public void insertRightProtocol(int music_name, Panel panel) { // 오른손 프로토콜 입력
        String strBuf = ReadTextAssets(music_name);
        String[] lines = strBuf.split("\n");

        for (String note : lines) {
            panel.balls_right.add(new Ball(panel.getContext(), Integer.parseInt(note.trim())));
        }
    }

    public void insertLeftProtocol(int music_name, Panel panel) { // 왼손 프로토콜 입력
        String strBuf = ReadTextAssets(music_name);
        String[] lines = strBuf.split("\n");

        for (String note : lines) {
            panel.balls_left.add(new Ball(panel.getContext(), Integer.parseInt(note.trim())));
        }
    }

    public void insertImgRightProtocol(String music_name, Panel panel) { // 오른손 프로토콜 입력
        String strBuf = ReadImgTextAssets(music_name);
        String[] lines = strBuf.split("\n");

        for (String note : lines) {
            panel.balls_right.add(new Ball(panel.getContext(), Integer.parseInt(note.trim())));
        }
    }

    public void insertImgLeftProtocol(String music_name, Panel panel) { // 왼손 프로토콜 입력
        String strBuf = ReadImgTextAssets(music_name);
        String[] lines = strBuf.split("\n");

        for (String note : lines) {
            panel.balls_left.add(new Ball(panel.getContext(), Integer.parseInt(note.trim())));
        }
    }

    public String ReadTextAssets(int music_name) {
        String text = null;

        try {
            InputStream is = GameActivity.this.getResources().openRawResource(music_name);
            byte[] buffer = new byte[is.available()];

            is.read(buffer);
            is.close();
            text = new String(buffer);
        } catch(IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(GameActivity.this, "game start_Read Text", Toast.LENGTH_SHORT).show();
        return text;
    }

    public String ReadImgTextAssets(String music_name) {
        String text = null;


        StringBuffer strBuffer = new StringBuffer();
        try{
            InputStream is = new FileInputStream(music_name);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line="";
            while((line=reader.readLine())!=null){
                strBuffer.append(line+"\n");
            }

            reader.close();
            is.close();
        }catch (IOException e){
            e.printStackTrace();

        }
        text = strBuffer.toString();


/*
        try {
            InputStream is = openFileInput(music_name);
            byte[] buffer = new byte[is.available()];

            is.read(buffer);
            is.close();
            text = new String(buffer);
        } catch(IOException e) {
            e.printStackTrace();
        }

 */
        Toast.makeText(GameActivity.this, "game start_ReadIMG", Toast.LENGTH_SHORT).show();

        return text;
    }



    public ArrayList<ArrayList> insertAnswerProtocol(int music_name) {
        String strBuf = ReadTextAssets(music_name);
        String[] lines = strBuf.split("\n");
        ArrayList<ArrayList> answer = new ArrayList<>();

        for(String note : lines) {
            ArrayList<Integer> temp = new ArrayList<>();

            if(Integer.parseInt(note.trim()) % 1000000 >= 100000) { // 흰건반
                switch((Integer.parseInt(note.trim()) / 1000000) % 10) { // 옥타브
                    case 1://옥타브 1이고
                        switch((Integer.parseInt(note.trim()) / 100000) % 10) { //흰 건반일 경우
                            case 1:
                                temp.add(7);temp.add(8);temp.add(15); //흰 건반 번호가 1(도)
                                break;
                            case 2:
                                temp.add(10);temp.add(13);temp.add(14);
                                break;
                            case 3:
                                temp.add(15);temp.add(16);
                                break;
                            case 4:
                                temp.add(21);temp.add(22);
                                break;
                            case 5:
                                temp.add(17);temp.add(44);
                                break;
                            case 6:
                                temp.add(13);temp.add(14);
                                break;
                            case 7:
                                temp.add(15);temp.add(16);
                                break;
                        }
                        break;
                    case 2:
                        switch((Integer.parseInt(note.trim()) / 100000) % 10) {
                            case 1:
                                temp.add(7);temp.add(15);temp.add(16);
                                break;
                            case 2:
                                temp.add(9);temp.add(10);
                                break;
                            case 3:
                                temp.add(21);temp.add(22);
                                break;
                            case 4:
                                temp.add(21);temp.add(22);
                                break;
                            case 5:
                                temp.add(11);temp.add(25);temp.add(26);
                                break;
                            case 6:
                                temp.add(13);temp.add(14);
                                break;
                            case 7:
                                temp.add(16);temp.add(31);
                                break;
                        }
                        break;
                    case 3:
                        switch((Integer.parseInt(note.trim()) / 100000) % 10) {
                            case 1:
                                temp.add(89);
                                break;
                            case 2:
                                temp.add(17);temp.add(18);
                                break;
                            case 3:
                                temp.add(20);temp.add(21);
                                break;
                            case 4:
                                temp.add(21);temp.add(22);
                                break;
                            case 5:
                                temp.add(23);temp.add(25);
                                break;
                            case 6:
                                temp.add(27);temp.add(28);
                                break;
                            case 7:
                                temp.add(31);temp.add(32);
                                break;
                        }
                        break;
                    case 4:
                        switch((Integer.parseInt(note.trim()) / 100000) % 10) {
                            case 1:
                                temp.add(33);temp.add(34);
                                break;
                            case 2:
                                temp.add(37);temp.add(38);
                                break;
                            case 3:
                                temp.add(41);temp.add(42);
                                break;
                            case 4:
                                temp.add(43);temp.add(44);
                                break;
                            case 5:
                                temp.add(49);temp.add(50);
                                break;
                            case 6:
                                temp.add(55);temp.add(56);
                                break;
                            case 7:
                                temp.add(62);temp.add(63);
                                break;
                        }
                        break;
                    case 5:
                        switch((Integer.parseInt(note.trim()) / 100000) % 10) {
                            case 1:
                                temp.add(65);temp.add(67);
                                break;
                            case 2:
                                temp.add(76);
                                break;
                            case 3:
                                temp.add(84);
                                break;
                            case 4:
                                temp.add(90);
                                break;
                            case 5:
                                temp.add(99);
                                break;
                            case 6:
                                temp.add(113);
                                break;
                            case 7:
                                temp.add(126);
                                break;
                        }
                        break;
                }
            } else { // 검은 건반
                switch((Integer.parseInt(note.trim()) / 1000000) % 10) {
                    case 1:
                        switch((Integer.parseInt(note.trim()) / 10000) % 10) {
                            case 1:
                                temp.add(8);temp.add(11);temp.add(14);
                                break;
                            case 2:
                                temp.add(13);temp.add(14);
                                break;
                            case 3:
                                temp.add(17);temp.add(18);
                                break;
                            case 4:
                                temp.add(11);temp.add(13);
                                break;
                            case 5:
                                temp.add(14);temp.add(16);
                                break;
                        }
                        break;
                    case 2:
                        switch((Integer.parseInt(note.trim()) / 10000) % 10) {
                            case 1:
                                temp.add(17);temp.add(18);
                                break;
                            case 2:
                                temp.add(9);temp.add(10);
                                break;
                            case 3:
                                temp.add(11);temp.add(12);
                                break;
                            case 4:
                                temp.add(13);temp.add(14);
                                break;
                            case 5:
                                temp.add(13);
                                break;
                        }
                        break;
                    case 3:
                        switch((Integer.parseInt(note.trim()) / 10000) % 10) {
                            case 1:
                                temp.add(89);
                                break;
                            case 2:
                                temp.add(19);
                                break;
                            case 3:
                                temp.add(23);temp.add(24);
                                break;
                            case 4:
                                temp.add(25);temp.add(26);
                                break;
                            case 5:
                                temp.add(30);temp.add(60);
                                break;
                        }
                        break;
                    case 4:
                        switch((Integer.parseInt(note.trim()) / 10000) % 10) {
                            case 1:
                                temp.add(35);temp.add(36);
                                break;
                            case 2:
                                temp.add(39);temp.add(40);
                                break;
                            case 3:
                                temp.add(45);temp.add(47);
                                break;
                            case 4:
                                temp.add(53);
                                break;
                            case 5:
                                temp.add(59);temp.add(60);
                                break;
                        }
                        break;
                    case 5:
                        switch((Integer.parseInt(note.trim()) / 10000) % 10) {
                            case 1:
                                temp.add(70);temp.add(71);
                                break;
                            case 2:
                                temp.add(79);temp.add(80);
                                break;
                            case 3:
                                temp.add(94);
                                break;
                            case 4:
                                temp.add(105);
                                break;
                            case 5:
                                temp.add(118);
                                break;
                        }
                        break;
                }
            }

            answer.add(temp);
        }

        return answer;
    }

    public ArrayList<ArrayList> insertImgAnswerProtocol(String music_name) {
        String strBuf = ReadImgTextAssets(music_name);
        String[] lines = strBuf.split("\n");
        ArrayList<ArrayList> answer = new ArrayList<>();

        for(String note : lines) {
            ArrayList<Integer> temp = new ArrayList<>();

            if(Integer.parseInt(note.trim()) % 1000000 >= 100000) { // 흰건반
                switch((Integer.parseInt(note.trim()) / 1000000) % 10) { // 옥타브
                    case 1:
                        switch((Integer.parseInt(note.trim()) / 100000) % 10) {
                            case 1:
                                temp.add(7);temp.add(8);temp.add(15);
                                break;
                            case 2:
                                temp.add(10);temp.add(13);temp.add(14);
                                break;
                            case 3:
                                temp.add(15);temp.add(16);
                                break;
                            case 4:
                                temp.add(21);temp.add(22);
                                break;
                            case 5:
                                temp.add(17);temp.add(44);
                                break;
                            case 6:
                                temp.add(13);temp.add(14);
                                break;
                            case 7:
                                temp.add(15);temp.add(16);
                                break;
                        }
                        break;
                    case 2:
                        switch((Integer.parseInt(note.trim()) / 100000) % 10) {
                            case 1:
                                temp.add(7);temp.add(15);temp.add(16);
                                break;
                            case 2:
                                temp.add(9);temp.add(10);
                                break;
                            case 3:
                                temp.add(21);temp.add(22);
                                break;
                            case 4:
                                temp.add(21);temp.add(22);
                                break;
                            case 5:
                                temp.add(11);temp.add(25);temp.add(26);
                                break;
                            case 6:
                                temp.add(13);temp.add(14);
                                break;
                            case 7:
                                temp.add(16);temp.add(31);
                                break;
                        }
                        break;
                    case 3:
                        switch((Integer.parseInt(note.trim()) / 100000) % 10) {
                            case 1:
                                temp.add(89);
                                break;
                            case 2:
                                temp.add(17);temp.add(18);
                                break;
                            case 3:
                                temp.add(20);temp.add(21);
                                break;
                            case 4:
                                temp.add(21);temp.add(22);
                                break;
                            case 5:
                                temp.add(23);temp.add(25);
                                break;
                            case 6:
                                temp.add(27);temp.add(28);
                                break;
                            case 7:
                                temp.add(31);temp.add(32);
                                break;
                        }
                        break;
                    case 4:
                        switch((Integer.parseInt(note.trim()) / 100000) % 10) {
                            case 1:
                                temp.add(33);temp.add(34);
                                break;
                            case 2:
                                temp.add(37);temp.add(38);
                                break;
                            case 3:
                                temp.add(41);temp.add(42);
                                break;
                            case 4:
                                temp.add(43);temp.add(44);
                                break;
                            case 5:
                                temp.add(49);temp.add(50);
                                break;
                            case 6:
                                temp.add(55);temp.add(56);
                                break;
                            case 7:
                                temp.add(62);temp.add(63);
                                break;
                        }
                        break;
                    case 5:
                        switch((Integer.parseInt(note.trim()) / 100000) % 10) {
                            case 1:
                                temp.add(65);temp.add(67);
                                break;
                            case 2:
                                temp.add(76);
                                break;
                            case 3:
                                temp.add(84);
                                break;
                            case 4:
                                temp.add(90);
                                break;
                            case 5:
                                temp.add(99);
                                break;
                            case 6:
                                temp.add(113);
                                break;
                            case 7:
                                temp.add(126);
                                break;
                        }
                        break;
                }
            } else { // 검은 건반
                switch((Integer.parseInt(note.trim()) / 1000000) % 10) {
                    case 1:
                        switch((Integer.parseInt(note.trim()) / 10000) % 10) {
                            case 1:
                                temp.add(8);temp.add(11);temp.add(14);
                                break;
                            case 2:
                                temp.add(13);temp.add(14);
                                break;
                            case 3:
                                temp.add(17);temp.add(18);
                                break;
                            case 4:
                                temp.add(11);temp.add(13);
                                break;
                            case 5:
                                temp.add(14);temp.add(16);
                                break;
                        }
                        break;
                    case 2:
                        switch((Integer.parseInt(note.trim()) / 10000) % 10) {
                            case 1:
                                temp.add(17);temp.add(18);
                                break;
                            case 2:
                                temp.add(9);temp.add(10);
                                break;
                            case 3:
                                temp.add(11);temp.add(12);
                                break;
                            case 4:
                                temp.add(13);temp.add(14);
                                break;
                            case 5:
                                temp.add(13);
                                break;
                        }
                        break;
                    case 3:
                        switch((Integer.parseInt(note.trim()) / 10000) % 10) {
                            case 1:
                                temp.add(89);
                                break;
                            case 2:
                                temp.add(19);
                                break;
                            case 3:
                                temp.add(23);temp.add(24);
                                break;
                            case 4:
                                temp.add(25);temp.add(26);
                                break;
                            case 5:
                                temp.add(30);temp.add(60);
                                break;
                        }
                        break;
                    case 4:
                        switch((Integer.parseInt(note.trim()) / 10000) % 10) {
                            case 1:
                                temp.add(35);temp.add(36);
                                break;
                            case 2:
                                temp.add(39);temp.add(40);
                                break;
                            case 3:
                                temp.add(45);temp.add(47);
                                break;
                            case 4:
                                temp.add(53);
                                break;
                            case 5:
                                temp.add(59);temp.add(60);
                                break;
                        }
                        break;
                    case 5:
                        switch((Integer.parseInt(note.trim()) / 10000) % 10) {
                            case 1:
                                temp.add(70);temp.add(71);
                                break;
                            case 2:
                                temp.add(79);temp.add(80);
                                break;
                            case 3:
                                temp.add(94);
                                break;
                            case 4:
                                temp.add(105);
                                break;
                            case 5:
                                temp.add(118);
                                break;
                        }
                        break;
                }
            }

            answer.add(temp);
        }

        return answer;
    }
    public void clickVelocity(View v) {
        ImageView velocity1 = (ImageView)findViewById(R.id.btn_velocity1);
        ImageView velocity2 = (ImageView)findViewById(R.id.btn_velocity2);
        ImageView velocity3 = (ImageView)findViewById(R.id.btn_velocity3);
        ImageView velocity4 = (ImageView)findViewById(R.id.btn_velocity4);
        ImageView velocity5 = (ImageView)findViewById(R.id.btn_velocity5);
        ImageView velocity6 = (ImageView)findViewById(R.id.btn_velocity6);
        ImageView velocity7 = (ImageView)findViewById(R.id.btn_velocity7);
        ImageView velocity8 = (ImageView)findViewById(R.id.btn_velocity8);


        switch (v.getId()) {
            case R.id.btn_velocity1:
                velocity1.setBackgroundResource(R.drawable.btn_velocity1_entered);
                velocity2.setBackgroundResource(R.drawable.btn_velocity2);
                velocity3.setBackgroundResource(R.drawable.btn_velocity3);
                velocity4.setBackgroundResource(R.drawable.btn_velocity4);
                velocity5.setBackgroundResource(R.drawable.btn_velocity5);
                velocity6.setBackgroundResource(R.drawable.btn_velocity6);
                velocity7.setBackgroundResource(R.drawable.btn_velocity7);
                velocity8.setBackgroundResource(R.drawable.btn_velocity8);
                velocity = 1f;
                break;
            case R.id.btn_velocity2:
                velocity1.setBackgroundResource(R.drawable.btn_velocity1);
                velocity2.setBackgroundResource(R.drawable.btn_velocity2_entered);
                velocity3.setBackgroundResource(R.drawable.btn_velocity3);
                velocity4.setBackgroundResource(R.drawable.btn_velocity4);
                velocity5.setBackgroundResource(R.drawable.btn_velocity5);
                velocity6.setBackgroundResource(R.drawable.btn_velocity6);
                velocity7.setBackgroundResource(R.drawable.btn_velocity7);
                velocity8.setBackgroundResource(R.drawable.btn_velocity8);
                velocity = 1.5f;
                break;
            case R.id.btn_velocity3:
                velocity1.setBackgroundResource(R.drawable.btn_velocity1);
                velocity2.setBackgroundResource(R.drawable.btn_velocity2);
                velocity3.setBackgroundResource(R.drawable.btn_velocity3_entered);
                velocity4.setBackgroundResource(R.drawable.btn_velocity4);
                velocity5.setBackgroundResource(R.drawable.btn_velocity5);
                velocity6.setBackgroundResource(R.drawable.btn_velocity6);
                velocity7.setBackgroundResource(R.drawable.btn_velocity7);
                velocity8.setBackgroundResource(R.drawable.btn_velocity8);
                velocity = 2f;
                break;
            case R.id.btn_velocity4:
                velocity1.setBackgroundResource(R.drawable.btn_velocity1);
                velocity2.setBackgroundResource(R.drawable.btn_velocity2);
                velocity3.setBackgroundResource(R.drawable.btn_velocity3);
                velocity4.setBackgroundResource(R.drawable.btn_velocity4_entered);
                velocity5.setBackgroundResource(R.drawable.btn_velocity5);
                velocity6.setBackgroundResource(R.drawable.btn_velocity6);
                velocity7.setBackgroundResource(R.drawable.btn_velocity7);
                velocity8.setBackgroundResource(R.drawable.btn_velocity8);
                velocity = 2.5f;
                break;
            case R.id.btn_velocity5:
                velocity1.setBackgroundResource(R.drawable.btn_velocity1);
                velocity2.setBackgroundResource(R.drawable.btn_velocity2);
                velocity3.setBackgroundResource(R.drawable.btn_velocity3);
                velocity4.setBackgroundResource(R.drawable.btn_velocity4);
                velocity5.setBackgroundResource(R.drawable.btn_velocity5_entered);
                velocity6.setBackgroundResource(R.drawable.btn_velocity6);
                velocity7.setBackgroundResource(R.drawable.btn_velocity7);
                velocity8.setBackgroundResource(R.drawable.btn_velocity8);
                velocity = 3f;
                break;
            case R.id.btn_velocity6:
                velocity1.setBackgroundResource(R.drawable.btn_velocity1);
                velocity2.setBackgroundResource(R.drawable.btn_velocity2);
                velocity3.setBackgroundResource(R.drawable.btn_velocity3);
                velocity4.setBackgroundResource(R.drawable.btn_velocity4);
                velocity5.setBackgroundResource(R.drawable.btn_velocity5);
                velocity6.setBackgroundResource(R.drawable.btn_velocity6_entered);
                velocity7.setBackgroundResource(R.drawable.btn_velocity7);
                velocity8.setBackgroundResource(R.drawable.btn_velocity8);
                velocity = 3.5f;
                break;
            case R.id.btn_velocity7:
                velocity1.setBackgroundResource(R.drawable.btn_velocity1);
                velocity2.setBackgroundResource(R.drawable.btn_velocity2);
                velocity3.setBackgroundResource(R.drawable.btn_velocity3);
                velocity4.setBackgroundResource(R.drawable.btn_velocity4);
                velocity5.setBackgroundResource(R.drawable.btn_velocity5);
                velocity6.setBackgroundResource(R.drawable.btn_velocity6);
                velocity7.setBackgroundResource(R.drawable.btn_velocity7_entered);
                velocity8.setBackgroundResource(R.drawable.btn_velocity8);
                velocity = 4f;
                break;
            case R.id.btn_velocity8:
                velocity1.setBackgroundResource(R.drawable.btn_velocity1);
                velocity2.setBackgroundResource(R.drawable.btn_velocity2);
                velocity3.setBackgroundResource(R.drawable.btn_velocity3);
                velocity4.setBackgroundResource(R.drawable.btn_velocity4);
                velocity5.setBackgroundResource(R.drawable.btn_velocity5);
                velocity6.setBackgroundResource(R.drawable.btn_velocity6);
                velocity7.setBackgroundResource(R.drawable.btn_velocity7);
                velocity8.setBackgroundResource(R.drawable.btn_velocity8_entered);
                velocity = 4.5f;
                break;
        }

        sum_right_count_while = 0;
        sum_left_count_while = 0;

        for(Ball ball : balls_right) {
            if (ball.getCount_while() > 0) {
                ball.setCount_while(ball.getProtocol(), velocity);
            }
        }

        for(Ball ball : balls_left) {
            if (ball.getCount_while() > 0) {
                ball.setCount_while(ball.getProtocol(), velocity);
            }
        }

        panel.invalidate();
    }

    public void onBackPressed() {
        try{
            //panel.thread.setStop(true);
            panel.balls_right.clear();
            panel.balls_left.clear();
            panel.repeat_right.clear();
            panel.repeat_left.clear();
            panel.repeat_right_sum.clear();
            panel.repeat_left_sum.clear();
            panel.repeat_number=0;
            answer_right.clear();
            answer_left.clear();

            StartStopBTN.setText("Start");
            md2.started=false;
            recordTask2.cancel(true);

            Toast.makeText(this, "연습을 종료합니다!", Toast.LENGTH_SHORT).show();




            startActivity(new Intent(GameActivity.this,Home.class));
            ActivityCompat.finishAffinity(this);

            y_piano_upleft = y_piano_upleft + 218; // 160pixel = 40dp
        }catch (NullPointerException e){
            startActivity(new Intent(GameActivity.this,Home.class));
            ActivityCompat.finishAffinity(this);

            y_piano_upleft = y_piano_upleft + 218; // 160pixel = 40dp
        }


        //audioRecord.stop();
        //record.cancel(true);


    }


}
