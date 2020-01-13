<?php

    include_once '../classes/offer.php';
    
    // get params
    $idOffer = filter_input(INPUT_POST, 'idOffer');
    $idUser = filter_input(INPUT_POST, 'idUser');
    
    // get offer details
    $offer = new Offer();
    $offers = $offer->getOfferDetails($idOffer, $idUser);
       
    echo json_encode($offers);
?>

