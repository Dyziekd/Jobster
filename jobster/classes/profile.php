<?php

    include_once '../config/dbConnection.php';
    
    class Profile
    {
        private $db;
        private $profileTable = "profile";
        
        public function __construct()
        {
            $this->db = new Connection();
        }
        
        // create profile (insert new profile into database)
        public function createProfile($gender, $name, $surname, $birthday, $state, $city, $phoneNumber, $hobby, $description, $idUser)
        {
            // make query
            $query = "INSERT INTO ".$this->profileTable." (gender, name, surname, birthday, state, city, phone_number, hobby, description, id_user) VALUES ('$gender', '$name', '$surname', '$birthday', '$state', '$city', '$phoneNumber', '$hobby', '$description', '$idUser')"; 
            $result = mysqli_query($this->db->getDb(), $query);
            
            // set json response
            if($result == 1)
            {
                $idProfile = $this->db->getDb()->insert_id;
                $json['id_profile'] = $idProfile;
                $json['success'] = 1;
                $json['message'] = "Wirtualny profil został utworzony!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nie udało się utworzyć wirtualnego profilu";
            }
            
            return $json;
        }   

        // get profile data 
        public function getProfileData($id)
        {
            $json = array();
            
            // get data from virtual profile
            $query = "SELECT id_profile, name, surname, birthday, state, city, phone_number, hobby, description, average_rating FROM ".$this->profileTable." WHERE id_user = '$id'";
            $result = mysqli_query($this->db->getDb(), $query);
            $row = mysqli_fetch_assoc($result);
            $json['id_profile'] = $row['id_profile'];
            $json['name'] = $row['name'];
            $json['surname'] = $row['surname'];
            $json['birthday'] = $row['birthday'];
            $json['state'] = $row['state'];
            $json['city'] = $row['city'];
            $json['phone_number'] = $row['phone_number'];
            $json['hobby'] = $row['hobby'];
            $json['description'] = $row['description'];
            $json['average_rating'] = $row['average_rating'];

            // get email 
            $userTable = "user";
            $query = "SELECT email FROM ".$userTable." WHERE id_user = '$id'";
            $result = mysqli_query($this->db->getDb(), $query);
            $json['email'] = mysqli_fetch_row($result)['0'];
            
            return $json;
        }
        
      
    
           
        
  
    }

?>

