#include<Windows.h>
#include<iostream>

#include<winnt.h>
#include"opencv2/opencv.hpp"
#include"opencv2/highgui.hpp"
#include"Note.hpp"

using namespace cv;
using namespace std;

#define LINE_CONNECTION 2 //음계 색출 시 보정값

Mat image;
Mat subImage[10] = {}; //오선 배열 동적할당
double line_y[5] = {};
int linecheck = 0;
Note sheet_note[10][100];

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
	Point2d note[10][100] = {};



	//templete image load
	temp = imread("image/B.jpg", IMREAD_GRAYSCALE);
	CV_Assert(temp.data);
	//threshold(temp, temp, 127, 255, THRESH_BINARY | THRESH_OTSU);

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


	*/
	



	for (int i = 0;i < linecheck;i++) {
		Mat clone = subImage[i].clone();
		int note_number = 0;
		for (int k = 0;k < 10;k++) {
			matchTemplate(clone, g_clef, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.40) {
				rectangle(clone, Rect(left_top, Point(left_top.x + g_clef.cols, left_top.y + g_clef.rows)), 0, 1, LINE_8);
				clef = 1;
			}
		}
		for (int k = 0;k < 10;k++) {
			matchTemplate(clone, c_clef, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.40) {
				rectangle(clone, Rect(left_top, Point(left_top.x + c_clef.cols, left_top.y + c_clef.rows)), 0, 1, LINE_8);
				clef = 0;
			}
		}
		for (int k = 0;k < 100;k++) {
			matchTemplate(clone, temp, coeff, TM_CCOEFF_NORMED);

			minMaxLoc(coeff, &min, &max, NULL, &left_top);
			if (max > 0.75) {

				rectangle(clone, Rect(left_top, Point(left_top.x + temp.cols, left_top.y + temp.rows)), 0, 1, LINE_8);
				note[i][note_number].x = left_top.x;
				note[i][note_number].y = (left_top.y + temp.rows / 2) - LINE_CONNECTION;
				note_number++;
			}
		}
		for (int k = 0;k < 10;k++) {
			matchTemplate(clone, temp2, coeff2, TM_CCOEFF_NORMED);

			minMaxLoc(coeff2, &min, &max, NULL, &left_top);
			if (max > 0.50) {
				rectangle(clone, Rect(left_top, Point(left_top.x + temp2.cols, left_top.y + temp2.rows)), 0, 1, LINE_8);
				note[i][note_number].x = left_top.x;
				note[i][note_number].y = (left_top.y + temp.rows / 2) - LINE_CONNECTION;
				note_number++;
			}
		}
		sort(note[i], note[i] + note_number, cmp2);
		for (int j = 0;j < note_number;j++) { //중복 제거
			if (note[i][j].x - note[i][j - 1].x < 3) {
				memmove(note[i] + j, note[i] + j + 1, sizeof(note[i]) - j);
				note_number--;

			}

		}

		int Tolerance; //음표 중심좌표와 오선간 허용 오차 값
		double line_gap = line_y[1] - line_y[0]; //오선 사이 간격
		if (line_gap == 6) {
			Tolerance = 2; //음표 중심좌표와 오선간 허용 오차 값
		}
		else
			Tolerance = 2;

		if (clef == 1) {
			cout << "오른손" << endl;
			for (int j = 0;j < note_number;j++) {
				if (note[i][j].y > line_y[0] - line_gap + Tolerance && note[i][j].y < line_y[0] - 2*line_gap - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "시" << endl;
					sheet_note[i][j].setWhiteNumber(7);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(5);
				}
				else if (note[i][j].y < line_y[0] - line_gap + Tolerance && note[i][j].y > line_y[0]-line_gap - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "라" << endl;
					sheet_note[i][j].setWhiteNumber(6);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(5);
				}
				else if (note[i][j].y > line_y[0]-line_gap + Tolerance && note[i][j].y < line_y[0] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "솔" << endl;
					sheet_note[i][j].setWhiteNumber(5);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(5);
				}
				else if (note[i][j].y<line_y[0] + Tolerance && note[i][j].y>line_y[0] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "파" << endl;
					sheet_note[i][j].setWhiteNumber(4);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(5);
				}
				else if (note[i][j].y > line_y[0] + Tolerance && note[i][j].y < line_y[1] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "미" << endl;
					sheet_note[i][j].setWhiteNumber(3);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(5);
				}
				else if (note[i][j].y<line_y[1] + Tolerance && note[i][j].y>line_y[1] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "레" << endl;
					sheet_note[i][j].setWhiteNumber(2);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(5);
				}
				else if (note[i][j].y > line_y[1] + Tolerance && note[i][j].y < line_y[2] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "도" << endl;
					sheet_note[i][j].setWhiteNumber(1);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(5);
				}
				else if (note[i][j].y<line_y[2] + Tolerance && note[i][j].y>line_y[2] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "시" << endl;
					sheet_note[i][j].setWhiteNumber(7);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(4);
				}
				else if (note[i][j].y > line_y[2] + Tolerance && note[i][j].y < line_y[3] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "라" << endl;
					sheet_note[i][j].setWhiteNumber(6);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(4);
				}
				else if (note[i][j].y<line_y[3] + Tolerance && note[i][j].y>line_y[3] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "솔" << endl;
					sheet_note[i][j].setWhiteNumber(5);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(4);
				}
				else if (note[i][j].y > line_y[3] + Tolerance && note[i][j].y < line_y[4] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "파" << endl;
					sheet_note[i][j].setWhiteNumber(4);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(4);
				}
				else if (note[i][j].y<line_y[4] + Tolerance && note[i][j].y>line_y[4] - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "미" << endl;
					sheet_note[i][j].setWhiteNumber(3);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(4);
				}
				else if (note[i][j].y > line_y[4] + Tolerance && note[i][j].y < line_y[4] + line_gap - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y << "  " << "레" << endl;
					sheet_note[i][j].setWhiteNumber(2);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(4);
				}
				else if (note[i][j].y < line_y[4] + line_gap + Tolerance && note[i][j].y > line_y[4] + line_gap - Tolerance) {
					cout << note[i][j].x << "  " << note[i][j].y <<"  "<< "도" << endl;
					sheet_note[i][j].setWhiteNumber(1);
					sheet_note[i][j].setClef(1);
					sheet_note[i][j].setOctav(4);
				}

				else{
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

				else {
					cout << note[i][j].x << "  " << note[i][j].y << endl;
				}
			}
		}

		cout << "1번째 선"<<line_y[0] << endl;
		cout << "2번째 선" << line_y[1] << endl;
		cout << "3번째 선" << line_y[2] << endl;
		cout << "4번째 선" << line_y[3] << endl;
		cout << "5번째 선" << line_y[4] << endl;
		

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
	
	
	find_scale();
	


	imshow("binary_image", binary_image);

	
	

	imshow("subImage0", subImage[0]);
	
	imshow("original", image);
	imshow("result", binary_image);
	
	
	
	

	waitKey(0);
	return 0;
}