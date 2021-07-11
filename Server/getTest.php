<?php
 
    header("Content-Type: text/html; charset=UTF-8");
 
    $name= $_GET['name'];
    $message= $_GET['msg'];
 
    echo "이름 : $name \n";
    echo "메세지 : $message \n";
 
?>
