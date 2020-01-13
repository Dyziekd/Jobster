<?php

    include_once '../classes/application.php';
    
    // get params
    $idApplication = filter_input(INPUT_POST, 'idApplication');
    
    // try to accept application
    if(!empty($idApplication))
    {        
        $application = new Application();        
        $acceptApplication = $application->acceptApplication($idApplication);
       
        echo json_encode($acceptApplication);
    }

?>
