<?php

    include_once '../classes/rating.php';
    
    // get params
    $idProfile = filter_input(INPUT_POST, 'idProfile');
    
    // try to get user opinions
    if(!empty($idProfile))
    {        
        $rating = new Rating();        
        $opinions = $rating->getOpinions($idProfile);
       
        echo json_encode($opinions);
    }

?>
