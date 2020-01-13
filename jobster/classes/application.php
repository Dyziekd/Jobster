<?php

    include_once '../config/dbConnection.php';

    class Application
    {
        private $db;
        private $applicationTable = "application";
        
        public function __construct()
        {
            $this->db = new Connection();
        }
        
        // create application
        public function createApplication($message, $idOffer, $idUser)
        {
            // fix message
            if(empty($message))
                $message = 'NULL';
            else
                $message= "'".$message."'";
            
            // make query
            $query = "INSERT INTO ".$this->applicationTable." (apply_time, message, status, id_offer, id_user) VALUES (NOW(), $message, '1', '$idOffer', '$idUser')";
            $result = mysqli_query($this->db->getDb(), $query);
            
            // set json response
            if($result == 1)
            {
                $json['success'] = 1;
                $json['message'] = "Zaplikowano do oferty!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nie udało się zaaplikować do oferty";
            }
            
            return $json;
        }
        
        // sets application status to accepted
        public function acceptApplication($idApplication)
        {
            // make query
            $query = "UPDATE ".$this->applicationTable." SET status = 2 WHERE id_application = '$idApplication'";
            $result = mysqli_query($this->db->getDb(), $query);
            
            // set json response
            if($result == 1)
            {
                $json['success'] = 1;
                $json['message'] = "Zaakceptowano aplikację!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nie udało się zaakceptować aplikacji!";
            }
            
            return $json;
        }
        
         // sets application status to rejected
        public function rejectApplication($idApplication)
        {
            // make query
            $query = "UPDATE ".$this->applicationTable." SET status = 3 WHERE id_application = '$idApplication'";
            $result = mysqli_query($this->db->getDb(), $query);
         
            // set json response
            if($result == 1)
            {
                $json['success'] = 1;
                $json['message'] = "Odrzcono aplikację!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nie udało się odrzucić aplikacji!";
            }
            
            return $json;
        }
        
        // cancel application
        public function cancelApplication($idOffer, $idUser)
        {
            // make query
            $query = "DELETE FROM ".$this->applicationTable." WHERE id_offer = '$idOffer' AND id_user = '$idUser'";
            $result = mysqli_query($this->db->getDb(), $query);
            
            // set json response
            if($result == 1)
            {
                $json['success'] = 1;
                $json['message'] = "Twoja aplikacja została cofnięta!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nie udało się anulować aplikacji!";
            }
            
            return $json;
        }
        
        // get user applications 
        public function getUserApplications($idUser, $status)
        {
            $offerTable = "offer";
            $userTable = "user";
            $json = array();
            
            // create query
            $query = "SELECT a.id_application, o.name AS offer_name, o.city, a.status, o.salary, o.salary_type, a.apply_time, a.id_offer, a.id_user FROM ".$this->applicationTable." a JOIN ".$offerTable." o ON o.id_offer = a.id_offer WHERE a.id_user = '$idUser'";
            if($status == 1)
                $query = $query . " AND a.status = '$status'";
            $query = $query . " ORDER BY a.apply_time DESC";
                      
            // make query
            $result = mysqli_query($this->db->getDb(), $query);
            $numRows = mysqli_num_rows($result);

            // push each application to json response
            for($i = 0; $i < $numRows; $i++)
            {
                $application = mysqli_fetch_assoc($result);
                array_push($json, $application);
            }

            return $json;
        }
        
        // get offer applications
        public function getOfferApplications($idOffer)
        {
            $profileTable = "profile";
            $userTable = "user";
            $json = array();
            
            // create query
            $query = "SELECT a.id_application, p.name, p.surname, a.apply_time, a.status, a.message, a.id_offer, a.id_user AS id_applier FROM ".$this->applicationTable." a JOIN ".$profileTable." p ON p.id_user = a.id_user WHERE a.id_offer = '$idOffer' ORDER BY a.apply_time DESC";
                      
            // make query
            $result = mysqli_query($this->db->getDb(), $query);
            $numRows = mysqli_num_rows($result);

            // push each application to json response
            for($i = 0; $i < $numRows; $i++)
            {
                $application = mysqli_fetch_assoc($result);
                array_push($json, $application);
            }

            return $json;
        }
    }
?>