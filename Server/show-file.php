<?php

/*** assign the image id ***/
$image_id = 3;
try     {
    /*** connect to the database ***/
    $dbh = new PDO("mysql:host=localhost;dbname=testdb", 'root', 'e423');

    /*** set the PDO error mode to exception ***/
    $dbh->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);

    /*** The sql statement ***/
    $sql = "SELECT image, image_type FROM testblob WHERE image_id=$image_id";

    /*** prepare the sql ***/
    $stmt = $dbh->prepare($sql);

    /*** exceute the query ***/
    $stmt->execute();

    /*** set the fetch mode to associative array ***/
    $stmt->setFetchMode(PDO::FETCH_ASSOC);

    /*** set the header for the image ***/
    $array = $stmt->fetch();

    /*** check we have a single image and type ***/
    if(sizeof($array) == 2)
    {
        /*** set the headers and display the image ***/
        header("Content-type: ".$array['image_type']);

        /*** output the image ***/
        echo $array['image'];
    }
    else
    {
        throw new Exception("Out of bounds Error");
    }
}
catch(PDOException $e)
{
    echo $e->getMessage();
}
catch(Exception $e)
{
    echo $e->getMessage();
}
?>
