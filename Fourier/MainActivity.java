package com.example.sound_vanilla3;

import android.Manifest;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import org.jtransforms.fft.DoubleFFT_1D;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

import ca.uol.aig.fftpack.RealDoubleFFT;

// FFT(Fast Fourier Transform) DFT 알고리즘 : 데이터를 시간 기준(time base)에서 주파수 기준(frequency base)으로 바꾸는데 사용.

public class MainActivity extends Activity implements OnClickListener {

    int frequency = 8192; //주파수가 8192일 경우 4096 까지 측정이 가능함
    int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;

    private RealDoubleFFT transformer;
    int blockSize = 2048; // 1024개의 배열이 나옴. 배열 한 칸당 4hz의 범위를 포함하고 있음.
    DoubleFFT_1D fft = new DoubleFFT_1D(blockSize); //JTransform 라이브러리로 FFT 수행

    //frequency -> 측정 주파수 대역으로 퓨리에 변환 시 f/2 만큼의 크기의 주파수를 분석 할 수 있음.
    //blockSize -> 한 분기마다 측정하는 사이즈로 double 배열로 저장 시 , b/2 개의 배열이 나옴. f/b -> 배열 하나에 할당되는 주파수 범위로 8192/2048 -> 4Hz임

    Button startStopButton;

    boolean started = false;
    // RecordAudio는 여기에서 정의되는 내부 클래스로서 AsyncTask를 확장한다.
    RecordAudio recordTask;
    // Bitmap 이미지를 표시하기 위해 ImageView를 사용한다. 이 이미지는 현재 오디오 스트림에서 주파수들의 레벨을 나타낸다.
    // 이 레벨들을 그리려면 Bitmap에서 구성한 Canvas 객체와 Paint객체가 필요하다.

    ImageView imageView;
    Bitmap bitmap;
    Canvas canvas;
    Paint paint;

    BarChart chart;
    ArrayList xlabels = new ArrayList();
    ArrayList ylabels = new ArrayList();
    BarData data;

    TextView t0;
    TextView t1 ;
    TextView t2 ;



    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        setContentView(R.layout.activity_main);

        startStopButton = (Button) findViewById(R.id.StartStopButton);
        startStopButton.setOnClickListener(this);

        // RealDoubleFFT 클래스 컨스트럭터는 한번에 처리할 샘플들의 수를 받는다. 그리고 출력될 주파수 범위들의 수를 나타낸다.
        transformer = new RealDoubleFFT(blockSize);

        // ImageView 및 관련 객체 설정 부분
       // imageView = (ImageView) findViewById(R.id.ImageView01);
        bitmap = Bitmap.createBitmap((int) blockSize/2, (int) 200, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
      //  imageView.setImageBitmap(bitmap);


        t0 = (TextView)findViewById(R.id.HzText0);
        t1 = (TextView)findViewById(R.id.HzText1);
        t2 = (TextView)findViewById(R.id.HzText2);


        chart =(BarChart)findViewById(R.id.chart);
        YAxis leftYAxis = chart.getAxisLeft();
        leftYAxis.setAxisMaxValue((float)200);
        leftYAxis.setAxisMinValue(0);
        chart.getAxisRight().setEnabled(false);

        //chart 그리기
        int xChart=0;
        //x축 라벨 추가
        //4096 / 16 =256 씩 16칸으로 할거임
        for(int i=0; i<1024; i++){
            xlabels.add(Integer.toString(xChart));
            xChart=xChart+1;
        }

        ylabels.add(new BarEntry(2.2f,0));
        ylabels.add(new BarEntry(10f,512));
        ylabels.add(new BarEntry(63.f,800));
        ylabels.add(new BarEntry(70.f,900));

        BarDataSet barDataSet = new BarDataSet(ylabels,"Hz");
      //  chart.animateY(5000);
        data = new BarData(xlabels,barDataSet); //MPAndroidChart v3.1 에서 오류나서 다른 버전 사용
       // barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        chart.setData(data);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        //오디오 녹음을 사용할 것인지 권한 여부를 체크해주는 코드로, 없으면 동작 안됨! +) AndroidManifest에도 오디오 권한 부분 추가되있음
    }

    // 이 액티비티의 작업들은 대부분 RecordAudio라는 클래스에서 진행된다. 이 클래스는 AsyncTask를 확장한다.
    // AsyncTask를 사용하면 사용자 인터페이스를 멍하니 있게 하는 메소드들을 별도의 스레드로 실행한다.
    // doInBackground 메소드에 둘 수 있는 것이면 뭐든지 이런 식으로 실행할 수 있다.

    private class RecordAudio extends AsyncTask<Void, double[], Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                // AudioRecord를 설정하고 사용한다.
                int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
                AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
                // short로 이뤄진 배열인 buffer는 원시 PCM 샘플을 AudioRecord 객체에서 받는다.
                // double로 이뤄진 배열인 toTransform은 같은 데이터를 담지만 double 타입인데, FFT 클래스에서는 double타입이 필요해서이다.
                short[] buffer = new short[blockSize];
                double[] toTransform = new double[blockSize];
                double[] mag = new double[blockSize/2];

                audioRecord.startRecording();

