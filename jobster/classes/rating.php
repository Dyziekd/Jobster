<?php

    include_once '../config/dbConnection.php';

    class Rating
    {
        private $db;
        private $ratingTable = "rating";
        
        public function __construct()
        {
            $this->db = new Connection();
        }
        
        // create opinion (insert new opinion into database)
        public function createOpinion($rating, $comment, $idProfileFrom, $idProfileTo)
        {
            // opinion already exists
            if($this->checkIfOpinionExists($idProfileFrom, $idProfileTo))
            {
                $json['success'] = "0";
                $json['message'] = "Nie możesz wystawić więcej niż jedną opinie jednemu użytkownikowi!";
                return $json;
            }
            
            // opinion to same account type
            if($this->getAccountType($idProfileFrom) == $this->getAccountType($idProfileTo))
            {
                $json['success'] = "0";
                if($this->getAccountType($idProfileFrom) == 1) 
                    $json['message'] = "Jako pracownik nie możesz wystawić opinii innemu pracownikowi!";
                else if($this->getAccountType($idProfileFrom) == 2)
                    $json['message'] = "Jako pracodawca nie możesz wystawić opnii innemu pracodawcy!!";

                return $json;
            }
            
            // fix comment
            if(empty($comment))
                $comment = 'NULL';
            else
                $comment = "'".$comment."'";
            
            // make query
            $query = "INSERT INTO ".$this->ratingTable." (rating, comment, id_profile_from, id_profile_to) VALUES ('$rating', $comment, '$idProfileFrom', '$idProfileTo')";
            $result = mysqli_query($this->db->getDb(), $query);
            
            echo mysqli_error($this->db->getDb());
            // set json response 
            if($result == 1)
            {
                $json['success'] = 1;
                $json['message'] = "Dodano opinię!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nie udało się dodać opinii";
            }
            
            return $json;
        }   
        
        // get opnions (fetch opinions from database)
        public function getOpinions($idProfile)
        {
            $profileTable = "profile";
            $json = array();
            
            // make query
            $query = "SELECT r.id_rating, p.name, p.surname, r.rating, r.comment, r.id_profile_from, r.id_profile_to FROM ".$this->ratingTable." r JOIN ".$profileTable." p ON p.id_profile = r.id_profile_from WHERE r.id_profile_to = '$idProfile'";
            $result = mysqli_query($this->db->getDb(), $query);
            $numRows = mysqli_num_rows($result);

            // push each opinion to json response
            for($i = 0; $i < $numRows; $i++)
            {
                $opinion = mysqli_fetch_assoc($result);
                array_push($json, $opinion);
            }

            return $json;
        }    
        
        // check if opinion with specified ids exists
        private function checkIfOpinionExists($idProfileFrom, $idProfileTo)
        {
            // make query
            $query = "SELECT * FROM ".$this->ratingTable." where id_profile_from = '$idProfileFrom' AND id_profile_to = '$idProfileTo'";
            $result = mysqli_query($this->db->getDb(), $query);
            
            return mysqli_num_rows($result) > 0;
        }
        
        // get user account type
        private function getAccountType($idProfile)
        {
            $userTable = "user";
            $profileTable = "profile";
            
            // make query
            $query = "SELECT u.account_type FROM ".$userTable." u JOIN ".$profileTable. " p ON p.id_user = u.id_user WHERE p.id_profile = '$idProfile'";
            $result = mysqli_query($this->db->getDb(), $query);
            $row = mysqli_fetch_assoc($result);
            
            // return account type
            return $row['account_type'];
        }
    }
?>
