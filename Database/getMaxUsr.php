<?php 
	//Getting the requested id
	$url = $_GET['url'];
	
	//Importing database
    require_once('db_connect.php');

	//Creating sql query with where clause to get an specific COMMENTS
	$sql = "SELECT MAX(usr) AS usr FROM COMMENTS WHERE url=$url";
	
	//getting result 
	$r = mysqli_query($con,$sql);
	
	//creating an array 
	$result = array();
	
	//looping through all the records fetched
	while($row = mysqli_fetch_array($r)){
		
		//Pushing comment and id in the array created 
		array_push($result,array(
            "usr"=>$row['usr'],
        ));
        
	}
	
	//Displaying the array in json format 
	echo json_encode(array('Usr'=>$result));
	
    mysqli_close($con);
    
?>
