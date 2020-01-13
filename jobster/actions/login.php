<?php

    include_once '../classes/user.php';
    
    // login variables
    $login = filter_input(INPUT_POST, 'login');
    $password = filter_input(INPUT_POST, 'password');
        
    // try to sign in
    if(!empty($login) && !empty($password))
    {
        $user = new User();
        $signIn = $user->signIn($login, $password);
       
        echo json_encode($signIn);
    }
    
?>