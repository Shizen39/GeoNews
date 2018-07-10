<?php 

   if(isset($_POST['url'])){
	//Getting url
	$url = $_POST['url'];
	
	//Importing database
	require_once('dbConnect.php');
	
	//Creating sql query
	$sql = "DELETE FROM COMMENTS WHERE url=$url;";
	
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

