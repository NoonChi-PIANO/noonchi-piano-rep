package com.example.test;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.util.ArrayList;

class Panel extends SurfaceView implements SurfaceHolder.Callback {

    public static int surface_width;
    public static int surface_height;
    public static ArrayList<Ball> balls_right = new ArrayList<>();
    public static ArrayList<Ball> balls_left = new ArrayList<>();
    public static ArrayList<Ball> repeat_right = new ArrayList<>();
    public static ArrayList<Ball> repeat_left = new ArrayList<>();
    public static ArrayList<ArrayList<Ball>> repeat_right_sum = new ArrayList<>();
    public static ArrayList<ArrayList<Ball>> repeat_left_sum = new ArrayList<>();
    public DrawBall thread;
    public static boolean start_point; //
    public static boolean end_point; //
    public static boolean start; //
    public static int repeat_flag; //
    public static int repeat_number;
    public static int repeat_right_num;
    public static int repeat_left_num;

    public Panel(Context context) {
        super(context);
        getHolder().addCallback(this);
        start_point = false;
        end_point=false;
        start = false;
        repeat_flag=0;
        repeat_right_num=0;
        repeat_left_num=0;
        repeat_right = new ArrayList<>();
        repeat_left= new ArrayList<>();
        repeat_right_sum = new ArrayList<>();
        repeat_left_sum = new ArrayList<>();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread = new DrawBall(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int width, int height) {
        surface_width = width;
        surface_height = height;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
