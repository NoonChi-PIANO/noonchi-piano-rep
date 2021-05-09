#include<Windows.h>
#include<iostream>
#include<winnt.h>
#include"opencv2/opencv.hpp"
#include"opencv2/highgui.hpp"
#include"Note.hpp"
#include<fstream>
#include<string>
using namespace cv;
using namespace std;

#define LINE_CONNECTION 2 //음계 색출 시 보정값

Mat image;
Mat subImage[10] = {}; //오선 배열 동적할당
double line_y[5] = {};
int linecheck = 0;
Note sheet_note[10][100];
int fn_number[20] = {};
Point2d note[10][100] = {};
int bottommost = 0;
int full_number = 0;

bool cmp(const Point2d& p1, const Point2d& p2) {
	if (p1.y > p2.y)
		return true;
	else {
		return false;
	}
}
bool cmp2(const Point2d& p1, const Point2d& p2) {
	if (p1.x< p2.x)
		return true;
	else {
		return false;
	}
}
void draw_houghLines(Mat src, Mat& dst, vector<Vec2f> lines, int nline)
{
	Point2d pt_sort[300] = {};
	Point2d pt_sort2[300] = {};
	size_t size = min((int)lines.size(), nline);
	//cvtColor(src, dst, COLOR_GRAY2BGR);
	for (size_t i = 0; i < size; i++)
	{
		float rho = lines[i][0], theta = lines[i][1];
		double a = cos(theta), b = sin(theta);
		Point2d pt(a * rho, b * rho);
		Point2d delta(1000 * -b, 100 * a);
	
		pt_sort[i] = pt + delta;
		pt_sort2[i] = pt - delta;
		
	}
	
	sort(pt_sort, pt_sort + 300, cmp);
	sort(pt_sort2, pt_sort2 + 300, cmp);
	int line_num = 4;
	for (int i = 0;i < size;i+=2) {
//		
		double pty = pt_sort[i].y - pt_sort[i + 1].y;
		
		
		for (int k = 0;k < pty-1;k++) {
			for (int j = 0;j < src.cols;j++) {
				if (dst.at<uchar>(Point2d(j, pt_sort[i].y-k)) == 255) {
					dst.at<uchar>(Point2d(j, pt_sort[i].y - 1-k)) = 255;
				}	
			}
		}
		line_y[line_num] = pt_sort[i].y - 1;
		//cout << line_y[line_num] << endl;
		line_num--;
		
	
	}
}
void divide_image(Mat binary_image) {
	int liner = 0;
	//int linecheck = 0;
	int linecheck2 = 0;
	int length = 0;
	int one[100];
	int five[100];
	
	int nWidth = binary_image.cols;
	int limit = (int)((nWidth / 100.0) * 70);
	int nHeight = binary_image.rows;

	for (int nY = 0;nY < nHeight; nY++) {
		PBYTE pbyData = (PBYTE)binary_image.data + nY * binary_image.step1();
		int count = 0;
		for (int nX = 0;nX < nWidth; nX++) {
			count += *pbyData++ == 0;
		}
		if (count >= limit) {

			liner++;
			if (liner % 5 == 1) {
				one[linecheck] = nY;
				linecheck++;
			}
			else if (liner > 0 && liner % 5 == 0) {
				five[linecheck2] = nY;
				linecheck2++;
			}
			nY = nY + 1;
		}
	}

	for (int i = 0;i < linecheck;i++) {
		Rect rect[100];
		int init = (five[i] + one[i + 1]) / 2;

		rect[i] = Rect(Point(0, one[i] - 30), Size(binary_image.cols, five[i] - one[i] + 60));
		//cout << rect[i] << endl;
		//cout << one[i] << endl;
		//cout << five[i] << endl;

		subImage[i] = binary_image(rect[i]);
		imshow("i", subImage[i]);
	}
	
}
void divide_by_four() {
	int k = 10;
	Rect rect[100] = {};
	Mat houghImage[4] = {};
	imshow("소절 분할 이미지", subImage[0]);

	vector<Vec2f> line;
	for (int j = 0;j < linecheck;j++) {
		Mat canny;
		Mat cannImage[100] = {};
		int div = subImage[j].cols / 4;
		for (int i = 0;i < 4;i++) {
			
			Mat canny;
			rect[i] = Rect(Point(i * div, 0), Point((i + 1) * div, subImage[j].rows));

			houghImage[i] = subImage[j](rect[i]);

			Canny(houghImage[i], canny, 0, 255, 7, true);
			
			HoughLines(canny, line, 1, (CV_PI / 180), 10);
			draw_houghLines(canny, houghImage[i], line, 10);
			if (j == 0) {
				imshow("4분할 이미지" + to_string(i), houghImage[i]);
			}
			

		}
		imshow(to_string(j), subImage[j]);
	}
	
	
}
void find_beat() {
	Mat quater_h, quater_l;
	Mat half_h, half_l;
	Mat eighth_l, eighth_h;
	Mat dotted_quater_l, dotted_quater_h;
	Mat sixteenth_l, sixteenth_h;
	Mat dotted_eighth_l, dotted_eighth_h;
	Mat dotted_half_l, dotted_half_h;
	double min, max;
	Mat coeff;
	Point left_top;
	

	int Tolerance =4; //오차 허용 값
	dotted_half_l = imread("image/dotted_half_l.png", IMREAD_GRAYSCALE);
	CV_Assert(dotted_half_l.data);
	threshold(dotted_half_l, dotted_half_l, 127, 255, THRESH_BINARY | THRESH_OTSU);

	dotted_quater_l = imread("image/DOTTED_QUATER_L.png", IMREAD_GRAYSCALE);
	CV_Assert(dotted_quater_l.data);
	threshold(dotted_quater_l, dotted_quater_l, 127, 255, THRESH_BINARY | THRESH_OTSU);


	dotted_quater_h = imread("image/DOTTED_QUATER_H.png", IMREAD_GRAYSCALE);
	CV_Assert(dotted_quater_h.data);
	threshold(dotted_quater_h, dotted_quater_h, 127, 255, THRESH_BINARY | THRESH_OTSU);

	dotted_eighth_l = imread("image/DOTTED_EIGHTH_L.png", IMREAD_GRAYSCALE);
	CV_Assert(dotted_eighth_l.data);
	threshold(dotted_eighth_l, dotted_eighth_l, 127, 255, THRESH_BINARY | THRESH_OTSU);

	dotted_eighth_h = imread("image/DOTTED_EIGHTH_H.png", IMREAD_GRAYSCALE);
	CV_Assert(dotted_eighth_h.data);
	threshold(dotted_eighth_h, dotted_eighth_h, 127, 255, THRESH_BINARY | THRESH_OTSU);



	quater_h = imread("image/QUATER_H.png", IMREAD_GRAYSCALE);
	CV_Assert(quater_h.data);
	threshold(quater_h, quater_h, 127, 255, THRESH_BINARY | THRESH_OTSU);

	quater_l = imread("image/QUATER_L.png", IMREAD_GRAYSCALE);
	CV_Assert(quater_l.data);
	threshold(quater_l, quater_l, 127, 255, THRESH_BINARY | THRESH_OTSU);
	
	half_h = imread("image/HALF_H.png", IMREAD_GRAYSCALE);
	CV_Assert(half_h.data);
	threshold(half_h, half_h, 127, 255, THRESH_BINARY | THRESH_OTSU);
	
	half_l = imread("image/HALF_L.png", IMREAD_GRAYSCALE);
	CV_Assert(half_l.data);
	threshold(half_l, half_l, 127, 255, THRESH_BINARY | THRESH_OTSU);

	eighth_l = imread("image/EIGHTH_L.png", IMREAD_GRAYSCALE);
	CV_Assert(eighth_l.data);
	threshold(eighth_l, eighth_l, 127, 255, THRESH_BINARY | THRESH_OTSU);

	eighth_h = imread("image/EIGHTH_H.png", IMREAD_GRAYSCALE);
	CV_Assert(eighth_h.data);
	threshold(eighth_h, eighth_h, 127, 255, THRESH_BINARY | THRESH_OTSU);

	sixteenth_h = imread("image/SIXTEENTH_H.png", IMREAD_GRAYSCALE);
	CV_Assert(sixteenth_h.data);
	threshold(sixteenth_h, sixteenth_h, 127, 255, THRESH_BINARY | THRESH_OTSU);

	sixteenth_l = imread("image/SIXTEENTH_L.png", IMREAD_GRAYSCALE);
	CV_Assert(sixteenth_l.data);
	threshold(sixteenth_l, sixteenth_l, 127, 255, THRESH_BINARY | THRESH_OTSU);
	

	
	for (int i = 0;i < linecheck;i++) {
		Mat clone = subImage[i];
		int beat_x;
		imshow("title", clone);
		int left_thres=7;
		int right_thres=7;
		int dot_x;


		Mat dot = imread("image/dot.png", IMREAD_GRAYSCALE);
		CV_Assert(dot.data);
		threshold(dot, dot, 127, 255, THRESH_BINARY | THRESH_OTSU);

	

		
		//깃발이 있는 악표를 검출하기 위한 알고리즘
		for (int j = 0;j < fn_number[i];j++) {
			int flag_check[3] = {0,0,0};
			int overlap = 0;
			bool is_dotted = false;
			
			bool is_flag = false;
		
			//점 부터 검출.
			for (int k = 0;k < 1;k++) {
				matchTemplate(clone, dot, coeff, TM_CCOEFF_NORMED);

				minMaxLoc(coeff, &min, &max, NULL, &left_top);
				if (max > 0.90) {
					k--;
					rectangle(clone, Rect(left_top, Point(left_top.x + dot.cols, left_top.y + dot.rows)), 255, -1, LINE_8);
					dot_x = left_top.x + dot.cols / 2;
					for (int j = 0;j < fn_number[i];j++) {
						if (dot_x < note[i][j].x + 10 && note[i][j].x < dot_x) {
							is_dotted = true;
							sheet_note[i][j].isDot();
						}
					}
				}
			}
			

			for (int k = 1;k < clone.rows - 7;k++) {
				if (clone.at<uchar>(Point(note[i][j].x,k))==0 && clone.at<uchar>(Point(note[i][j].x,k+1))==0 && clone.at<uchar>(Point(note[i][j].x,k+2))==0) {
					if (k<note[i][j].y - 5 || k>note[i][j].y + 5) {

						clone.at<uchar>(Point(note[i][j].x, k + 1)) = 255;
						clone.at<uchar>(Point(note[i][j].x, k + 2)) = 255;
						clone.at<uchar>(Point(note[i][j].x, k + 3)) = 255;
						flag_check[0]++;
						is_flag = true;
						
					}
				}
				else if (clone.at<uchar>(Point(note[i][j].x+right_thres, k)) == 0 && clone.at<uchar>(Point(note[i][j].x + right_thres, k + 1)) == 0 && clone.at<uchar>(Point(note[i][j].x + right_thres, k + 2)) == 0) {
					if (k<note[i][j].y - 5 || k>note[i][j].y + 5) {
						
						
							clone.at<uchar>(Point(note[i][j].x + right_thres, k + 1)) = 255;
							clone.at<uchar>(Point(note[i][j].x + right_thres, k + 2)) = 255;
							clone.at<uchar>(Point(note[i][j].x + right_thres, k + 3)) = 255;
						
							flag_check[1]++;
						
							is_flag = true;
						
					}
				}
				
				else if (clone.at<uchar>(Point(note[i][j].x - left_thres, k)) == 0 && clone.at<uchar>(Point(note[i][j].x - left_thres, k + 1)) == 0 && clone.at<uchar>(Point(note[i][j].x - left_thres, k + 2)) == 0) {
					if (k<note[i][j].y - 5 || k>note[i][j].y + 5) {
						
							clone.at<uchar>(Point(note[i][j].x - left_thres, k + 1)) = 255;
							clone.at<uchar>(Point(note[i][j].x -left_thres, k + 2)) = 255;
							clone.at<uchar>(Point(note[i][j].x - left_thres, k + 3)) = 255;

							flag_check[2]++;
							is_flag = true;
					}
				}

				
					
				
				
			}
			sort(flag_check, flag_check + 3);
			overlap = flag_check[2];
			if (overlap == 1) {
				
					sheet_note[i][j].setBeat(80);
				
				
				rectangle(clone, Rect(Point(note[i][j].x - 4, 1), Point(note[i][j].x + 4, clone.rows - 7)), 255, -1, LINE_8);
			}
			else if (overlap == 2) {
				
					sheet_note[i][j].setBeat(40);
				
				
				rectangle(clone, Rect(Point(note[i][j].x - 4, 1), Point(note[i][j].x + 4, clone.rows - 7)), 255, -1, LINE_8);
			}
			else if (overlap == 3) {
				
					sheet_note[i][j].setBeat(20);
				
				
				rectangle(clone, Rect(Point(note[i][j].x - 4, 1), Point(note[i][j].x + 4, clone.rows - 7)), 255, -1, LINE_8);
			}
			else if (overlap > 3) {
				
					sheet_note[i][j].setBeat(10);
				
				
			}
		}
		
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, dotted_quater_l, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.95) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + dotted_quater_l.cols, left_top.y + dotted_quater_l.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + dotted_quater_l.cols / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						sheet_note[i][j].setBeat(240);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + dotted_quater_l.cols / 2 << endl;
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, dotted_quater_h, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.95) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + dotted_quater_h.cols, left_top.y + dotted_quater_h.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + dotted_quater_h.cols / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						sheet_note[i][j].setBeat(240);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + dotted_quater_h.cols / 2 << endl;
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, dotted_eighth_l, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.97) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + dotted_eighth_l.cols, left_top.y + dotted_eighth_l.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + dotted_eighth_l.cols / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						sheet_note[i][j].setBeat(120);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + dotted_eighth_l.cols / 2 << endl;
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, dotted_eighth_h, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.97) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + dotted_eighth_h.cols, left_top.y + dotted_eighth_h.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + dotted_eighth_h.cols / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						sheet_note[i][j].setBeat(120);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + dotted_eighth_h.cols / 2 << endl;
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, dotted_half_l, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.97) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + dotted_half_l.cols, left_top.y + dotted_half_l.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + dotted_half_l.cols / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						sheet_note[i][j].setBeat(480);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + dotted_half_l.cols / 2 << endl;
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, sixteenth_h, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.95) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + sixteenth_h.cols, left_top.y + sixteenth_h.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + sixteenth_h.cols / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						sheet_note[i][j].setBeat(40);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + sixteenth_h.cols / 2 << endl;
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, sixteenth_l, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.95) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + sixteenth_l.cols, left_top.y + sixteenth_l.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + sixteenth_l.cols / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance -2 < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						sheet_note[i][j].setBeat(40);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + sixteenth_l.cols / 2 << endl;
			}
		}
		
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, eighth_l, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.90) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + eighth_l.cols, left_top.y + eighth_l.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + (quater_h.cols) / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						sheet_note[i][j].setBeat(80);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + quater_h.cols / 2 << endl;
			}
		}
		
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, eighth_h, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.90) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + eighth_h.cols, left_top.y + eighth_h.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + (quater_h.cols) / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						sheet_note[i][j].setBeat(80);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + quater_h.cols / 2 << endl;
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, quater_h, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.95) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + quater_h.cols, left_top.y + quater_h.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + quater_h.cols / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						sheet_note[i][j].setBeat(160);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + quater_h.cols / 2 << endl;
			}
		}

		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, quater_l, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.95) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + quater_l.cols, left_top.y + quater_l.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + (quater_h.cols) / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						sheet_note[i][j].setBeat(160);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + quater_h.cols / 2 << endl;
			}
		}

		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, half_l, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.90) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + half_l.cols, left_top.y + half_l.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + quater_h.cols / 2;
				for (int j = 0;j <fn_number[i];j++) {
					if ( beat_x - Tolerance < note[i][j].x && note[i][j].x  < beat_x + Tolerance) {
						//cout <<"note배열 x좌표"<< note[i][j].x << endl;
						sheet_note[i][j].setBeat(320);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + quater_h.cols / 2 << endl;
			}
		}
		
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, half_h, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.90) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + half_h.cols, left_top.y + half_h.rows)), 0, 1, LINE_8);
				beat_x = left_top.x + quater_h.cols / 2;
				for (int j = 0;j < fn_number[i];j++) {
					if (beat_x - Tolerance < note[i][j].x && note[i][j].x < beat_x + Tolerance) {
						//cout <<"note배열 x좌표"<< note[i][j].x << endl;
						sheet_note[i][j].setBeat(320);
						//sheet_note[i][j].getNote();
					}
				}
				//cout << left_top.x + quater_h.cols / 2 << endl;
			}
		}


		for (int j = 0;j < fn_number[i];j++) {
			sheet_note[i][j].getNote();
		}
		cout << endl;
	}//라인 별 반복
	

}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//






