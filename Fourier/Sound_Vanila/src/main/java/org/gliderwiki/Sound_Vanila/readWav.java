package org.gliderwiki.Sound_Vanila;
import java.io.*;

public class readWav {

    // getter methods
    public static long getWavFrames(File file)
    {
        // try loop to catch any exception
        try {
            // open the wav file
            WavFile wavFile = WavFile.openWavFile(file);

            // return the number of frames
            return wavFile.getNumFrames();

        } catch (Exception e) {
            System.err.println(e);

            // error value
            return -1;
        }

    }

    public static int getWavChannels(File file)
    {
        // try loop to catch any exception
        try {
            WavFile wavFile = WavFile.openWavFile(file);

             return wavFile.getNumChannels();

        } catch (Exception e) {
            System.err.println(e);

            // error value
            return -1;
        }
    }

    public static double[] getWavData(File file)
    {
        // try loop to catch any exception
        try {
            // open the file
            WavFile wavFile = WavFile.openWavFile(file);

            // use the getter method to get the channel number (should be mono)
            int numChannels = getWavChannels(file);

            // same, but with the frame getter method
            int numFrames = (int) getWavFrames(file); // possible data loss

            // create a buffer the size of the number of frames
            double[] buffer = new double[numFrames * numChannels];

            // Read frames into buffer
            wavFile.readFrames(buffer, numFrames);

            // Close the wavFile
            wavFile.close();

            return buffer;

        } catch (Exception e) {
            System.err.println(e);

            // throw an error, if this runs something went wrong in reading the .wav file
            throw new RuntimeException("[could not read wav file " + file + "]");
        }
    }


    // main method, solely for testing purposes
    public static void main(String[] args)
    {
        // test, everything seems to be working
        File fichier_son = new File("C:\\Users\\sangsu\\Desktop\\piano_DATA\\pianoSample_C6.wav");
        double[] test = getWavData(fichier_son);
        for(int i = 0; i<test.length; i++){
            System.out.println(test[i]);
        }
    }

}