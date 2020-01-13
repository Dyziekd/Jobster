<?php

    include_once '../config/dbConnection.php';

    class Offer
    {
        private $db;
        private $offerTable = "offer";
        
        public function __construct()
        {
            $this->db = new Connection();
        }
        
        // create offer (insert new offer into database)
        public function createOffer($name, $salaryType, $salary, $state, $city, $address, $startTime, $description, $idUser)
        {
            // fix description 
            if(empty($description))
                $description = 'NULL';
            else
                $description = "'".$description."'";
            
            // make query
            $query = "INSERT INTO ".$this->offerTable." (name, salary_type, salary, state, city, address, start_time, publication_time, description, status, id_user) VALUES ('$name', '$salaryType', '$salary', '$state', '$city', '$address', '$startTime', NOW(), $description, '1', '$idUser')"; 
            $result = mysqli_query($this->db->getDb(), $query);
            
            // set json response 
            if($result == 1)
            {
                $json['success'] = 1;
                $json['message'] = "Oferta została utworzona!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nie udało się utworzyć oferty";
            }
            
            return $json;
        }   
        
        // end offer (change status to inactive)
        public function endOffer($idOffer)
        {
            // make query
            $query = "UPDATE ".$this->offerTable." SET status = 0 WHERE id_offer = '$idOffer'"; 
            $result = mysqli_query($this->db->getDb(), $query);
            
            // set json response 
            if($result == 1)
            {
                $json['success'] = 1;
                $json['message'] = "Zakończono okres aplikowania do tej oferty!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nie udało się zakończyć możliwości aplikowania do tej oferty";
            }
            
            return $json;
        }   
        
        // get offers (fetch offers from database)
        public function getOffers($idUser, $status)
        {
            $profileTable = "profile";
            $json = array();
            
            // create query
            if($status == 1)    // only active offers
                $query = "SELECT o.id_offer, o.name AS offer_name, o.city, o.start_time, o.salary, o.salary_type, p.name AS provider_name, p.surname AS provider_surname FROM ".$this->offerTable." o JOIN ".$profileTable." p ON p.id_user = o.id_user WHERE o.start_time > NOW() AND o.status = '$status'";
            else if($status == 0) // only inactive offers 
                $query = "SELECT o.id_offer, o.name AS offer_name, o.city, o.start_time, o.salary, o.salary_type, p.name AS provider_name, p.surname AS provider_surname FROM ".$this->offerTable." o JOIN ".$profileTable." p ON p.id_user = o.id_user WHERE status = '$status'";
            if(isset($idUser))
                $query = $query . " AND o.id_user = '$idUser'";
            $query = $query . " ORDER BY o.publication_time DESC";
            
            // make query
            $result = mysqli_query($this->db->getDb(), $query);
            $numRows = mysqli_num_rows($result);

            // push each offer to json response
            for($i = 0; $i < $numRows; $i++)
            {
                $offer = mysqli_fetch_assoc($result);
                array_push($json, $offer);
            }

            return $json;
        }
        
        // get offer details
        public function getOfferDetails($idOffer, $idUser)
        {
            $profileTable = "profile";
            $json = array();
            
            // create query
            $query = "SELECT o.id_offer, p.name AS provider_name, p.surname AS provider_surname, o.publication_time, o.name AS offer_name, o.state, o.city, o.address, o.start_time, o.salary, o.salary_type, o.description, o.applications_count, o.status, o.id_user FROM ".$this->offerTable." o JOIN ".$profileTable." p ON p.id_user = o.id_user WHERE o.id_offer = '$idOffer'";
           
            // make query
            $result = mysqli_query($this->db->getDb(), $query);
            $rows = mysqli_fetch_assoc($result);

            // set json response
            $json['provider_name'] = $rows['provider_name'];
            $json['provider_surname'] = $rows['provider_surname'];
            $json['publication_time'] = $rows['publication_time'];
            $json['offer_name'] = $rows['offer_name'];
            $json['state'] = $rows['state'];
            $json['city'] = $rows['city'];
            $json['address'] = $rows['address'];
            $json['start_time'] = $rows['start_time'];
            $json['salary'] = $rows['salary'];
            $json['salary_type'] = $rows['salary_type'];
            $json['description'] = $rows['description'];
            $json['applications_count'] = $rows['applications_count'];
            $json['status'] = $rows['status'];
            $json['id_provider'] = $rows['id_user'];
            $json['id_offer'] = $rows['id_offer'];
            $json['already_applied'] = $this->isAlreadyApplied($idOffer, $idUser);
            
            return $json;
        }
        
        // check if specified user already applied to specified offer
        private function isAlreadyApplied($idOffer, $idUser)
        {
            $applicationTable = "application";

            $query = "SELECT * FROM ".$applicationTable." WHERE id_offer = '$idOffer' AND id_user = '$idUser'"; 
            $result = mysqli_query($this->db->getDb(), $query);
            
            if(mysqli_num_rows($result) > 0)
                return true;
            else
                return false;

        }
    }
?>
