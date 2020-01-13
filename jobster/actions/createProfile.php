<?php

    include_once '../classes/profile.php';
    
    // get profile params
    $gender = filter_input(INPUT_POST, 'gender');
    $name = filter_input(INPUT_POST, 'name');
    $surname = filter_input(INPUT_POST, 'surname');
    $birthday = filter_input(INPUT_POST, 'birthday');
    $state = filter_input(INPUT_POST, 'state');
    $city = filter_input(INPUT_POST, 'city');
    $phoneNumber = filter_input(INPUT_POST, 'phoneNumber');
    $hobby = filter_input(INPUT_POST, 'hobby');
    $description = filter_input(INPUT_POST, 'description');
    $idUser = filter_input(INPUT_POST, 'idUser');
    
    // try to create profile
    if(!empty($gender) && !empty($name) && !empty($surname) && !empty($birthday) && !empty($state) && !empty($city) && !empty($phoneNumber) && !empty($idUser))
    {
        $profile = new Profile();
        $createProfile = $profile->createProfile($gender, $name, $surname, $birthday, $state, $city, $phoneNumber, $hobby, $description, $idUser);
       
        echo json_encode($createProfile);
    }
    
?>
