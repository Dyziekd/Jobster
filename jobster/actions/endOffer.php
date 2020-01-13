<?php

    include_once '../classes/offer.php';
    
    // get params
    $idOffer = filter_input(INPUT_POST, 'idOffer');
 
    // try to end offer
    if(!empty($idOffer))
    {        
        $offer = new Offer();        
        $endOffer = $offer->endOffer($idOffer);
       
        echo json_encode($endOffer);
    }



?>

