<?php

    include_once '../classes/rating.php';
    
    // get opinion params
    $rating = filter_input(INPUT_POST, 'rating');
    $comment = filter_input(INPUT_POST, 'comment');
    $id_profile_from = filter_input(INPUT_POST, 'id_profile_from');
    $id_profile_to = filter_input(INPUT_POST, 'id_profile_to');
   
    // try to create opinion
    if(!empty($rating) && !empty($id_profile_from) && !empty($id_profile_to))
    {        
        $opinion = new Rating();        
        $createOpinion = $opinion->createOpinion($rating, $comment, $id_profile_from, $id_profile_to);
       
        echo json_encode($createOpinion);
    }



?>

