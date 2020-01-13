<?php

    include_once '../classes/user.php';
    
    // registration variables
    $login = filter_input(INPUT_POST, 'login');
    $email = filter_input(INPUT_POST, 'email');
    $password = filter_input(INPUT_POST, 'password');
    $accountType = filter_input(INPUT_POST, 'accountType');
        
    // try to sign up
    if(!empty($login) && !empty($email) && !empty($password) && !empty($accountType))
    {
        $user = new User();
        $signUp = $user->signUp($login, $email, $password, $accountType);
        
        echo json_encode($signUp);
    }
    
?>
