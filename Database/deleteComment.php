<?php 
	if(isset($_POST['id'])){ 
		require_once('db_connect.php');
        		
		//Create a prepared statements for overhead and sql injection protection 
      	//and in order to separate data logic from sql logic
		$sql = "DELETE FROM COMMENTS WHERE `id`=?;";

        //Prepare the template query
        if($pst= $con->prepare($sql)){
        	//Get values
            $id = $_POST['id'];
			
            //Bind type values (ssss) whit bind values
            $pst->bind_param('s',$id);
            //Execute the query
            $pst->execute();
            
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