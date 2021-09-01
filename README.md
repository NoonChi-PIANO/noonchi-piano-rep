# 공지사항
API 29로 해야됨. 만약에 fftpack없다 뜨면 app/libs/ 에 fftpack추가해야함
산학대전ver로 바뀌면서 음계인식 FFT -> MIDI Input으로 변경  

# noonchi-piano-rep
눈치 피아노 졸업 작품 공통 저장소

# 프로젝트 개요

![image](https://user-images.githubusercontent.com/66546156/125243188-b59b8f80-e328-11eb-86d7-e23a7e9dd844.png)
![image](https://user-images.githubusercontent.com/66546156/125242247-66089400-e327-11eb-82d6-2deb554cc10f.png)
![image](https://user-images.githubusercontent.com/66546156/125242346-85072600-e327-11eb-87c4-5b354c792f6f.png)
![image](https://user-images.githubusercontent.com/66546156/125242374-8e908e00-e327-11eb-9a53-6cf5a428ac8f.png)
![image](https://user-images.githubusercontent.com/66546156/125242391-94866f00-e327-11eb-80fb-e02b0e87f0f2.png)


# 핵심 모듈의 소개
![image](https://user-images.githubusercontent.com/66546156/125242460-aec04d00-e327-11eb-92dc-42b0b50db1c2.png)

# 악보 인식 모듈
사용 기술 
-> APM(Apache, Php, MySQL) 프레임 워크를 사용하여 서버 구축. 
-> PHP를 통해 어플리케이션과 서버의 의사소통 구현
-> 어플리케이션의 요청을 받으면 C++ OpenCV 프로그램이 실행되며 악보 인식

![image](https://user-images.githubusercontent.com/66546156/125242731-08287c00-e328-11eb-95b7-39f2ad671e69.png)
![image](https://user-images.githubusercontent.com/66546156/125242684-fc3cba00-e327-11eb-80e3-f81a1904d5f5.png)


# 음계 인식 모듈
사용 기술
-> Fast Fourier Transform (using RealDoubleFFT)

![image](https://user-images.githubusercontent.com/66546156/125242886-3efe9200-e328-11eb-97e8-ff82aef920ef.png)
![image](https://user-images.githubusercontent.com/66546156/125242902-43c34600-e328-11eb-82e0-3258f5409d1e.png)


# 리듬 막대 모듈
![image](https://user-images.githubusercontent.com/66546156/125242920-4e7ddb00-e328-11eb-93eb-feec19d411bd.png)


# 어플리케이션 데모
ver 2021.07.12 70%
+) 음계 인식, 양손 양손 악보 추가, 반복 연습, 배속 조절 기능 추가
+) 서버에서 동작하는 악보인식 추가
![image](https://user-images.githubusercontent.com/66546156/125243417-0b703780-e329-11eb-9ce3-1ee7baf3358e.png)
![image](https://user-images.githubusercontent.com/66546156/125243604-470b0180-e329-11eb-87c0-2df0e5142b89.png)



