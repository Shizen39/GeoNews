<?php 	
	if(isset($_POST['comment'])){ 
		require_once('db_connect.php');
        		
		//Create a prepared statements for overhead and sql injection protection 
        //and in order to separate data logic from sql logic
		//$sql = "INSERT INTO `COMMENTS` (`comment`, `url`, `android_id`, `usr`) VALUES ('$comment','$url','$android_id','$usr')";
		$sql = "INSERT INTO `COMMENTS` (`comment`, `url`, `android_id`, `usr`) VALUES (?,?,?,?)";

        //Prepare the template query
        if($pst= $con->prepare($sql)){
        	//Get values
            $comment = $_POST['comment'];
            $url = $_POST['url'];
            $android_id= $_POST['android_id'];
            $usr = $_POST['usr'];
            
            //Bind type values (ssss) whit bind values
            $pst->bind_param('ssss', $comment, $url, $android_id, $usr);
            //Execute the query
            $pst->execute();
            
            //Check result
            if($pst->affected_rows == 1){
                echo 1;
            }else{
                //Some error while inserting
                echo "ERROR: " . mysqli_error($con);
            }
            //Closing the statement
            $pst->close();
            // Close connection
 			mysqli_close($con);
        }else{
			echo "ERROR: " . mysqli_error($con);
		}		
	}else{
		echo "ERROR: " . mysqli_error($con);
	}
?>