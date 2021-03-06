package com.example.test;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.test.Panel.repeat_number;
import static com.example.test.SetPianoOctaveActivity.gunban;
import static com.example.test.Ball.WHITE;
import static com.example.test.Ball.BLACK;
import static com.example.test.DrawGunban.x_piano_upleft;
import static com.example.test.DrawGunban.y_piano_upleft;
import static com.example.test.Panel.balls_right;
import static com.example.test.Panel.balls_left;
import static com.example.test.Panel.repeat_right;
import static com.example.test.Panel.repeat_flag;
import static com.example.test.Panel.start;
import static com.example.test.Panel.start_point;
import static com.example.test.Panel.end_point;
import static com.example.test.Panel.repeat_right_sum;
import static com.example.test.Panel.repeat_left_sum;
import static com.example.test.Panel.repeat_right_num;
import static com.example.test.Panel.repeat_left_num;
import static com.example.test.Panel.repeat_left;

class DrawBall extends Thread {
    private SurfaceHolder holder;
    private Paint white_gunban;
    private Paint black_gunban;
    private Paint judge_line;
    private boolean stop;



    public DrawBall(Panel panel){
        setHolder(panel);
        setColor();
        setStop(false);
       // setStart_point(false);
        //setEnd_point(false);
        //repeat_flag=0;


    }

    public void setHolder(Panel panel){
        holder=panel.getHolder();
    }

    public void setColor(){
        white_gunban = new Paint();
        white_gunban.setColor(Color.WHITE);
        black_gunban = new Paint();
        black_gunban.setColor(Color.BLACK);
        judge_line=new Paint();
        judge_line.setColor(Color.GRAY);
    }

