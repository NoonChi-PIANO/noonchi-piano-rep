package org.gliderwiki.Sound_Vanila;

import java.io.File;

import org.apache.commons.math3.transform.FastFourierTransformer;
import org.jtransforms.fft.DoubleFFT_1D;

public class App 
{
	   
    public static void main( String[] args )
    {
    	
    	int N = 1024;  //sample 갯수; 
    	int freq1 = 24;  
    	int freq2 = 97;
    	double samplingCycle = 0.004; //250Hz
    	double amplitude = 1; //진폭
    	
        System.out.println( "FFT_SOUND_version_Vanila" );
        
        
        double y[] = new double[N]; //Imaginary Part
        for(int i=0;i<N;i++){
           y[i] = 0;
        }
         
        double x[] = new double[N]; //Real Part
        for(int i=0;i<N;i++){
           x[i] = amplitude*Math.sin(2*Math.PI*freq1*samplingCycle*i) + Math.sin(2*Math.PI*freq2*samplingCycle*i)
           + Math.sin(2*Math.PI*66*samplingCycle*i)
           ;
        }
         
        double[] a = new double[2*N]; //fft 수행할 배열 사이즈 2N
        for(int k=0;k<N;k++){
            a[2*k] = x[k];   //Re
            a[2*k+1] = y[k]; //Im
        }
         
        DoubleFFT_1D fft = new DoubleFFT_1D(N); //1차원의 fft 수행
        fft.complexForward(a); //a 배열에 output overwrite
         
        double[] mag = new double[N/2];
        for(int k=0;k<N/2;k++){
           mag[k] = Math.sqrt(Math.pow(a[2*k],2)+Math.pow(a[2*k+1],2)  );
        }
        
       for(int m=0; m<512; m++) {
    	    System.out.println(m/4 + " 주파수 : " + mag[m]);
       }
       

   
    }
    
    
    //for test app
    public static int sum(int a, int b) {
    	return a+b;
    }
}
