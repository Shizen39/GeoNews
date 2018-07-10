<?php 
	
	require_once('db_connect.php');
	if($_SERVER['REQUEST_METHOD']=='POST'){
		
		//Getting values
		$comment = $_GET['comment'];
		$url = $_GET['url'];
		$android_id= $_GET['android_id'];
		$usr = $_GET['usr'];
		
		echo $comment;
		echo $url;
		echo $android_id;
		echo $usr;



		
		//Creating an sql query
		//$sql = "INSERT INTO COMMENTS (comment, url, usr) VALUES ('$comment','$url','$usr')";
		$sql = "INSERT INTO `my_geonews`.`COMMENTS` (`comment`, `url`, `android_id`, `usr`) VALUES ('$comment','$url','$android_id','$usr')";
		
		//Importing our db connection script

		
		
		//Executing query to database
		if(mysqli_query($con,$sql)){
			echo 'Comment Added Successfully';
		}else{
			echo 'Could Not Add Comment';
		}
		
		//Closing the database 
		mysqli_close($con);
	}else{
		echo 'Not Post Request';
	}
?>