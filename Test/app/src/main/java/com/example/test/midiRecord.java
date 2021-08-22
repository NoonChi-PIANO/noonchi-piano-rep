package com.example.test;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

import jp.kshoji.javax.sound.midi.MidiDevice;
import jp.kshoji.javax.sound.midi.MidiMessage;
import jp.kshoji.javax.sound.midi.MidiSystem;
import jp.kshoji.javax.sound.midi.MidiUnavailableException;
import jp.kshoji.javax.sound.midi.Receiver;
import jp.kshoji.javax.sound.midi.Transmitter;

public class midiRecord {
    Context mContext;
    public midiRecord(Context context){ mContext = context;}
    public boolean started = true;

    String mds1, mds2, mds3,mds4;


    public class RecordAudio extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            MidiDevice device;
            MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

            while(started){
                for (int i = 0; i < infos.length; i++) {
                    try {
                        device = MidiSystem.getMidiDevice(infos[i]);
                        // does the device have any transmitters?
                        // if it does, add it to the device list
                        System.out.println(infos[i]);

                        // get all transmitters
                        List<Transmitter> transmitters = device.getTransmitters();
                        // and for each transmitter

                        for (int j = 0; j < transmitters.size(); j++) {
                            // create a new receiver
                            transmitters.get(j).setReceiver(
                                    // using my own MidiInputReceiver
                                    new MidiInputReceiver(device.getDeviceInfo().toString()));
                        }

                        Transmitter trans = device.getTransmitter();
                        trans.setReceiver(new MidiInputReceiver(device.getDeviceInfo().toString()));

                        // open each device
                        device.open();
                        // if code gets this far without throwing an exception
                        // print a success message
                        System.out.println(device.getDeviceInfo() + " Was Opened");

                        publishProgress();
                    } catch (MidiUnavailableException e) {
                        Log.e("AudioRecord", "Recording Failed");
                    }
                }
            }
            return null;
        }
        // onProgressUpdate는 우리 엑티비티의 메인 스레드로 실행된다. 따라서 아무런 문제를 일으키지 않고 사용자 인터페이스와 상호작용할 수 있다.
        // 이번 구현에서는 onProgressUpdate가 FFT 객체를 통해 실행된 다음 데이터를 넘겨준다. 이 메소드는 최대 100픽셀의 높이로 일련의 세로선으로

        @Override
        protected void onProgressUpdate(Void...params) {

            //((TextView) ((Activity)mContext).findViewById(R.id.tx1)).setText(mds1);
           // ((TextView) ((Activity)mContext).findViewById(R.id.tx2)).setText(mds2);
            //((TextView) ((Activity)mContext).findViewById(R.id.tx3)).setText(mds3);
            //((TextView) ((Activity)mContext).findViewById(R.id.tx2)).setText(mds2);
            ((TextView) ((Activity)mContext).findViewById(R.id.HzText2)).setText(mds4);


        }
    }


    //tried to write my own class. I thought the send method handles an MidiEvents sent to it
    public class MidiInputReceiver implements Receiver {
        public String name;

        public MidiInputReceiver(String name) {
            this.name = name;
        }

        public void send(MidiMessage msg, long timeStamp) {

            byte[] aMsg = msg.getMessage();
            // take the MidiMessage msg and store it in a byte array

            //변수를 여기서 바꾸어주었으면 좋겠다 이말이죠.
            //aMsg[0] = velocity
            //aMsg[1] = note
            //aMsg[2] = pressed much

            mds1 = (String.valueOf(aMsg[0]));
            mds2 = (String.valueOf(aMsg[1]));
            mds3 = (String.valueOf(aMsg[2]));
            mds4 = whichScale2Midi(String.valueOf(aMsg[1]));



            System.out.println();
        }

        public void close() {
        }
    }

    public String whichScale2Midi(String scale){
        String result="";

        if(scale.equals("99999")){ return result = "C4"; }


        else if(scale.equals("48")){ return result = "C3";}else if(scale.equals("54")){ return result = "F#3";}
        else if(scale.equals("49")){ return result = "C#3";}else if(scale.equals("55")){ return result = "G3";}
        else if(scale.equals("50")){ return result = "D3";}else if(scale.equals("56")){ return result = "G#3";}
        else if(scale.equals("51")){ return result = "D#3";}else if(scale.equals("57")){ return result = "A3";}
        else if(scale.equals("52")){ return result = "E#3";}else if(scale.equals("58")){ return result = "A#3";}
        else if(scale.equals("53")){ return result = "F3";}else if(scale.equals("59")){ return result = "B#3";}

        else if(scale.equals("60")){ return result = "C4";}else if(scale.equals("66")){ return result = "F#4";}
        else if(scale.equals("61")){ return result = "C#4";}else if(scale.equals("67")){ return result = "G4";}
        else if(scale.equals("62")){ return result = "D4";}else if(scale.equals("68")){ return result = "G#4";}
        else if(scale.equals("63")){ return result = "D#4";}else if(scale.equals("69")){ return result = "A4";}
        else if(scale.equals("64")){ return result = "E#4";}else if(scale.equals("70")){ return result = "A#4";}
        else if(scale.equals("65")){ return result = "F4";}else if(scale.equals("71")){ return result = "B#4";}

        else if(scale.equals("72")){ return result = "C5";}else if(scale.equals("78")){ return result = "F#5";}
        else if(scale.equals("73")){ return result = "C#5";}else if(scale.equals("79")){ return result = "G5";}
        else if(scale.equals("74")){ return result = "D5";}else if(scale.equals("80")){ return result = "G#5";}
        else if(scale.equals("75")){ return result = "D#5";}else if(scale.equals("81")){ return result = "A5";}
        else if(scale.equals("76")){ return result = "E#5";}else if(scale.equals("82")){ return result = "A#5";}
        else if(scale.equals("77")){ return result = "F5";}else if(scale.equals("83")){ return result = "B#5";}




        return result;
    }


}