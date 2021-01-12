#include<Windows.h>
#include<iostream>

#include<winnt.h>
#include"opencv2/opencv.hpp"
#include"opencv2/highgui.hpp"

using namespace cv;
using namespace std;

Mat image;
Mat subImage[10] = {}; //오선 배열 동적할당

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
		//cout << pt_sort[i] << endl;
		//cout << pt_sort2[i] << endl;
		//line(dst, pt+delta, pt-delta, 255, 1, LINE_AA);
	}
	//cout << endl;
	sort(pt_sort, pt_sort + 300, cmp);
	sort(pt_sort2, pt_sort2 + 300, cmp);
	for (int i = 0;i < size;i+=2) {
//		int* my_ptr = (int*)dst.data;
		double pty = pt_sort[i].y - pt_sort[i + 1].y;
		//line(dst, pt_sort[i],pt_sort2[i], 255, 1, LINE_AA);
		int flag = 0;
		for (int k = 0;k < pty-1;k++) {
			for (int j = 0;j < src.cols;j++) {
				if (dst.at<uchar>(Point2d(j, pt_sort[i].y-k)) == 255) {
					dst.at<uchar>(Point2d(j, pt_sort[i].y - 1-k)) = 255;
				}
				
				
			}
		}
		//cout << src.at<uchar>(Point2d(0, 1)) << endl;

	//	for (int j = 1;j < pty;j++) {
	//		line(dst, pt_sort[i] - Point2d(0, j), pt_sort2[i]-Point2d(0,j), 255, 1.5, LINE_8);
	//	}
		//line(dst, pt_sort[i + 1], pt_sort2[i + 1], 255, 1, LINE_AA);
		
		//cout << pt_sort[i] << endl;
		//cout << pt_sort2[i] << endl;
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
		rect[i] = Rect(Point(0, one[i] - 20), Size(binary_image.cols, five[i] - one[i] + 40));
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
			//imshow(to_string(100 + k++), canny);
			HoughLines(canny, line, 1, (CV_PI / 180), 10);
			draw_houghLines(canny, houghImage[i], line, 10);

			cout << line[i] << endl;
//			cout << sizeof(subImage) / sizeof(Mat) << endl;
			//imshow(to_string(k++), houghImage[i]);
		}
		
		//Mat mask = getStructuringElement(MORPH_ELLIPSE, Size(3, 3));
		//morphologyEx(subImage[j], subImage[j], MORPH_CLOSE,mask);
		
		
	

		
		imshow(to_string(j), subImage[j]);
	}
}

int main() {
	
	Mat binary_image;
	Mat temp, temp2;
	Mat dst1;
	Mat clef;
	image = imread("image/stars.jpg", IMREAD_GRAYSCALE);
	CV_Assert(image.data);
	threshold(image, binary_image, 127, 255, THRESH_BINARY | THRESH_OTSU);

	temp = imread("image/B.jpg", IMREAD_GRAYSCALE);
	CV_Assert(temp.data);
	threshold(temp, temp, 127, 255, THRESH_BINARY | THRESH_OTSU);

	temp2 = imread("image/C.jpg", IMREAD_GRAYSCALE);
	CV_Assert(temp2.data);
	threshold(temp2, temp2, 127, 255, THRESH_BINARY | THRESH_OTSU);

	clef = imread("image/Clef.jpg", IMREAD_GRAYSCALE);
	CV_Assert(clef.data);
	threshold(clef, clef, 127, 255, THRESH_BINARY | THRESH_OTSU );

	double min, max;
	Point left_top;
	Mat coeff;
	Mat coeff2;
	imshow("temp", temp);
	imshow("temp2", temp2);

	//adaptiveThreshold(image, binary_image,255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY, 7, 10);
	imshow("binary_image", binary_image);
	divide_image(binary_image);
	divide_by_four();
	
	
	
	
	for(int k =0;k<100;k++){
	matchTemplate(binary_image, temp, coeff, TM_CCOEFF_NORMED);
	
		minMaxLoc(coeff, &min, &max, NULL, &left_top);
		if(max>0.70){
			rectangle(binary_image, Rect(left_top, Point(left_top.x + temp.cols, left_top.y + temp.rows)), 0, 1, LINE_8);
		}
	}
	for (int k = 0;k < 10;k++) {
		matchTemplate(binary_image, temp2, coeff2, TM_CCOEFF_NORMED);

		minMaxLoc(coeff2, &min, &max, NULL, &left_top);
		if (max > 0.50) {
			rectangle(binary_image, Rect(left_top, Point(left_top.x + temp2.cols, left_top.y + temp2.rows)), 0, 1, LINE_8);
		}
	}
	for (int k = 0;k < 10;k++) {
		matchTemplate(binary_image, clef, coeff2, TM_CCOEFF_NORMED);

		minMaxLoc(coeff2, &min, &max, NULL, &left_top);
		if (max > 0.40) {
			rectangle(binary_image, Rect(left_top, Point(left_top.x + clef.cols, left_top.y + clef.rows)), 0, 1, LINE_AA);
		}
	}

	imshow("binary_image", binary_image);

	
	

	imshow("subImage0", subImage[0]);
	
	imshow("original", image);
	imshow("result", binary_image);
	
	
	
	

	waitKey(0);
	return 0;
}