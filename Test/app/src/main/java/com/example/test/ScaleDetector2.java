package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.jtransforms.fft.DoubleFFT_1D;

import java.util.ArrayList;
import java.util.Iterator;

import ca.uol.aig.fftpack.RealDoubleFFT;

public class ScaleDetector2 {


    Context mContext;
    public ScaleDetector2(Context context){ mContext = context;}

    //프로그램 실행 순서
    public int frequency = 8192; //주파수가 8192일 경우 4096 까지 측정이 가능함
    public int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
    public int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private RealDoubleFFT transformer;
    int blockSize = 2048; // 1024개의 배열이 나옴. 배열 한 칸당 4hz의 범위를 포함하고 있음.
    DoubleFFT_1D fft = new DoubleFFT_1D(blockSize); //JTransform 라이브러리로 FFT 수행

    //frequency -> 측정 주파수 대역으로 퓨리에 변환 시 f/2 만큼의 크기의 주파수를 분석 할 수 있음.
    //blockSize -> 한 분기마다 측정하는 사이즈로 double 배열로 저장 시 , b/2 개의 배열이 나옴. f/b -> 배열 하나에 할당되는 주파수 범위로 8192/2048 -> 4Hz임

    public boolean started = false;
    // RecordAudio는 여기에서 정의되는 내부 클래스로서 AsyncTask를 확장한다.
    // public RecordAudio recordTask;

    String scale2 ;


    public class RecordAudio extends AsyncTask<Void, double[], Void> {
        /* Context cContext;
         public RecordAudio(Context context){ cContext = context;}*/
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
                    transformer.ft(toTransform);
                    publishProgress(toTransform);
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
            ArrayList<Integer> hzList = new ArrayList<Integer>();
            ArrayList<Double> hzSize = new ArrayList<Double>();
            for(int i=0; i<toTransform[0].length; i++){
                if(toTransform[0][i]>33){
                    hzList.add(i);
                    hzSize.add(toTransform[0][i]);
                }
            }
            Iterator iter = hzList.iterator();
            if(iter.hasNext()==true){
                //((TextView) ((Activity)mContext).findViewById(R.id.HzText0)).setText(Integer.toString(hzList.get(0)*4)); //대역대
                //((TextView) ((Activity)mContext).findViewById(R.id.HzText1)).setText(Double.toString(hzSize.get(0)));// 소리크기
                //((TextView) ((Activity)mContext).findViewById(R.id.HzText2)).setText(whichScale(hzList.get(0)*4));      //음계
                ((TextView) ((Activity)mContext).findViewById(R.id.HzText2)).setText(whichScale2(toTransform));


                if(((TextView) ((Activity)mContext).findViewById(R.id.HzText2)).getText().toString().
                        equals(((TextView) ((Activity)mContext).findViewById(R.id.Right_Scale)).getText().toString())
                        ||
                        ((TextView) ((Activity)mContext).findViewById(R.id.HzText2)).getText().toString().
                                equals(((TextView) ((Activity)mContext).findViewById(R.id.Left_Scale)).getText().toString())
                ){

                    ((TextView) ((Activity)mContext).findViewById(R.id.isCorrect)).setText("correct");
                }
            }

            hzSize.clear();
            hzList.clear();


        }
    }
    public String whichScale2(double[]... toTransform){
        if(toTransform[0][111]>99999){

        }
        else if(toTransform[0][259]>55 ||toTransform[0][260]>55 || toTransform[0][261]>55  ){
            scale2 = "C4"; //도
        }
        else if(toTransform[0][293]>15 || toTransform[0][292]>30 || toTransform[0][294]>20 ||
                toTransform[0][295]>30 || toTransform[0][296]>30 ){
            scale2 = "D4"; //레
        }
        else if(toTransform[0][329]>50 ||toTransform[0][328]>50 || toTransform[0][330]>50  ){
            scale2 = "E4"; //미
        }
        else if(toTransform[0][349]>50 || toTransform[0][348]>50 || toTransform[0][347]>50 ||
                toTransform[0][346]>50 || toTransform[0][350]>50 || toTransform[0][351]>50 ){
            scale2 = "F4"; //파
        }
        else if(toTransform[0][391]>55 ||toTransform[0][390]>60 || toTransform[0][389]>60 ||
                toTransform[0][392]>60  ){
            scale2 = "G4"; //솔
        }
        else if(toTransform[0][440]>30 || toTransform[0][441]>30 || toTransform[0][442]>55 ||
                toTransform[0][438]>30 || toTransform[0][436]>55 || toTransform[0][437]>55){
            scale2 = "A4"; //라
        }
        else if(toTransform[0][493]>80 ||toTransform[0][494]>80 || toTransform[0][495]>80 ||
                toTransform[0][496]>80  ){
            scale2 = "B4"; //솔
        }

        else if(toTransform[0][523]>44 ||toTransform[0][524]>44 || toTransform[0][521]>44  ){
            scale2 = "C5";
        }
        else if(toTransform[0][587]>44 ||toTransform[0][588]>44 || toTransform[0][589]>44  ){
            scale2 = "D5";
        }
        else if(toTransform[0][660]>15 ||toTransform[0][659]>20 || toTransform[0][662]>20 ||
                toTransform[0][663]>20 ||toTransform[0][658]>15 || toTransform[0][657]>28 ){
            scale2 = "E5";
        }
        else if(toTransform[0][697]>60 ||toTransform[0][698]>60 ||  toTransform[0][699]>60 || toTransform[0][700]>60  ){
            scale2 = "F5";
        }
        else if(toTransform[0][783]>55 ||toTransform[0][784]>55 ){
            scale2 = "G5";
        }
        else if(toTransform[0][880]>60 ||toTransform[0][881]>60 || toTransform[0][882]>60 ){
            scale2 = "A5";
        }
        else if(toTransform[0][987]>33 ||toTransform[0][988]>33 || toTransform[0][989]>33 ){
            scale2 = "B5";
        }
        //3옥타브
        else if(toTransform[0][129]>18 ||toTransform[0][130]>18){
            scale2 = "C3";
        }

        else if(toTransform[0][145]>18 ||toTransform[0][144]>18 ||toTransform[0][146]>18 ){
            scale2 = "D3";
        }
        else if(toTransform[0][164]>18 ||toTransform[0][163]>18 ||toTransform[0][165]>18 ){
            scale2 = "E3";
        }
        else if(toTransform[0][174]>18 ||toTransform[0][173]>18 ||toTransform[0][175]>18 ){
            scale2 = "F3";
        }
        else if(toTransform[0][195]>18 ||toTransform[0][196]>18 ||toTransform[0][194]>18 ){
            scale2 = "G3";
        }
        else if(toTransform[0][220]>18 ||toTransform[0][221]>18 ||toTransform[0][119]>18 ){
            scale2 = "A3";
        } else if(toTransform[0][246]>18 ||toTransform[0][245]>18 ||toTransform[0][247]>18 ){
            scale2 = "B3";
        }
        else{ }
        return scale2;
    }
}
