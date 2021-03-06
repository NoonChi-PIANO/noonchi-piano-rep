#pragma once

using namespace cv;
using namespace std;
class Note {
private:
	int clef;
	int octav;
	int white_number;
	int black_number;
	string beat;
	int is_dotted;
public:
	Note() {
		clef = 0;
		octav = 0;
		white_number = 0;
		black_number = 0;
		beat = "0000";
		is_dotted = 0;
	}
	void setNote(int _clef, int _octav, int _white_number, int _black_number, int _beat) {
		clef = _clef;
		octav = _octav;
		white_number = _white_number;
		black_number = _black_number;
		beat = _beat;
	}
	string printNote() {
		return to_string(clef) + to_string(octav) + to_string(white_number)  + to_string(black_number) + beat;
	}

	void getNote() {
		cout << clef << " " << octav << " " << white_number << " " << black_number << " " << beat << " " << endl;;
		
	}
	string getBeat() {
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
		if (is_dotted == 1) {
			beat = _beat + _beat / 2;
		}
		else {
			beat = _beat;
		}
		if (_beat < 100)
			beat = "00" + to_string(_beat);
		else
			beat = "0" + to_string(_beat);
		
	}
	void isDot() {
		is_dotted = 1;
	}
};