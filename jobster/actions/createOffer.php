<?php

    include_once '../classes/offer.php';
    
    // get offer params
    $name = filter_input(INPUT_POST, 'name');
    $salaryType = filter_input(INPUT_POST, 'salaryType');
    $salary = filter_input(INPUT_POST, 'salary');
    $state = filter_input(INPUT_POST, 'state');
    $city = filter_input(INPUT_POST, 'city');
    $address = filter_input(INPUT_POST, 'address');
    $startTime = filter_input(INPUT_POST, 'startTime');
    $endTime = filter_input(INPUT_POST, 'endTime');
    $description = filter_input(INPUT_POST, 'description');
    $idUser = filter_input(INPUT_POST, 'idUser');
    
    // try to create offer
    if(!empty($name) && !empty($salaryType) && !empty($salary) && !empty($state) && !empty($city) && !empty($address) && !empty($startTime) && !empty($idUser))
    {        
        $offer = new Offer();        
        $createApplication = $offer->createOffer($name, $salaryType, $salary, $state, $city, $address, $startTime, $description, $idUser);
       
        echo json_encode($createApplication);
    }



?>

