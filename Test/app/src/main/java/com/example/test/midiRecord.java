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
    public boolean started = false;

    String mds1, mds2, mds3;


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

           // ((TextView) ((Activity)mContext).findViewById(R.id.tx1)).setText(mds1);
           // ((TextView) ((Activity)mContext).findViewById(R.id.tx2)).setText(mds2);
           // ((TextView) ((Activity)mContext).findViewById(R.id.tx3)).setText(mds3);


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

            System.out.println(aMsg[0]);



            // msg.getLength() returns the length of the message in bytes
			/*for (int i = 0; i < msg.getLength(); i++) {
				System.out.println(aMsg[i]);
				//메세지 모두 출력하는거임

				// aMsg[0] is something, velocity maybe? Not 100% sure.
				// aMsg[1] is the note value as an int. This is the important one.
				// aMsg[2] is pressed or not (0/100), it sends 100 when they key goes down,
				// and 0 when the key is back up again. With a better keyboard it could maybe
				// send continuous values between then for how quickly it's pressed?
				// I'm only using VMPK for testing on the go, so it's either
				// clicked or not.
			}*/
            System.out.println();
        }

        public void close() {
        }
    }


}