void find_scale() { //부분적 템플릿 매칭 -> 좌표 찾아 음계 찾기
	Mat temp, temp2,temp3,temp4;
	Mat g_clef, c_clef;
	Mat quater_rest;
	Mat eighth_rest;
	Mat sharp;
	Mat flat;
	double min, max;
	Point left_top;
	Mat coeff;
	
	Mat C,G7,F,C7,D7,Am;
	int clef = 1;
	
	Mat short_slur;
	Mat long_slur;



	//templete image load
	temp = imread("image/1.png", IMREAD_GRAYSCALE);
	CV_Assert(temp.data);
	threshold(temp, temp, 127, 255, THRESH_BINARY);

	temp2 = imread("image/2.png", IMREAD_GRAYSCALE);
	CV_Assert(temp2.data);
	threshold(temp2, temp2, 127, 255, THRESH_BINARY);

	temp3 = imread("image/3.png", IMREAD_GRAYSCALE);
	CV_Assert(temp3.data);
	threshold(temp3, temp3, 127, 255, THRESH_BINARY);

	temp4 = imread("image/4.png", IMREAD_GRAYSCALE);
	CV_Assert(temp4.data);
	threshold(temp4, temp4, 127, 255, THRESH_BINARY);

	g_clef = imread("image/G_ClEF.png", IMREAD_GRAYSCALE);
	CV_Assert(g_clef.data);
	threshold(g_clef, g_clef, 127, 255, THRESH_BINARY | THRESH_OTSU);

	c_clef = imread("image/C_Clef.jpg", IMREAD_GRAYSCALE);
	CV_Assert(c_clef.data);
	threshold(c_clef, c_clef, 127, 255, THRESH_BINARY | THRESH_OTSU);

	quater_rest = imread("image/QUATER_REST.png", IMREAD_GRAYSCALE);
	CV_Assert(quater_rest.data);
	threshold(quater_rest, quater_rest, 127, 255, THRESH_BINARY | THRESH_OTSU);

	eighth_rest = imread("image/EIGHTH_REST.png", IMREAD_GRAYSCALE);
	CV_Assert(eighth_rest.data);
	threshold(eighth_rest, eighth_rest, 127, 255, THRESH_BINARY | THRESH_OTSU);
	
	sharp = imread("image/SHARP.png", IMREAD_GRAYSCALE);
	CV_Assert(sharp.data);
	threshold(sharp, sharp, 127, 255, THRESH_BINARY);

	flat = imread("image/FLAT.png", IMREAD_GRAYSCALE);
	CV_Assert(flat.data);
	threshold(flat, flat, 127, 255, THRESH_BINARY);

	F = imread("image/F.png", IMREAD_GRAYSCALE);
	CV_Assert(F.data);
	threshold(F, F, 127, 255, THRESH_BINARY);

	C7 = imread("image/C7.png", IMREAD_GRAYSCALE);
	CV_Assert(C7.data);
	threshold(C7, C7, 127, 255, THRESH_BINARY);

	C = imread("image/C.png", IMREAD_GRAYSCALE);
	CV_Assert(C.data);
	threshold(C, C, 127, 255, THRESH_BINARY);

	G7 = imread("image/G7.png", IMREAD_GRAYSCALE);
	CV_Assert(G7.data);
	threshold(G7, G7, 127, 255, THRESH_BINARY);

	D7 = imread("image/D7.png", IMREAD_GRAYSCALE);
	CV_Assert(D7.data);
	threshold(D7, D7, 127, 255, THRESH_BINARY);

	Am = imread("image/Am.png", IMREAD_GRAYSCALE);
	CV_Assert(Am.data);
	threshold(Am, Am, 127, 255, THRESH_BINARY);

	short_slur = imread("image/short_slur.png", IMREAD_GRAYSCALE);
	CV_Assert(short_slur.data);
	threshold(short_slur, short_slur, 127, 255, THRESH_BINARY);

	long_slur = imread("image/long_slur.png", IMREAD_GRAYSCALE);
	CV_Assert(long_slur.data);
	threshold(long_slur, long_slur, 127, 255, THRESH_BINARY);

	cout << "가로길이"<< temp.cols << endl;
	cout << "세로길이" << temp.rows << endl;

	for (int i = 0;i < linecheck;i++) {
		Mat clone = subImage[i].clone();
		int note_number = 0;
		int clef_x;

		//높은음자리표/낮은음자리표 탐색
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, g_clef, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.40) {
				rectangle(clone, Rect(left_top, Point(left_top.x + g_clef.cols, left_top.y + g_clef.rows)), 0, 1, LINE_8);
				clef_x = left_top.x + g_clef.cols / 2;
				clef = 1;
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, c_clef, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.40) {
				rectangle(clone, Rect(left_top, Point(left_top.x + c_clef.cols, left_top.y + c_clef.rows)), 0, 1, LINE_8);
				clef_x = left_top.x + c_clef.cols / 2;
				clef = 0;
			}
		}


		//계이름 탐색
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, temp, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.65) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + temp.cols, left_top.y + temp.rows)), 0, 1, LINE_8);
				note[i][note_number].x = left_top.x + temp2.cols / 2;
				note[i][note_number].y = (left_top.y + temp.rows / 2) - LINE_CONNECTION;
				note_number++;
				if (bottommost < left_top.y + temp.rows) {
					bottommost = left_top.y + temp.rows;
				}
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, temp2, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.65) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + temp2.cols, left_top.y + temp2.rows)), 0, 1, LINE_8);
				note[i][note_number].x = left_top.x + temp2.cols / 2;
				note[i][note_number].y = (left_top.y + temp2.rows / 2) - LINE_CONNECTION;
				note_number++;
				if (bottommost < left_top.y + temp.rows) {
					bottommost = left_top.y + temp.rows;
				}
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, temp4, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.65) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + temp4.cols, left_top.y + temp4.rows)), 0, 1, LINE_8);
				note[i][note_number].x = left_top.x + temp4.cols / 2;
				note[i][note_number].y = (left_top.y + temp4.rows / 2) - LINE_CONNECTION;
				note_number++;
				if (bottommost < left_top.y + temp.rows) {
					bottommost = left_top.y + temp.rows;
				}
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, temp3, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.65) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + temp3.cols, left_top.y + temp3.rows)), 0, 1, LINE_8);
				note[i][note_number].x = left_top.x + temp3.cols / 2;
				note[i][note_number].y = (left_top.y + temp3.rows / 2) - LINE_CONNECTION;
				note_number++;

			}
		}
		//쉼표 탐색
		for (int k = 0;k < 1;k++) {
			matchTemplate(subImage[i], eighth_rest, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.90) {
				k--;
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + eighth_rest.cols, left_top.y + eighth_rest.rows)), 255, -1, LINE_8);
				note[i][note_number].x = left_top.x + eighth_rest.cols / 2;
				note[i][note_number].y = -8;
				note_number++;
			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(subImage[i], quater_rest, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.90) {
				k--;
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + quater_rest.cols, left_top.y + quater_rest.rows)), 255, -1, LINE_8);
				note[i][note_number].x = left_top.x + quater_rest.cols / 2;
				note[i][note_number].y = -4;
				note_number++;
			}
		}

		//조 탐색.(# :: 파도솔레라미시. 다른거 :: 시미라레솔도파)
		int key = 0;
		int f_key = 0;
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, sharp, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.90) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + sharp.cols, left_top.y + sharp.rows)), 255, -1, LINE_8);
				if (clef_x < left_top.x && left_top.x < note[i][0].x) {
					key++;
				}

			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(clone, flat, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.90) {
				k--;
				rectangle(clone, Rect(left_top, Point(left_top.x + flat.cols, left_top.y + flat.rows)), 255, -1, LINE_8);
				if (clef_x < left_top.x && left_top.x < note[i][0].x) {
					f_key++;
				}

			}
		}
		//쓸데없는 기호 지워버리기(F, C7 등등..)
		
		for (int k = 0;k < 1;k++) {
			matchTemplate(subImage[i], F, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.80) {
				k--;
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + F.cols, left_top.y + F.rows)), 255, -1, LINE_8);

			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(subImage[i], C7, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.80) {
				k--;
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + C7.cols, left_top.y + C7.rows)), 255, -1, LINE_8);

			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(subImage[i], G7, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.80) {
				k--;
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + G7.cols, left_top.y + G7.rows)), 255, -1, LINE_8);

			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(subImage[i], C, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.80) {
				k--;
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + C.cols, left_top.y + C.rows)), 255, -1, LINE_8);

			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(subImage[i], D7, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.80) {
				k--;
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + D7.cols, left_top.y + D7.rows)), 255, -1, LINE_8);

			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(subImage[i], Am, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.80) {
				k--;
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + Am.cols, left_top.y + Am.rows)), 255, -1, LINE_8);

			}
		}
		
		for (int k = 0;k < 1;k++) {
			matchTemplate(subImage[i], long_slur, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.80) {
				k--;
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + long_slur.cols, left_top.y + long_slur.rows)), 255, -1, LINE_8);

			}
		}
		for (int k = 0;k < 1;k++) {
			matchTemplate(subImage[i], short_slur, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.80) {
				k--;
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + short_slur.cols, left_top.y + short_slur.rows)), 255, -1, LINE_8);

			}
		}
		


		sort(note[i], note[i] + note_number, cmp2);
		for (int j = 0;j < note_number;j++) { //중복 제거
			if ((note[i][j].x - note[i][j - 1].x) < 3) {
				memmove(note[i] + j, note[i] + j + 1, sizeof(note[i]) - j);
				note_number--;
				j--;
			}
		}
		full_number += note_number;
		fn_number[i] = note_number;
		cout << endl;
		cout << " 음표 갯수" << fn_number[i];
		cout << endl;
		int Tolerance; //음표 중심좌표와 오선간 허용 오차 값

		double line_gap = line_y[1] - line_y[0]; //오선 사이 간격
		if (line_gap == 6) {
			Tolerance = 2; //음표 중심좌표와 오선간 허용 오차 값
		}
		else
			Tolerance = 3;

		if (clef == 1) {
			cout << "오른손" << endl;
			for (int j = 0;j < note_number;j++) {
				if (note[i][j].y > line_y[0] - line_gap + Tolerance && note[i][j].y < line_y[0] - 2 * line_gap - Tolerance) {
					if (f_key > 0) {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "시 플랫" << endl;
						sheet_note[i][j].setBlackNumber(5);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(5);
					}
					else {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "시" << endl;
						sheet_note[i][j].setWhiteNumber(7);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(5);
					}
				}
				else if (note[i][j].y < line_y[0] - line_gap + Tolerance && note[i][j].y > line_y[0] - line_gap - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "라" << endl;
					sheet_note[i][j].setWhiteNumber(6);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(5);
				}
				else if (note[i][j].y > line_y[0] - line_gap + Tolerance && note[i][j].y < line_y[0] - Tolerance) {
					if (key > 2) {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "솔 샾" << endl;
						sheet_note[i][j].setBlackNumber(4);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(5);
					}
					else {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "솔" << endl;
						sheet_note[i][j].setWhiteNumber(5);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(5);
					}

				}
				else if (note[i][j].y<line_y[0] + Tolerance && note[i][j].y>line_y[0] - Tolerance) {

					if (key > 0) {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "파 샾" << endl;
						sheet_note[i][j].setBlackNumber(3);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(5);
					}
					else {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "파" << endl;
						sheet_note[i][j].setWhiteNumber(4);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(5);
					}
				}
				else if (note[i][j].y > line_y[0] + Tolerance && note[i][j].y < line_y[1] - Tolerance) {
					if (f_key > 1) {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "미 플랫" << endl;
						sheet_note[i][j].setBlackNumber(2);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(5);
					}
					else {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "미" << endl;
						sheet_note[i][j].setWhiteNumber(3);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(5);
					}

				}
				else if (note[i][j].y<line_y[1] + Tolerance && note[i][j].y>line_y[1] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "레" << endl;
					sheet_note[i][j].setWhiteNumber(2);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(5);
				}
				else if (note[i][j].y > line_y[1] + Tolerance && note[i][j].y < line_y[2] - Tolerance) {

					if (key > 1) {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "도 샾" << endl;
						sheet_note[i][j].setBlackNumber(1);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(5);
					}
					else {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "도" << endl;
						sheet_note[i][j].setWhiteNumber(1);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(5);
					}
				}
				else if (note[i][j].y<line_y[2] + Tolerance && note[i][j].y>line_y[2] - Tolerance) {
					if (f_key > 0) {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "시 플랫" << endl;
						sheet_note[i][j].setBlackNumber(5);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(4);
					}
					else {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "시" << endl;
						sheet_note[i][j].setWhiteNumber(7);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(4);
					}

				}
				else if (note[i][j].y > line_y[2] + Tolerance && note[i][j].y < line_y[3] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "라" << endl;
					sheet_note[i][j].setWhiteNumber(6);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(4);
				}
				else if (note[i][j].y<line_y[3] + Tolerance && note[i][j].y>line_y[3] - Tolerance) {
					if (key > 2) {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "솔 샾" << endl;
						sheet_note[i][j].setBlackNumber(4);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(4);
					}
					else {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "솔" << endl;
						sheet_note[i][j].setWhiteNumber(5);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(4);
					}
				}
				else if (note[i][j].y > line_y[3] + Tolerance && note[i][j].y < line_y[4] - Tolerance) {

					if (key > 0) {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "파 샾" << endl;
						sheet_note[i][j].setBlackNumber(3);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(4);
					}
					else {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "파" << endl;
						sheet_note[i][j].setWhiteNumber(4);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(4);
					}

				}
				else if (note[i][j].y<line_y[4] + Tolerance && note[i][j].y>line_y[4] - Tolerance) {
					if (f_key > 1) {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "미 플랫" << endl;
						sheet_note[i][j].setBlackNumber(2);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(4);
					}
					else {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "미" << endl;
						sheet_note[i][j].setWhiteNumber(3);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(4);

					}
				}
				else if (note[i][j].y > line_y[4] + Tolerance && note[i][j].y < line_y[4] + line_gap - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "레" << endl;
					sheet_note[i][j].setWhiteNumber(2);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(4);
				}
				else if (note[i][j].y < line_y[4] + line_gap + Tolerance && note[i][j].y > line_y[4] + line_gap - Tolerance) {
					if (key > 1) {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "도 샾" << endl;
						sheet_note[i][j].setBlackNumber(1);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(4);
					}
					else {
						cout << note[i][j].x << "  " << note[i][j].y << "  " << "도" << endl;
						sheet_note[i][j].setWhiteNumber(1);
						sheet_note[i][j].setClef(1);
						sheet_note[i][j].setOctav(4);
					}

				}

				else if (note[i][j].y > line_y[4] + line_gap + Tolerance && note[i][j].y < line_y[4] + line_gap * 2 - Tolerance) {

					cout << note[i][j].x << "  " << note[i][j].y << "  " << "시" << endl;
					sheet_note[i][j].setWhiteNumber(7);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(3);
				}



				//쉼표 탐색
				else if (note[i][j].y == -8) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "8분 쉼표" << endl;
					sheet_note[i][j].setWhiteNumber(0);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(0);
					sheet_note[i][j].setBeat(80);
				}
				else if (note[i][j].y == -4) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "4분 쉼표" << endl;
					sheet_note[i][j].setWhiteNumber(0);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(0);
					sheet_note[i][j].setBeat(160);
				}

				else {
					cout << note[i][j].x << "  " << note[i][j].y << endl;
				}
			}
		}
		else if (clef == 0) {
			cout << "왼손" << endl;
			for (int j = 0;j < note_number;j++) {
				if (note[i][j].y > line_y[0] - line_gap + Tolerance && note[i][j].y < line_y[0] - Tolerance * line_gap - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "레" << endl;
					sheet_note[i][j].setWhiteNumber(2);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(4);
				}
				else if (note[i][j].y < line_y[0] - line_gap + Tolerance && note[i][j].y > line_y[0] - line_gap - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "도" << endl;
					sheet_note[i][j].setWhiteNumber(1);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(4);
				}
				else if (note[i][j].y > line_y[0] - line_gap + Tolerance && note[i][j].y < line_y[0] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "시" << endl;
					sheet_note[i][j].setWhiteNumber(7);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(3);
				}
				else if (note[i][j].y<line_y[0] + Tolerance && note[i][j].y>line_y[0] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "라" << endl;
					sheet_note[i][j].setWhiteNumber(6);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(3);
				}
				else if (note[i][j].y > line_y[0] + Tolerance && note[i][j].y < line_y[1] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "솔" << endl;
					sheet_note[i][j].setWhiteNumber(5);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(3);
				}
				else if (note[i][j].y<line_y[1] + Tolerance && note[i][j].y>line_y[1] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "파" << endl;
					sheet_note[i][j].setWhiteNumber(4);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(3);
				}
				else if (note[i][j].y > line_y[1] + Tolerance && note[i][j].y < line_y[2] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "미" << endl;
					sheet_note[i][j].setWhiteNumber(3);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(3);
				}
				else if (note[i][j].y<line_y[2] + Tolerance && note[i][j].y>line_y[2] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "레" << endl;
					sheet_note[i][j].setWhiteNumber(2);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(3);
				}
				else if (note[i][j].y > line_y[2] + Tolerance && note[i][j].y < line_y[3] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "도" << endl;
					sheet_note[i][j].setWhiteNumber(1);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(3);
				}
				else if (note[i][j].y<line_y[3] + Tolerance && note[i][j].y>line_y[3] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "시" << endl;
					sheet_note[i][j].setWhiteNumber(7);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(2);
				}
				else if (note[i][j].y > line_y[3] + Tolerance && note[i][j].y < line_y[4] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "라" << endl;
					sheet_note[i][j].setWhiteNumber(6);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(2);
				}
				else if (note[i][j].y<line_y[4] + Tolerance && note[i][j].y>line_y[4] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "솔" << endl;
					sheet_note[i][j].setWhiteNumber(5);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(2);
				}
				else if (note[i][j].y > line_y[4] + Tolerance && note[i][j].y < line_y[4] + line_gap - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "파" << endl;
					sheet_note[i][j].setWhiteNumber(4);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(2);
				}
				else if (note[i][j].y < line_y[4] + line_gap + Tolerance && note[i][j].y > line_y[4] + line_gap - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "미" << endl;
					sheet_note[i][j].setWhiteNumber(3);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(2);
				}
				else if (note[i][j].y > line_y[4] + line_gap + Tolerance && note[i][j].y < line_y[4] + line_gap * 2 - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "레" << endl;
					sheet_note[i][j].setWhiteNumber(2);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(2);
				}

				//쉼표 인식
				else if (note[i][j].y == -8) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "8분 쉼표" << endl;
					sheet_note[i][j].setWhiteNumber(0);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(0);
					sheet_note[i][j].setBeat(80);
				}
				else if (note[i][j].y == -4) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "4분 쉼표" << endl;
					sheet_note[i][j].setWhiteNumber(0);
					sheet_note[i][j].setClef(0);
					sheet_note[i][j].setOctav(0);
					sheet_note[i][j].setBeat(160);
				}

				else {
					cout << note[i][j].x << "  " << note[i][j].y << endl;
				}
			}
		}

		cout << "1번째 선 좌표"<<line_y[0] << endl;
		cout << "2번째 선 좌표" << line_y[1] << endl;
		cout << "3번째 선 좌표" << line_y[2] << endl;
		cout << "4번째 선 좌표" << line_y[3] << endl;
		cout << "5번째 선 좌표" << line_y[4] << endl;
		
		imshow("clone" + i, clone);
	}
	
	
	
}

