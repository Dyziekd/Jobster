<?php

    include_once '../classes/user.php';
    
    $user = new User();

    // get params
    $idUser = filter_input(INPUT_POST, 'idUser');
    $newValue = filter_input(INPUT_POST, 'newValue');
    $password = filter_input(INPUT_POST, 'password');
    $function = filter_input(INPUT_POST, 'function');

    // change proper setting
    switch($function)
    {   
         case "changePassword":
            $json = $user->changePassword($newValue, $password, $idUser);
            echo json_encode($json);
            break;
        
         case "changeEmail":
            $json = $user->changeEmail($newValue, $password, $idUser);
            echo json_encode($json);
            break;
        
         case "changePhone":
            $json = $user->changePhone($newValue, $password, $idUser);
            echo json_encode($json);
            break;
        
         case "changeState":
            $json = $user->changeState($newValue, $password, $idUser);
            echo json_encode($json);
            break;
        
         case "changeCity":
            $json = $user->changeCity($newValue, $password, $idUser);
            echo json_encode($json);
            break;
        
         case "changeHobby":
            $json = $user->changeHobby($newValue, $password, $idUser);
            echo json_encode($json);
            break;
        
         case "changeDescription":
            $json = $user->changeDescription($newValue, $password, $idUser);
            echo json_encode($json);
            break;
    }
    
  
?>

