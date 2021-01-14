#include<Windows.h>
#include<iostream>

#include<winnt.h>
#include"opencv2/opencv.hpp"
#include"opencv2/highgui.hpp"
#include"Note.hpp"
using namespace cv;
using namespace std;

Mat image;
Mat subImage[10] = {}; //오선 배열 동적할당
double line_y[5] = {};
int linecheck = 0;

bool cmp(const Point2d& p1, const Point2d& p2) {
	if (p1.y > p2.y)
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
		cout << line_y[line_num] << endl;
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
	int limit = (int)((nWidth / 100.0) * 80);
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
		
	}
	
}
void divide_by_four() {
	int k = 10;
	Rect rect[100] = {};
	
	vector<Vec2f> line;
	for (int j = 0;j < linecheck;j++) {
		Mat canny;
		Mat cannImage[100] = {};
		int div = subImage[j].cols / 4;
		for (int i = 0;i < 4;i++) {
			Mat houghImage[4] = {};
			Mat canny;
			rect[i] = Rect(Point(i * div, 0), Point((i + 1) * div, subImage[j].rows));

			houghImage[i] = subImage[j](rect[i]);

			Canny(houghImage[i], canny, 0, 255, 7, true);
			
			HoughLines(canny, line, 1, (CV_PI / 180), 10);
			draw_houghLines(canny, houghImage[i], line, 10);

		}
		imshow(to_string(j), subImage[j]);
	}
}

void find_scale() { //부분적 템플릿 매칭 -> 좌표 찾아 음계 찾기
	Mat temp, temp2;
	Mat g_clef, c_clef;
	double min, max;
	Point left_top;
	Mat coeff;
	Mat coeff2;
	int clef = 1;
	double note_x[100];
	double note_y[100];
	int note_number = 0;

	//templete image load
	temp = imread("image/B.jpg", IMREAD_GRAYSCALE);
	CV_Assert(temp.data);
	threshold(temp, temp, 127, 255, THRESH_BINARY | THRESH_OTSU);

	temp2 = imread("image/C.jpg", IMREAD_GRAYSCALE);
	CV_Assert(temp2.data);
	threshold(temp2, temp2, 127, 255, THRESH_BINARY | THRESH_OTSU);

	g_clef = imread("image/Clef.jpg", IMREAD_GRAYSCALE);
	CV_Assert(g_clef.data);
	threshold(g_clef, g_clef, 127, 255, THRESH_BINARY | THRESH_OTSU);

	c_clef = imread("image/C_Clef.jpg", IMREAD_GRAYSCALE);
	CV_Assert(c_clef.data);
	threshold(c_clef, c_clef, 127, 255, THRESH_BINARY | THRESH_OTSU);
	/*
	
	for (int k = 0;k < 10;k++) {
		matchTemplate(binary_image, temp2, coeff2, TM_CCOEFF_NORMED);

		minMaxLoc(coeff2, &min, &max, NULL, &left_top);
		if (max > 0.50) {
			rectangle(binary_image, Rect(left_top, Point(left_top.x + temp2.cols, left_top.y + temp2.rows)), 0, 1, LINE_8);
		}
	}
	*/
	for (int i = 0;i < linecheck;i++) {
		for (int k = 0;k < 10;k++) {
			matchTemplate(subImage[i], g_clef, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.40) {
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + g_clef.cols, left_top.y + g_clef.rows)), 0, 1, LINE_8);
				clef = 1;
			}
		}
		for (int k = 0;k < 10;k++) {
			matchTemplate(subImage[i], c_clef, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.40) {
				rectangle(subImage[i], Rect(left_top, Point(left_top.x + c_clef.cols, left_top.y + c_clef.rows)), 0, 1, LINE_8);
				clef = 0;
			}
		}
		for (int k = 0;k < 100;k++) {
			matchTemplate(subImage[i], temp, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.70) {

				rectangle(subImage[i], Rect(left_top, Point(left_top.x + temp.cols, left_top.y + temp.rows)), 0, 1, LINE_8);
				note_x[note_number] = left_top.x;
				note_y[note_number] = temp.rows / 2;
				note_number++;
			}
		}
		

	}
	
	

}

int main() {
	
	Mat binary_image;
	
	Mat dst1;
	
	image = imread("image/stars.jpg", IMREAD_GRAYSCALE);
	CV_Assert(image.data);
	threshold(image, binary_image, 127, 255, THRESH_BINARY | THRESH_OTSU);






	//adaptiveThreshold(image, binary_image,255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 7, 10);
	imshow("binary_image", binary_image);
	divide_image(binary_image);
	divide_by_four();
	
	
	
	
	


	imshow("binary_image", binary_image);

	
	

	imshow("subImage0", subImage[0]);
	
	imshow("original", image);
	imshow("result", binary_image);
	
	
	
	

	waitKey(0);
	return 0;
}
