<?php 
	if(isset($_POST['newcomment'])){ 
		require_once('db_connect.php');
        		
		//Create a prepared statements for overhead and sql injection protection 
      	//and in order to separate data logic from sql logic
        $sql = "UPDATE COMMENTS SET `comment`=? WHERE `comment`=? AND `url`=?;";
        //Prepare the template query
        if($pst= $con->prepare($sql)){
        	//Get values
            $newComment = $_POST['newcomment'];
            $oldComment = $_POST['oldcomment'];
            $url = $_POST['url'];
			
            //Bind type values (ssss) whit bind values
            $pst->bind_param('sss', $newComment, $oldComment, $url);
            //Execute the query
            $ret= $pst->execute();
            
            //Check result
            if($pst->affected_rows == 1){
                echo 1;
            }else{
                //Some error while inserting
                echo "ERROR: " . mysqli_error($con);
            }
            //Close the statement
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