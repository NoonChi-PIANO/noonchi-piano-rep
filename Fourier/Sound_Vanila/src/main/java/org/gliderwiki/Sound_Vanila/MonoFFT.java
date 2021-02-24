package org.gliderwiki.Sound_Vanila;

import org.jtransforms.fft.DoubleFFT_1D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import ca.uol.aig.fftpack.RealDoubleFFT;

public class MonoFFT {

	static int sampleRate = 44100;

	public static double[] FFT_VA(File file) {

		double[] real_Wav_size = readWav.getWavData(file);

		double[] wav_to_fft = new double[sampleRate];

		for (int i = 0; i < real_Wav_size.length; i++) {
			wav_to_fft[i] = real_Wav_size[i];
		}

		int rf = (int) readWav.getWavFrames(file);
		int n = 44100; // mono type의 wav파일은 샘플링 레이트가 44100임 , stereo시 88200
		int c = (int) readWav.getWavChannels(file);

		System.out.println(n + "개의 프레임"); // 프레임 갯수까지는 맞게 나옴.
		System.out.println(rf + "개의 실제 프레임");
		System.out.println(c + "개의 채널 '모노'"); // 모노이기때문에 1개 채널 맞음

		// 아마 JTransform에는 인풋 아웃풋에 실수 허수가 들어가기 때문에 배열 사이즈가 그 2배를 가지는걸 준비해야한다고함

		// DoubleFFT_1D fft = new DoubleFFT_1D(n); //프레임 n개를 가지고 수행

		// fft.complexForward(wav_to_fft); //허수부분은 리턴 안하는거로 알고있음

		double y[] = new double[n];
		for (int i = 0; i < n; i++) {
			y[i] = 0;
		}

		double[] summary = new double[2 * n];
		for (int k = 0; k < n; k++) {
			summary[2 * k] = wav_to_fft[k]; // 실수부
			summary[2 * k + 1] = y[k]; // 허수부 0으로 채워넣음.
		}

		DoubleFFT_1D fft = new DoubleFFT_1D(n);
		// 1차원의 fft 수행 44100개의 샘플레이트로 퓨리에변환수행
		fft.complexForward(summary);
		double[] mag = new double[n / 2];
		for (int k = 0; k < n / 2; k++) {
			mag[k] = Math.sqrt(Math.pow(summary[2 * k], 2) + Math.pow(summary[2 * k + 1], 2));
		}

		RealDoubleFFT fft2 = new RealDoubleFFT(n);
		fft2.ft(wav_to_fft);
		double[] mag2 = new double[n / 2];
		for (int k = 0; k < n / 2; k++) {
			mag2[k] = Math.sqrt(Math.pow(wav_to_fft[2 * k], 2) + Math.pow(wav_to_fft[2 * k + 1], 2));
		}

		return wav_to_fft;
	}

	public static void writeToFile2(double[] k, File out) throws IOException {
		PrintWriter print_out = new PrintWriter(out);

		for (int h = 0; h < k.length; h++) {
			print_out.println(k[h]);
		}

		print_out.close();

		// FFT된 배열을 입출력 하는 용도
	}

	// main method, solely for testing purposes
	public static void main(String[] args) {
		File fichier_son = new File("C:\\Users\\sangsu\\Desktop\\piano_DATA\\pianoSample_C4_1sec_mono.wav");

		//////////////////////////////////////////////////// 그냥 웨이브 파일 출력하는 것.
		double[] wav_to_data = readWav.getWavData(fichier_son);
		System.out.println("wav file sysout");

		try {
			PrintWriter print_out2 = new PrintWriter("C:\\Users\\sangsu\\Desktop\\piano_DATA\\pianoSampleWaVFile1.txt");

			for (int h = 0; h < wav_to_data.length; h++) {
				print_out2.println(wav_to_data[h]);
			}

			print_out2.close();

		} catch (FileNotFoundException e1) { // TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// 1sec data result = 8만개 가량 나옴. 스테레오라서 8만개인것으로 추정.
		// mono로 했을때 44100개 만큼 나옴

		//////////////////////////////////////////////////// 그냥 웨이브 파일 출력하는 것.

		double[] monoData = FFT_VA(fichier_son); // fft한 결과가 monoData에 포함

		for (int i = 0; i < monoData.length; i++) {

			if ((int) monoData[i] > 500) {
				System.out.println(i + "쯤에서 소리가 납니다");
			}

		}

		try {
			PrintWriter print_out2 = new PrintWriter(
					"C:\\Users\\sangsu\\Desktop\\piano_DATA\\pianoSampleWaVFile_mono_c2.txt");

			for (int h = 0; h < monoData.length; h++) {
				print_out2.println(monoData[h]);
			}

			print_out2.close();

		} catch (FileNotFoundException e1) { // TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
