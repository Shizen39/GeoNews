<?php 
	if(!empty($_GET)){ 
    	//Importing database
        require_once('db_connect.php');
        $url = $_GET['url'];
        $android_id= $_GET['android_id'];

        //Create a prepared statements for overhead and sql injection protection 
      	//and in order to separate data logic from sql logic
        $sql = "SELECT usr FROM COMMENTS WHERE url=$url AND android_id=$android_id GROUP BY usr";

        if($stm = $con->prepare($sql)){
            //Execute the query
            $stm->execute();

            /*Bind RESULT*/
            //bind select result values to variables
            $stm->bind_result($usr);
            
            //creating the response array 
        	$result = array();
            $ret = array();
            //looping through all the records fetched	
			while($tmp= $stm->fetch()){
                //Pushing field in the array created 
                  $result["usr"]= $usr;
                  $ret[]=$result;
			}
			//Displaying the array in json format 
			echo json_encode(array('Usr'=>$ret));
            
        	//Close the statement
            $stm->close();
            // Close connection
 			mysqli_close($con);
        }else{
			echo "ERROR: " . mysqli_error($con);
		}		
	}else{
		echo "ERROR: " . mysqli_error($con);
	}
?>
        