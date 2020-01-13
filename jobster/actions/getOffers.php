<?php

    include_once '../classes/offer.php';
    
    // get params
    $idUser = filter_input(INPUT_POST, 'idUser');
    $status = filter_input(INPUT_POST, 'status');
    
    // get offers
    $offer = new Offer();
    $offers = $offer->getOffers($idUser, $status);
       
    echo json_encode($offers);
?>