    public void setStop(boolean true_or_false){
        stop = true_or_false;
    }
    public void setStart_point(boolean true_or_false){start_point = true_or_false;}
    public void setStart(boolean true_or_false){start=true_or_false;}
    public void setEnd_point(boolean true_or_false){end_point = true_or_false;}
    public void run(){

        while(!stop){
            Canvas canvas = holder.lockCanvas();

            if(canvas != null){
                canvas.drawColor(Color.BLACK);
                for(int i=0;i<7*gunban.getOctave();i++){
                    float x_white_gunban_upleft = x_piano_upleft + (47 + gunban.getCountSizeChange()) * i;
                    float y_white_gunban_upleft = y_piano_upleft;
                    float x_white_gunban_downright = x_white_gunban_upleft + 47 + gunban.getCountSizeChange();
                    float y_white_gunban_downright = y_piano_upleft + gunban.getWhiteVertical();
                    canvas.drawRect(x_white_gunban_upleft, y_white_gunban_upleft, x_white_gunban_downright, y_white_gunban_downright, white_gunban);

                }


                synchronized(balls_right) { // 흰색 건반에 내려오는 빛 그리기(오른쪽)
                    if(start_point&&repeat_flag==0) { //한번만 repeat_right에 복사
                        for(int i=0;i<balls_right.size();i++){
                            balls_right.get(i).setColor(balls_right.get(i).getProtocol());
                        }
                        for(Ball ball : balls_right){
                            repeat_right.add(new Ball(ball));
                        }
                        repeat_flag=1;
                        end_point=false;
                    }
                    if(end_point&&repeat_flag==1){
                        for(int i=0;i<repeat_right.size()-1;i++){
                            if(balls_right.get(i).getGo_down()){
                                if(balls_right.get(i).getTouch_Line()==false){
                                    repeat_right.remove(i);
                                    i--;
                                }
                            }
                        }
                        repeat_right.remove(repeat_right.size()-1);
                        balls_right.clear();
                        repeat_flag=0;
                        start_point=false;
                    }
                    if(start){
                        //setStop(false);
                        for(int i=1;i<repeat_number+1;i++){
                            ArrayList<Ball> repeat_array = new ArrayList<>();
                            for(Ball ball : repeat_right){
                                repeat_array.add(new Ball(ball));
                            }
                            repeat_right_sum.add(repeat_array);
                        }
                            for(Ball ball : repeat_right) {
                                if(ball.getType() == WHITE && ball.getGo_down()) {
                                    if (ball.getCount_while() > 0) {
                                        ball.minusCount_while();
                                    } else {
                                        ball.drawWhiteGubanLight(canvas);
                                    }
                                }

                            }
                            if(repeat_right.size()>0) {
                                if (repeat_right.get(repeat_right.size() - 1).getTouch_Line() == true) {

                                    repeat_right.clear();
                                    for (Ball ball2 : repeat_right_sum.get(repeat_right_num)) {
                                        repeat_right.add(new Ball(ball2));
                                    }
                                    repeat_right_num++;
                                    if (repeat_right_num == repeat_number) {
                                        setStop(true);
                                        repeat_right_num = 0;
                                    }
                                }
                            }
                      }
                    else if(!start){ 
                        for (Ball ball : balls_right) { //하나하나씩 돌고 while 에서 또 돌고
                            if (ball.getType() == WHITE && ball.getGo_down()) {
                                if (ball.getCount_while() > 0) {
                                    ball.minusCount_while();
                                } else {
                                    ball.drawWhiteGubanLight(canvas);
                                }
                            }
                        }
                    }
                }


                synchronized(balls_left) { // 흰색 건반에 내려오는 빛 그리기(오른쪽)
                    if(start_point&&repeat_flag==0) { //한번만 repeat_left에 복사
                        for(int i=0;i<balls_left.size();i++){
                            balls_left.get(i).setColor(balls_left.get(i).getProtocol());
                        }
                        for(Ball ball : balls_left){
                            repeat_left.add(new Ball(ball));
                        }
                        repeat_flag=1;
                        end_point=false;
                    }
                    if(end_point&&repeat_flag==1){
                        for(int i=0;i<repeat_left.size()-1;i++){
                            if(balls_left.get(i).getGo_down()){
                                if(balls_left.get(i).getTouch_Line()==false){
                                    repeat_left.remove(i);
                                    i--;
                                }
                            }
                        }
                        repeat_left.remove(repeat_left.size()-1);
                        balls_left.clear();
                        repeat_flag=0;
                        start_point=false;
                    }
                    if(start){
                        //setStop(false);
                        for(int i=1;i<repeat_number+1;i++){
                            ArrayList<Ball> repeat_array = new ArrayList<>();
                            for(Ball ball : repeat_left){
                                repeat_array.add(new Ball(ball));
                            }
                            repeat_left_sum.add(repeat_array);
                        }
                        for(Ball ball : repeat_left) {
                            if(ball.getType() == WHITE && ball.getGo_down()) {
                                if (ball.getCount_while() > 0) {
                                    ball.minusCount_while();
                                } else {
                                    ball.drawWhiteGubanLight(canvas);
                                }
                            }

                        }
                        if(repeat_left.size()>0) {
                            if (repeat_left.get(repeat_left.size() - 1).getTouch_Line() == true) {

                                repeat_left.clear();
                                for (Ball ball2 : repeat_left_sum.get(repeat_left_num)) {
                                    repeat_left.add(new Ball(ball2));
                                }
                                repeat_left_num++;
                                if (repeat_left_num == repeat_number) {
                                    setStop(true);
                                    repeat_left_num = 0;
                                }
                            }
                        }
                    }
                    else if(!start){
                        for (Ball ball : balls_left) { //하나하나씩 돌고 while 에서 또 돌고
                            if (ball.getType() == WHITE && ball.getGo_down()) {
                                if (ball.getCount_while() > 0) {
                                    ball.minusCount_while();
                                } else {
                                    ball.drawWhiteGubanLight(canvas);
                                }
                            }
                        }
                    }
                }

                for(int i = 0; i < gunban.getOctave(); i++) { // 검은색 건반 그리기
                    float x_black_gunban1_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (((47/1.3f + gunban.getCountSizeChange()) * 3) - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 2) / 3;
                    float x_black_gunban1_downright = x_black_gunban1_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_black_gunban1_upleft = y_piano_upleft;
                    float y_black_gunban1_downright = y_piano_upleft + gunban.getBlackVertical();
                    float x_black_gunban2_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + ((47/1.3f + gunban.getCountSizeChange()) * 3 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 2) / 3 * 2 + 23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange();
                    float x_black_gunban2_downright = x_black_gunban2_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_black_gunban2_upleft = y_piano_upleft;
                    float y_black_gunban2_downright = y_piano_upleft + gunban.getBlackVertical();
                    float x_black_gunban3_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (47/1.3f + gunban.getCountSizeChange()) * 3 + ((47/1.3f + gunban.getCountSizeChange()) * 4 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 3) / 4;
                    float x_black_gunban3_downright = x_black_gunban3_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_black_gunban3_upleft = y_piano_upleft;
                    float y_black_gunban3_downright = y_piano_upleft + gunban.getBlackVertical();
                    float x_black_gunban4_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (47/1.3f + gunban.getCountSizeChange()) * 3 + ((47/1.3f + gunban.getCountSizeChange()) * 4 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 3) / 4 * 2 + 23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange();
                    float x_black_gunban4_downright = x_black_gunban4_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_black_gunban4_upleft = y_piano_upleft;
                    float y_black_gunban4_downright = y_piano_upleft + gunban.getBlackVertical();
                    float x_black_gunban5_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (47/1.3f + gunban.getCountSizeChange()) * 3 + ((47/1.3f + gunban.getCountSizeChange()) * 4 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 3) / 4 * 3 + (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 2;
                    float x_black_gunban5_downright = x_black_gunban5_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_black_gunban5_upleft = y_piano_upleft;
                    float y_black_gunban5_downright = y_piano_upleft + gunban.getBlackVertical();

                    canvas.drawRect(x_black_gunban1_upleft, y_black_gunban1_upleft, x_black_gunban1_downright, y_black_gunban1_downright, black_gunban);
                    canvas.drawRect(x_black_gunban2_upleft, y_black_gunban2_upleft, x_black_gunban2_downright, y_black_gunban2_downright, black_gunban);
                    canvas.drawRect(x_black_gunban3_upleft, y_black_gunban3_upleft, x_black_gunban3_downright, y_black_gunban3_downright, black_gunban);
                    canvas.drawRect(x_black_gunban4_upleft, y_black_gunban4_upleft, x_black_gunban4_downright, y_black_gunban4_downright, black_gunban);
                    canvas.drawRect(x_black_gunban5_upleft, y_black_gunban5_upleft, x_black_gunban5_downright, y_black_gunban5_downright, black_gunban);
                }

                synchronized(balls_right) { // 흰색 건반에 내려오는 빛 그리기(오른쪽)
                    /*
                    if(start_point&&repeat_flag==0) { //한번만 repeat_right에 복사
                        for(int i=0;i<balls_right.size();i++){
                            balls_right.get(i).setColor(balls_right.get(i).getProtocol());
                        }
                        for(Ball ball : balls_right){
                            repeat_right.add(new Ball(ball));
                        }
                        repeat_flag=1;
                        end_point=false;
                    }
                    if(end_point&&repeat_flag==1){
                        for(int i=0;i<repeat_right.size()-1;i++){
                            if(balls_right.get(i).getType()==BLACK && balls_right.get(i).getGo_down()){
                                if(balls_right.get(i).getTouch_Line()==false){
                                    repeat_right.remove(i);
                                    i--;
                                }
                            }
                        }
                        repeat_right.remove(repeat_right.size()-1);
                        balls_right.clear();
                        repeat_flag=0;
                        start_point=false;
                    }
                    */

                    if(start){
                        //setStop(false);
                        for(int i=1;i<repeat_number+1;i++){
                            ArrayList<Ball> repeat_array = new ArrayList<>();
                            for(Ball ball : repeat_right){
                                repeat_array.add(new Ball(ball));
                            }
                            repeat_right_sum.add(repeat_array);
                        }
                        for(Ball ball : repeat_right) {
                            if(ball.getType() == BLACK && ball.getGo_down()) {
                                if (ball.getCount_while() > 0) {
                                    ball.minusCount_while();
                                } else {
                                    ball.drawBlackGubanLight(canvas);
                                }
                            }

                        }
                        /*
                        if(repeat_right.get(repeat_right.size()-1).getTouch_Line()==true){

                            repeat_right.clear();
                            for(Ball ball2 : repeat_right_sum.get(repeat_right_num)){
                                repeat_right.add(new Ball(ball2));
                            }
                            repeat_right_num++;
                            if(repeat_right_num==repeat_number){
                                setStop(true);
                                repeat_right_num=0;
                            }
                        }

                         */
                    }
                    else if(!start){
                        for (Ball ball : balls_right) { //하나하나씩 돌고 while 에서 또 돌고
                            if (ball.getType() == BLACK && ball.getGo_down()) {
                                if (ball.getCount_while() > 0) {
                                    ball.minusCount_while();
                                } else {
                                    ball.drawBlackGubanLight(canvas);
                                }
                            }
                        }
                    }
                }


                synchronized(balls_left) { // 흰색 건반에 내려오는 빛 그리기(오른쪽)
                    /*
                    if(start_point&&repeat_flag==0) { //한번만 repeat_left에 복사
                        for(int i=0;i<balls_left.size();i++){
                            balls_left.get(i).setColor(balls_left.get(i).getProtocol());
                        }
                        for(Ball ball : balls_left){
                            repeat_left.add(new Ball(ball));
                        }
                        repeat_flag=1;
                        end_point=false;
                    }

                    if(end_point&&repeat_flag==1){
                        for(int i=0;i<repeat_left.size()-1;i++){
                            if(balls_left.get(i).getType()==BLACK && balls_left.get(i).getGo_down()){
                                if(balls_left.get(i).getTouch_Line()==false){
                                    repeat_left.remove(i);
                                    i--;
                                }
                            }
                        }
                        repeat_left.remove(repeat_left.size()-1);
                        balls_left.clear();
                        repeat_flag=0;
                        start_point=false;
                    }
                    */

                    if(start){
                        //setStop(false);
                        for(int i=1;i<repeat_number+1;i++){
                            ArrayList<Ball> repeat_array = new ArrayList<>();
                            for(Ball ball : repeat_left){
                                repeat_array.add(new Ball(ball));
                            }
                            repeat_left_sum.add(repeat_array);
                        }
                        for(Ball ball : repeat_left) {
                            if(ball.getType() == BLACK && ball.getGo_down()) {
                                if (ball.getCount_while() > 0) {
                                    ball.minusCount_while();
                                } else {
                                    ball.drawBlackGubanLight(canvas);
                                }
                            }

                        }
                        /*
                        if(repeat_left.get(repeat_left.size()-1).getTouch_Line()==true){

                            repeat_left.clear();
                            for(Ball ball2 : repeat_left_sum.get(repeat_left_num)){
                                repeat_left.add(new Ball(ball2));
                            }
                            repeat_left_num++;
                            if(repeat_left_num==repeat_number){
                                setStop(true);
                                repeat_left_num=0;
                            }
                        }

                         */
                    }
                    else if(!start){
                        for (Ball ball : balls_left) { //하나하나씩 돌고 while 에서 또 돌고
                            if (ball.getType() == BLACK && ball.getGo_down()) {
                                if (ball.getCount_while() > 0) {
                                    ball.minusCount_while();
                                } else {
                                    ball.drawBlackGubanLight(canvas);
                                }
                            }
                        }
                    }
                }

                //canvas.drawRect(0, 0, 2000, y_piano_upleft, black_gunban); // 위에 가려주는 부분 그리기
                canvas.drawRect(x_piano_upleft, y_piano_upleft + gunban.getWhiteVertical() - 37, x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 *gunban.getOctave(), y_piano_upleft + gunban.getWhiteVertical() - 30, judge_line); // 흰색 건반 판정선 윗부분 그리기
                canvas.drawRect(x_piano_upleft, y_piano_upleft + gunban.getWhiteVertical() - 7, x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 *gunban.getOctave(), y_piano_upleft + gunban.getWhiteVertical(), judge_line); // 흰색 건반 판정선 아랫부분 그리기

                for(int i = 0; i < gunban.getOctave(); i++) { // 검은색 건반 판정선 위아래 다 그리기
                    float x_judgeline_up_black_gunban1_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (((47/1.3f + gunban.getCountSizeChange()) * 3) - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 2) / 3;
                    float x_judgeline_up_black_gunban1_downright = x_judgeline_up_black_gunban1_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_judgeline_up_black_gunban1_upleft = y_piano_upleft + gunban.getBlackVertical() - 37;
                    float y_judgeline_up_black_gunban1_downright = y_piano_upleft + gunban.getBlackVertical() - 30;
                    float x_judgeline_up_black_gunban2_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + ((47/1.3f + gunban.getCountSizeChange()) * 3 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 2) / 3 * 2 + 23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange();
                    float x_judgeline_up_black_gunban2_downright = x_judgeline_up_black_gunban2_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_judgeline_up_black_gunban2_upleft = y_piano_upleft + gunban.getBlackVertical() - 37;
                    float y_judgeline_up_black_gunban2_downright = y_piano_upleft + gunban.getBlackVertical() - 30;
                    float x_judgeline_up_black_gunban3_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (47/1.3f + gunban.getCountSizeChange()) * 3 + ((47/1.3f + gunban.getCountSizeChange()) * 4 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 3) / 4;
                    float x_judgeline_up_black_gunban3_downright = x_judgeline_up_black_gunban3_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_judgeline_up_black_gunban3_upleft = y_piano_upleft + gunban.getBlackVertical() - 37;
                    float y_judgeline_up_black_gunban3_downright = y_piano_upleft + gunban.getBlackVertical() - 30;
                    float x_judgeline_up_black_gunban4_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (47/1.3f + gunban.getCountSizeChange()) * 3 + ((47/1.3f + gunban.getCountSizeChange()) * 4 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 3) / 4 * 2 + 23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange();
                    float x_judgeline_up_black_gunban4_downright = x_judgeline_up_black_gunban4_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_judgeline_up_black_gunban4_upleft = y_piano_upleft + gunban.getBlackVertical() - 37;
                    float y_judgeline_up_black_gunban4_downright = y_piano_upleft + gunban.getBlackVertical() - 30;
                    float x_judgeline_up_black_gunban5_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (47/1.3f + gunban.getCountSizeChange()) * 3 + ((47/1.3f + gunban.getCountSizeChange()) * 4 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 3) / 4 * 3 + (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 2;
                    float x_judgeline_up_black_gunban5_downright = x_judgeline_up_black_gunban5_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_judgeline_up_black_gunban5_upleft = y_piano_upleft + gunban.getBlackVertical() - 37;
                    float y_judgeline_up_black_gunban5_downright = y_piano_upleft + gunban.getBlackVertical() - 30;
                    float x_judgeline_down_black_gunban1_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (((47/1.3f + gunban.getCountSizeChange()) * 3) - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 2) / 3;
                    float x_judgeline_down_black_gunban1_downright = x_judgeline_down_black_gunban1_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_judgeline_down_black_gunban1_upleft = y_piano_upleft + gunban.getBlackVertical() - 7;
                    float y_judgeline_down_black_gunban1_downright = y_piano_upleft + gunban.getBlackVertical();
                    float x_judgeline_down_black_gunban2_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + ((47/1.3f + gunban.getCountSizeChange()) * 3 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 2) / 3 * 2 + 23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange();
                    float x_judgeline_down_black_gunban2_downright = x_judgeline_down_black_gunban2_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_judgeline_down_black_gunban2_upleft = y_piano_upleft + gunban.getBlackVertical() - 7;
                    float y_judgeline_down_black_gunban2_downright = y_piano_upleft + gunban.getBlackVertical();
                    float x_judgeline_down_black_gunban3_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (47/1.3f + gunban.getCountSizeChange()) * 3 + ((47/1.3f + gunban.getCountSizeChange()) * 4 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 3) / 4;
                    float x_judgeline_down_black_gunban3_downright = x_judgeline_down_black_gunban3_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_judgeline_down_black_gunban3_upleft = y_piano_upleft + gunban.getBlackVertical() - 7;
                    float y_judgeline_down_black_gunban3_downright = y_piano_upleft + gunban.getBlackVertical();
                    float x_judgeline_down_black_gunban4_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (47/1.3f + gunban.getCountSizeChange()) * 3 + ((47/1.3f + gunban.getCountSizeChange()) * 4 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 3) / 4 * 2 + 23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange();
                    float x_judgeline_down_black_gunban4_downright = x_judgeline_down_black_gunban4_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_judgeline_down_black_gunban4_upleft = y_piano_upleft + gunban.getBlackVertical() - 7;
                    float y_judgeline_down_black_gunban4_downright = y_piano_upleft + gunban.getBlackVertical();
                    float x_judgeline_down_black_gunban5_upleft = x_piano_upleft + (47/1.3f + gunban.getCountSizeChange()) * 7 * i + (47/1.3f + gunban.getCountSizeChange()) * 3 + ((47/1.3f + gunban.getCountSizeChange()) * 4 - (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 3) / 4 * 3 + (23/1.3f + 23/1.3f / 47/1.3f * gunban.getCountSizeChange()) * 2;
                    float x_judgeline_down_black_gunban5_downright = x_judgeline_down_black_gunban5_upleft + 23/1.3f + 0.489361f * gunban.getCountSizeChange();
                    float y_judgeline_down_black_gunban5_upleft = y_piano_upleft + gunban.getBlackVertical() - 7;
                    float y_judgeline_down_black_gunban5_downright = y_piano_upleft + gunban.getBlackVertical();

                    canvas.drawRect(x_judgeline_up_black_gunban1_upleft, y_judgeline_up_black_gunban1_upleft, x_judgeline_up_black_gunban1_downright, y_judgeline_up_black_gunban1_downright, judge_line);
                    canvas.drawRect(x_judgeline_up_black_gunban2_upleft, y_judgeline_up_black_gunban2_upleft, x_judgeline_up_black_gunban2_downright, y_judgeline_up_black_gunban2_downright, judge_line);
                    canvas.drawRect(x_judgeline_up_black_gunban3_upleft, y_judgeline_up_black_gunban3_upleft, x_judgeline_up_black_gunban3_downright, y_judgeline_up_black_gunban3_downright, judge_line);
                    canvas.drawRect(x_judgeline_up_black_gunban4_upleft, y_judgeline_up_black_gunban4_upleft, x_judgeline_up_black_gunban4_downright, y_judgeline_up_black_gunban4_downright, judge_line);
                    canvas.drawRect(x_judgeline_up_black_gunban5_upleft, y_judgeline_up_black_gunban5_upleft, x_judgeline_up_black_gunban5_downright, y_judgeline_up_black_gunban5_downright, judge_line);
                    canvas.drawRect(x_judgeline_down_black_gunban1_upleft, y_judgeline_down_black_gunban1_upleft, x_judgeline_down_black_gunban1_downright, y_judgeline_down_black_gunban1_downright, judge_line);
                    canvas.drawRect(x_judgeline_down_black_gunban2_upleft, y_judgeline_down_black_gunban2_upleft, x_judgeline_down_black_gunban2_downright, y_judgeline_down_black_gunban2_downright, judge_line);
                    canvas.drawRect(x_judgeline_down_black_gunban3_upleft, y_judgeline_down_black_gunban3_upleft, x_judgeline_down_black_gunban3_downright, y_judgeline_down_black_gunban3_downright, judge_line);
                    canvas.drawRect(x_judgeline_down_black_gunban4_upleft, y_judgeline_down_black_gunban4_upleft, x_judgeline_down_black_gunban4_downright, y_judgeline_down_black_gunban4_downright, judge_line);
                    canvas.drawRect(x_judgeline_down_black_gunban5_upleft, y_judgeline_down_black_gunban5_upleft, x_judgeline_down_black_gunban5_downright, y_judgeline_down_black_gunban5_downright, judge_line);
                }

                holder.unlockCanvasAndPost(canvas);
            }
        }
    }
}