<?php 
	if(!empty($_GET)){ 
    	//Importing database
        require_once('db_connect.php');
        $qUrl = $_GET["url"];
        
        //Create a prepared statements for overhead and sql injection protection 
      	//and in order to separate data logic from sql logic
		$sql = "SELECT id,comment,`url`,android_id,usr,DATE_FORMAT(CONVERT_TZ(`date`, '+04:00', @@session.time_zone), '%Y-%m-%dT%TZ') as date FROM COMMENTS WHERE url=$qUrl order by id";

        //Prepare the template query
        if($stm = $con->prepare($sql)){
            //Execute the query
            $stm->execute();

            /*Bind RESULT*/
            //bind select result values to variables
            $stm->bind_result($id, $comment, $url, $android_id, $usr, $date);

            //creating the response array 
        	$result = array();
            $ret = array();
            //looping through all the records fetched				
            while($tmp= $stm->fetch()){
                //Pushing fields in the array created 
                  $result["id"]= $id;
                  $result["comment"]= $comment;
                  $result["url"]= $url;
                  $result["android_id"]= $android_id;
                  $result["usr"]= $usr;
                  $result["date"]= $date;
                  $ret[]=$result;
			}
            //Displaying the array in json format 
        	echo json_encode(array('comments'=>$ret));

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