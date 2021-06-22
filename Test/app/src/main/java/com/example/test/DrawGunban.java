package com.example.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import static com.example.test.SetPianoOctaveActivity.gunban;

public class DrawGunban extends View {
    public static float x_piano_upleft = 30; //피아노위치 X값
    public static float y_piano_upleft = 284; //피아노위치 Y값

    public DrawGunban(Context context) {
        super(context);
    }

    public DrawGunban(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onDraw(Canvas canvas) {
        Paint white_gunban = new Paint();
        white_gunban.setColor(Color.YELLOW);
        Paint black_gunban = new Paint();
        black_gunban.setColor(Color.BLACK);

        super.onDraw(canvas);

        for(int i = 0; i < 7 * gunban.getOctave(); i++) {
            float x_white_gunban_upleft = x_piano_upleft + (47 + gunban.getCountSizeChange()) * i;
            float y_white_gunban_upleft = y_piano_upleft - 270;
            float x_white_gunban_downright = x_white_gunban_upleft + 47 + gunban.getCountSizeChange();
            float y_white_gunban_downright = y_piano_upleft + gunban.getWhiteVertical() - 270;

            canvas.drawRect(x_white_gunban_upleft, y_white_gunban_upleft, x_white_gunban_downright, y_white_gunban_downright, white_gunban);
        }

        for(int i = 0; i < gunban.getOctave(); i++) {
            float x_black_gunban1_upleft = x_piano_upleft + (47 + gunban.getCountSizeChange()) * 7 * i + (((47 + gunban.getCountSizeChange()) * 3) - (23 + 0.489361f * gunban.getCountSizeChange()) * 2) / 3;
            float x_black_gunban1_downright = x_black_gunban1_upleft + 23 + 0.489361f * gunban.getCountSizeChange();
            float y_black_gunban1_upleft = y_piano_upleft - 270;
            float y_black_gunban1_downright = y_piano_upleft + gunban.getBlackVertical() - 270;
            float x_black_gunban2_upleft = x_piano_upleft + (47 + gunban.getCountSizeChange()) * 7 * i + ((47 + gunban.getCountSizeChange()) * 3 - (23 + 0.489361f * gunban.getCountSizeChange()) * 2) / 3 * 2 + 23 + 0.489361f * gunban.getCountSizeChange();
            float x_black_gunban2_downright = x_black_gunban2_upleft + 23 + 0.489361f * gunban.getCountSizeChange();
            float y_black_gunban2_upleft = y_piano_upleft - 270;
            float y_black_gunban2_downright = y_piano_upleft + gunban.getBlackVertical() - 270;
            float x_black_gunban3_upleft = x_piano_upleft + (47 + gunban.getCountSizeChange()) * 7 * i + (47 + gunban.getCountSizeChange()) * 3 + ((47 + gunban.getCountSizeChange()) * 4 - (23 + 0.489361f * gunban.getCountSizeChange()) * 3) / 4;
            float x_black_gunban3_downright = x_black_gunban3_upleft + 23 + 0.489361f * gunban.getCountSizeChange();
            float y_black_gunban3_upleft = y_piano_upleft - 270;
            float y_black_gunban3_downright = y_piano_upleft + gunban.getBlackVertical() - 270;
            float x_black_gunban4_upleft = x_piano_upleft + (47 + gunban.getCountSizeChange()) * 7 * i + (47 + gunban.getCountSizeChange()) * 3 + ((47 + gunban.getCountSizeChange()) * 4 - (23 + 0.489361f * gunban.getCountSizeChange()) * 3) / 4 * 2 + 23 + 0.489361f * gunban.getCountSizeChange();
            float x_black_gunban4_downright = x_black_gunban4_upleft + 23 + 0.489361f * gunban.getCountSizeChange();
            float y_black_gunban4_upleft = y_piano_upleft - 270;
            float y_black_gunban4_downright = y_piano_upleft + gunban.getBlackVertical() - 270;
            float x_black_gunban5_upleft = x_piano_upleft + (47 + gunban.getCountSizeChange()) * 7 * i + (47 + gunban.getCountSizeChange()) * 3 + ((47 + gunban.getCountSizeChange()) * 4 - (23 + 0.489361f * gunban.getCountSizeChange()) * 3) / 4 * 3 + (23 + 0.489361f * gunban.getCountSizeChange()) * 2;
            float x_black_gunban5_downright = x_black_gunban5_upleft + 23 + 0.489361f * gunban.getCountSizeChange();
            float y_black_gunban5_upleft = y_piano_upleft - 270;
            float y_black_gunban5_downright = y_piano_upleft + gunban.getBlackVertical() - 270;

            canvas.drawRect(x_black_gunban1_upleft, y_black_gunban1_upleft, x_black_gunban1_downright, y_black_gunban1_downright, black_gunban);
            canvas.drawRect(x_black_gunban2_upleft, y_black_gunban2_upleft, x_black_gunban2_downright, y_black_gunban2_downright, black_gunban);
            canvas.drawRect(x_black_gunban3_upleft, y_black_gunban3_upleft, x_black_gunban3_downright, y_black_gunban3_downright, black_gunban);
            canvas.drawRect(x_black_gunban4_upleft, y_black_gunban4_upleft, x_black_gunban4_downright, y_black_gunban4_downright, black_gunban);
            canvas.drawRect(x_black_gunban5_upleft, y_black_gunban5_upleft, x_black_gunban5_downright, y_black_gunban5_downright, black_gunban);
        }
    }
}