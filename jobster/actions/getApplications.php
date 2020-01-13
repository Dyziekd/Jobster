<?php

    include_once '../classes/application.php';
    
    // get params
    $idUser = filter_input(INPUT_POST, 'idUser');
    $status = filter_input(INPUT_POST, 'status');
    $idOffer = filter_input(INPUT_POST, 'idOffer');

    // get applications
    $application = new Application();
    
    if(is_null($idOffer))
        $applications = $application->getUserApplications($idUser, $status); 
    else
        $applications = $application->getOfferApplications($idOffer);
    
    echo json_encode($applications);
?>

