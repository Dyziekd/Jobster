<?php

    include_once '../classes/profile.php';

    // get params
    $idUser = filter_input(INPUT_POST, 'idUser');
    
    // get user data
    if(!empty($idUser))
    {
        $profile = new Profile();
        $profileData = $profile->getProfileData($idUser);
       
        echo json_encode($profileData);
    }
    
    
?>