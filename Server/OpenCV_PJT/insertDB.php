<?php
 
    ini_set('display_errors', '0');
    //경고문구 삭제

    header("Content-Type:text/html; charset=UTF-8");
 
    $name= $_POST['name'];
    $msg= $_POST['msg'];
    $file= $_FILES['img'];
 
    //이미지 파일을 영구보관하기 위해
    //이미지 파일의 세부정보 얻어오기
    
    //$srcName= $file['name'];
    $srcName= $name;

    $tmpName= $file['tmp_name']; //php 파일을 받으면 임시저장소에 넣는다. 그곳이 tmp
 
    //임시 저장소 이미지를 원하는 폴더로 이동
    //$dstName= "uploads/".date('Ymd_his').$srcName;
    //$dstName= "image/".$srcName.'.'."png";
    $dstName= "image/bair".'.'."png";


    $result=move_uploaded_file($tmpName, $dstName);
    if($result){
        echo "upload success\n";
    }else{
        echo "upload fail\n";
    }
    echo "$name\n";
    echo "$msg\n";
    echo "$dstName\n";
 
    //글 작성 시간 변수
    $now= date('Y-m-d H:i:s');
 
    // $name, $msg, $dstName, $now DB에 저장
    // MySQL에 접속
    $conn= mysqli_connect("localhost","sangsu","@tkdtnfl98","noonchi");
   // if($conn) echo "id good \n"; else echo "id faillllll \n";

    //한글 깨짐 방지
    mysqli_query($conn, "set names utf8");
 
    //insert하는 쿼리문
    $sql="insert into talk(name, message, imgPath, date) values('$name','$msg','$dstName','$now')";
 
    $result =mysqli_query($conn, $sql); //쿼리를 요청하다. 
 
   if($result) echo "insert success \n";
   else echo "insert fail \n";
 
    mysqli_close($conn);

    exec("OpenCV_PJT.exe"); 
    exec("download.php");

?>