                while (started) {
                    int bufferReadResult = audioRecord.read(buffer, 0, blockSize);

                    //FFT는 Double 형 데이터를 사용하므로 short로 읽은 데이터를 형변환 시켜줘야함. short / short.MAX_VALUE = double
                    for (int i = 0; i < blockSize && i < bufferReadResult; i++) {
                        toTransform[i] = (double) buffer[i] / Short.MAX_VALUE; // 부호 있는 16비트
                    }


                    //두개의 FFT 코드를 사용 잡음잡는것은 RealDoubleFFT가 훨씬 더 잘잡는다.
                    //RealDoubleFFT 부분
                    //transformer.ft(toTransform);

                    //-> JTransform 부분
                    //Jtransform 은 입력에 실수부 허수부가 들어가야하므로 허수부 임의로 0으로 채워서 생성해줌
                    double y[] = new double[blockSize];
                    for (int i = 0; i < blockSize; i++) {
                        y[i] = 0;
                    }
                    //실수 허수를 넣으므로 연산에는 blockSize의 2배인 배열 필요
                    double[] summary = new double[2 * blockSize];
                    for (int k = 0; k < blockSize; k++) {
                        summary[2 * k] = toTransform[k]; //실수부
                        summary[2 * k + 1] = y[k]; //허수부 0으로 채워넣음.
                    }
                  //  DoubleFFT_1D fft = new DoubleFFT_1D(blockSize);
                    fft.complexForward(summary);
                    for(int k=0;k<blockSize/2;k++){
                        mag[k] = Math.sqrt(Math.pow(summary[2*k],2)+Math.pow(summary[2*k+1],2));
                    }


                    // publishProgress를 호출하면 onProgressUpdate가 호출된다.
                    //publishProgress(toTransform);
                     publishProgress(mag);
                }
                audioRecord.stop();
            } catch (Throwable t) {
                Log.e("AudioRecord", "Recording Failed");
            }
            return null;
        }
        // onProgressUpdate는 우리 엑티비티의 메인 스레드로 실행된다. 따라서 아무런 문제를 일으키지 않고 사용자 인터페이스와 상호작용할 수 있다.
        // 이번 구현에서는 onProgressUpdate가 FFT 객체를 통해 실행된 다음 데이터를 넘겨준다. 이 메소드는 최대 100픽셀의 높이로 일련의 세로선으로

        @Override
        protected void onProgressUpdate(double[]... toTransform) {
           /* canvas.drawColor(Color.BLACK);
            for (int i = 0; i < toTransform[0].length; i++) {
                int x = i;
                int downy = (int) (100 - (toTransform[0][i] * 10));
                int upy = 100;
                canvas.drawLine(x, downy, x, upy, paint);
            }
            imageView.invalidate();*/

            xlabels.clear();
            ylabels.clear();

            int xChart=0;
            for(int i=0; i<toTransform[0].length; i++){
                xlabels.add(Integer.toString(xChart));
                xChart=xChart+1;
            }

            for(int i=0; i<toTransform[0].length; i++){
                ylabels.add(new BarEntry((float)toTransform[0][i],i));
            }


            double max = toTransform[0][0];
            int hz = 0;


          /*  for(int i=0; i<toTransform[0].length; i++){
                if(max<toTransform[0][i]){
                    max = toTransform[0][i];
                    hz = i;
                }
            }

            if(max>100) {
                t0.setText(Integer.toString(hz*4));
                t1.setText(Double.toString(max));
            }*/

            ArrayList<Integer> hzList = new ArrayList<Integer>();
            ArrayList<Double> hzSize = new ArrayList<Double>();
            for(int i=0; i<toTransform[0].length; i++){
                if(toTransform[0][i]>90){
                    hzList.add(i);
                    hzSize.add(toTransform[0][i]);
                }
            }

            Iterator iter = hzList.iterator();
            if(iter.hasNext()==true){
                t0.setText(Integer.toString(hzList.get(0)*4));
                t1.setText(Double.toString(hzSize.get(0)));
                t2.setText(whichScale(hzList.get(0)*4));
            }


            hzSize.clear();
            hzList.clear();

            /*for(int i=0; i<toTransform[0].length; i++){
                if((float)toTransform[0][i] > (float)90 ){
                    t0.setText(Integer.toString(i*4));
                    t1.setText(Double.toString(toTransform[0][i]));
                }
            }*/

            BarDataSet barDataSet = new BarDataSet(ylabels,"Hz");
            data = new BarData(xlabels,barDataSet);

            chart.setData(data);
            chart.invalidate();

        }
    }

    public String whichScale(int k){
        String scale="dib";

        if(k>250 && k<270 ){
            scale ="C4"; //도
        }else if(k>286 && k<302){
            scale = "D4"; //레
        }else if(k>320 && k<340)
        {
            scale = "E4"; //미
        } else if(k>378 && k<403){
            scale ="G4"; //솔
        }else if(k>427 && k<452){
            scale = "A4"; //라
        }
        else if(k>508&&k<538){
            scale ="C5"; //도
        }
        else{
            scale = "no";
        }

        return scale;
    }

    @Override
    public void onClick(View arg0) {
        if (started) {
            started = false;
            startStopButton.setText("Start");
            recordTask.cancel(true);
        } else {
            started = true;
            startStopButton.setText("Stop");
            recordTask = new RecordAudio();
            recordTask.execute();
        }
    }
}

