<?php

//Defining Constants used to connect to the database
 define('HOST','localhost');
 define('USER','root');
 define('PASS','');
 define('DB','my_geonews');
 
 //Connecting to Database
 $con = mysqli_connect(HOST,USER,PASS,DB) or die('Unable to Connect');