void CallBackFunc(int event, int x, int y, int flags, void* userdata)
{
	if (event == EVENT_LBUTTONDOWN)
	{
		cout << "왼쪽 마우스 버튼 클릭.. 좌표 = (" << x << ", " << y << ")" << endl;
	}
}
Mat drop_lyrics(Mat binary_image) {
	double min, max;
	Point left_top;
	Mat coeff;
	Mat temp = imread("image/곰.png", IMREAD_GRAYSCALE);
	Mat temp2 = imread("image/라.png", IMREAD_GRAYSCALE);
	CV_Assert(temp.data);
	threshold(temp, temp, 127, 255, THRESH_BINARY);

	CV_Assert(temp2.data);
	threshold(temp2, temp2, 127, 255, THRESH_BINARY);


	for (int k = 0;k < 1;k++) {
		matchTemplate(binary_image, temp, coeff, TM_CCOEFF_NORMED);

		minMaxLoc(coeff, &min, &max, NULL, &left_top);
		if (max > 0.50) {
			k--;
			rectangle(binary_image, Rect(Point(0,left_top.y), Point(binary_image.cols, left_top.y + temp.rows)), 255, -1, LINE_8);

		}
	}
	for (int k = 0;k < 1;k++) {
		matchTemplate(binary_image, temp2, coeff, TM_CCOEFF_NORMED);

		minMaxLoc(coeff, &min, &max, NULL, &left_top);
		if (max > 0.50) {
			k--;
			rectangle(binary_image, Rect(Point(0, left_top.y-1), Point(binary_image.cols, left_top.y + temp2.rows)), 255, -1, LINE_8);

		}
	}
	return binary_image;
}
int main() {

	Mat binary_image;

	image = imread("image/bair.png", IMREAD_GRAYSCALE);
	CV_Assert(image.data);


	threshold(image, binary_image, 127, 255,THRESH_BINARY | THRESH_OTSU);
	//adaptiveThreshold(image, binary_image,255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 7, 10);

	imshow("binary_image", binary_image);

	binary_image = drop_lyrics(binary_image);
	imshow("가사 제거", binary_image);
	divide_image(binary_image);
	divide_by_four();
	find_scale();
	find_beat();

	
	namedWindow("gray image", WINDOW_AUTOSIZE);

	//템플릿 이미지 추출(수동) >>
	//윈도우에 출력  
	
	imshow("gray image", binary_image);

	//윈도우에 콜백함수를 등록
	setMouseCallback("gray image", CallBackFunc, NULL);
	
	Mat A = binary_image(Rect(Point(304,500), Point(321, 517)));
	imwrite("라.png", A);

	Mat B = binary_image(Rect(Point(74, 297), Point(145, 311)));
	imwrite("long_slur.png", B);
	

	

imshow("original", image);
imshow("result", binary_image);

ofstream fout("note.txt", ios_base::out);
	
for (int i = 0; i < linecheck;i++) {
	for (int j = 0;j < fn_number[i];j++) {
		sheet_note[i][j].getNote();
		fout << sheet_note[i][j].printNote() << endl;
	}
}
fout.close();

cout << "총 음표 갯수 : " << full_number <<" 개"<< endl;



waitKey(0);
return 0;
}

