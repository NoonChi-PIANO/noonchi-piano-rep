package org.gliderwiki.Sound_Vanila;
import org.jtransforms.fft.DoubleFFT_1D;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;

public class Merge {

    public static double[] realFFT(File file)
    {
        // Get the .wav data using the readWav class
        double[] data_to_fft = readWav.getWavData(file);

        /* Get the length of the array.
        Since we are feeding real numbers into the fft,
        the length of the array should be equal to the
        number of frames, which we get using the readWav class. */
        int n = (int) readWav.getWavFrames(file);

        // Make a new fft object
        DoubleFFT_1D fft = new DoubleFFT_1D(n);

        // Perform the realForward fft
        
        
      //  fft.realForward(data_to_fft);		// real -> 허수부 그냥 포함시킴 
        fft.complexForward(data_to_fft);   //comp -> 허수부 0으로 만듬
        

        // Return the final data
        return data_to_fft;
    }


    public static void writeToFile(File in, File out) throws IOException
    {
        PrintWriter print_out = new PrintWriter(out);
        int i;
        double[] data_to_file = realFFT(in); // wav 파일을 realFFT(wavFFT.java) 함수 처리후 data_to_file 배열에 삽입 

        for(i=0; i<data_to_file.length; i++){
			/*
			 * if(data_to_file[i] > 1){ print_out.println(data_to_file[i]); } else {
			 * print_out.println(0); }
			 */
        	
        	
        	print_out.println(data_to_file[i]);  //전체 부분 출력

        }
        
        for(i=0; i<data_to_file.length; i++){
            if(data_to_file[i] > 1){
                print_out.println(data_to_file[i]);
            } else {
                print_out.println(0);   //허수 부분은 0으로 출력하고  실수 부분만 출력을 함
            }

        }
        
        print_out.close();
    }
   

    // main method, solely for testing purposes
    public static void main(String[] args) {
        File fichier_son = new File("C:\\Users\\sangsu\\Desktop\\piano_DATA\\pianoSample_C4_1sec.wav");
        double[] test = realFFT(fichier_son); 
        int i;

		/*
		 * for(i=0; i<test.length; i++){ // if(test[i]>1) { System.out.println(test[i]);
		 * // } }
		 */
        
        //test[] 안에 FFT 한 사인파가 들어가있음. 보통 1초 44100 프레임, 스테레오라 176400 프레임. 0.1초로 나누려면 10으로 나눔
        //test[] 배열의 0~17640 만 퓨리에 변환 실시 하면 될 듯. 
        
        int N = 17640;
        double[] mag = new double[N/2];
        for(int k=0;k<N/2;k++){
            mag[k] = Math.sqrt(Math.pow(test[2*k],2)+Math.pow(test[2*k+1],2));
         }
        for(int m=0; m<512; m++) {
    	    System.out.println(m + " 주파수 : " + mag[m]);
       }
        


        try{
            writeToFile(fichier_son, new File("C:\\Users\\sangsu\\Desktop\\piano_DATA\\pianoSampleTxt_C4_1sec.txt"));
        } catch (IOException e){
            System.out.println("error");
        }
    }

}