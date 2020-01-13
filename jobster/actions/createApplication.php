<?php

    include_once '../classes/application.php';
    
    // get application params
    $message = filter_input(INPUT_POST, 'message');
    $idOffer = filter_input(INPUT_POST, 'idOffer');
    $idUser = filter_input(INPUT_POST, 'idUser');
    
    // try to create application
    if(!empty($idOffer) && !empty($idUser))
    {        
        $application = new Application();        
        $createApplication = $application->createApplication($message, $idOffer, $idUser);
       
        echo json_encode($createApplication);
    }

?>
