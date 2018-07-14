<?php 
	//Getting the requested id
	$url = $_GET['url'];
	
	//Importing database
    require_once('db_connect.php');

	//Creating sql query with where clause to get an specific COMMENTS
	$sql = "SELECT id,comment,url,android_id,usr,DATE_FORMAT(CONVERT_TZ(`date`, '+04:00', @@session.time_zone), '%Y-%m-%dT%TZ') as date FROM COMMENTS WHERE url=$url order by id";
	
	//getting result 
	$r = mysqli_query($con,$sql);
	
	//creating an array 
	$result = array();
	
	//looping through all the records fetched
	while($row = mysqli_fetch_array($r)){
		
		//Pushing comment and id in the array created 
		array_push($result,array(
			"id"=>$row['id'],
			"comment"=>$row['comment'],
            "url"=>$row['url'],
            "android_id"=>$row['android_id'],
            "usr"=>$row['usr'],
            "date"=>$row['date'] //quiiiiiiiii
		));
        
	}
	
	//Displaying the array in json format 
	echo json_encode(array('comments'=>$result));
	
    mysqli_close($con);
    
?>
