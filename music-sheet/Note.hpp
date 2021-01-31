#pragma once

using namespace cv;
using namespace std;
class Note {
private:
	int clef;
	int octav;
	int white_number;
	int black_number;
	int beat;
public:
	Note() {
		clef = 0;
		octav = 0;
		white_number = 0;
		black_number = 0;
		beat = 0;
	}
	void setNote(int _clef, int _octav, int _white_number, int _black_number, int _beat) {
		clef = _clef;
		octav = _octav;
		white_number = _white_number;
		black_number = _black_number;
		beat = _beat;
	}
	string printNote() {
		return to_string(clef) + " " + to_string(octav) + " " + to_string(white_number) + " " + to_string(black_number) + " " + to_string(beat);
	}

	void getNote() {
		cout << clef << " " << octav << " " << white_number << " " << black_number << " " << beat << " " << endl;;
		
	}
	int getBeat() {
		return beat;
	}
	void setClef(int _clef) {
		clef = _clef;
	}
	void setOctav(int _octav) {
		octav = _octav;
	}
	void setWhiteNumber(int _white_number) {
		white_number = _white_number;
	}
	void setBlackNumber(int _black_number) {
		black_number = _black_number;
	}
	void setBeat(int _beat) {
		beat = _beat;
	}
};