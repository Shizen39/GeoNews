<?php 

   if(isset($_POST['url'])){
	//Getting url
	$url = $_POST['url'];
	$comment = $_POST['comment'];
	$android_id = $_POST['android_id'];
	
	//Importing database
	require_once('dbConnect.php');
	
	//Creating sql query
	$sql = "DELETE FROM COMMENTS WHERE url=$url AND comment=$comment and andrid_id=$android_id;";
	
	//Deleting record in database 
	if(mysqli_query($con,$sql)){
		echo 'Comment Deleted Successfully';
	}else{
		echo 'Could Not Delete Comment Try Again';
	}
	
	//closing connection 
	mysqli_close($con);
   }else{
        echo 'Sending Bad Request';
   }


?>

