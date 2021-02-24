package org.gliderwiki.Sound_Vanila;

import org.jtransforms.fft.DoubleFFT_1D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;

public class VanilaFFT {

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
        fft.realForward(data_to_fft);

        // Return the final data
        return data_to_fft;
    }
    
    public static double[] FFT_VA(File file) {
    	
    	double[] wav_to_fft = readWav.getWavData(file);  
    	int n = (int) readWav.getWavFrames(file);
    	DoubleFFT_1D fft = new DoubleFFT_1D(n);  //프레임 n개를 가지고 수행
    	
    	fft.complexForward(wav_to_fft); //허수부분은 리턴 안하는거로 알고있음
		
    	return wav_to_fft;
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
        
        //wav 파일을 FFT 후 입출력 하는 용도 
    }
    
    public static void writeToFile2(double[] k, File out) throws IOException {
    	PrintWriter print_out = new PrintWriter(out);
    	
    	for(int h = 0; h<k.length; h++ ) {
  		  print_out.println(k[h]); }
    	
    	print_out.close();
    	
    	//FFT된 배열을 입출력 하는 용도
    }
    
    
   

    // main method, solely for testing purposes
    public static void main(String[] args) {
        File fichier_son = new File("C:\\Users\\sangsu\\Desktop\\piano_DATA\\pianoSample_C4_1sec_mono.wav");
       
        ////////////////////////////////////////////////////
        double[] test = realFFT(fichier_son); 
        int i;
        
       

		/*
		 * double[] test_comp = FFT_VA(fichier_son); try { writeToFile2(test_comp, new
		 * File("C:\\Users\\sangsu\\Desktop\\piano_DATA\\pianoSampleCompleForward1.txt")
		 * ); System.out.println("wav_to_ComplexForwart_FFT complete"); } catch
		 * (IOException e2) { // TODO Auto-generated catch block e2.printStackTrace(); }
		 */
        
        
		/*
		 * for(i=0; i<test.length; i++){ // if(test[i]>1) { System.out.println(test[i]);
		 * // } }
		 */
        ////////////////////////////////////////////////////
        
        
        
		
		  ////////////////////////////////////////////////////그냥 웨이브 파일 출력하는 것.
		  double[] wav_to_data = readWav.getWavData(fichier_son);
		  System.out.println("wav file sysout");
		  
		  
		  try { PrintWriter print_out2 = new
		  PrintWriter("C:\\Users\\sangsu\\Desktop\\piano_DATA\\pianoSampleWaVFile1.txt"
		  );
		  
		  
		  for(int h = 0; h<wav_to_data.length; h++ ) {
		  print_out2.println(wav_to_data[h]); }
		  
		  
		  print_out2.close();
		  
		  } catch (FileNotFoundException e1) { // TODO Auto-generated catch block
		  e1.printStackTrace(); }
		  
		  // 1sec data result = 8만개 가량 나옴. 스테레오라서 8만개인것으로 추정.
		  
		  ////////////////////////////////////////////////////그냥 웨이브 파일 출력하는 것.
		  


			/*
			 * try{ writeToFile(fichier_son, new
			 * File("C:\\Users\\sangsu\\Desktop\\piano_DATA\\pianoSampleTxt_C4_1sec.txt"));
			 * //오디오 부분 만 출력.
			 * 
			 * } catch (IOException e){ System.out.println("error"); }
			 */
    }

}