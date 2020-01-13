<?php

    include_once '../config/dbConnection.php';

    class User
    {
        private $db;
        private $userTable = "user";
        private $profileTable = "profile";

        public function __construct()
        {
            $this->db = new Connection();
        }
        
        // check if login exists in database
        public function isLoginUnique($login)
        {
            $query = "SELECT * FROM ".$this->userTable." WHERE login = '$login'";
            $result = mysqli_query($this->db->getDb(), $query);

            if(mysqli_num_rows($result) > 0)
                return false;
            else
                return true;
        }

        // check if email exists in database
        public function isEmailUnique($email)
        {
            $query = "SELECT * FROM ".$this->userTable." WHERE email = '$email'";
            $result = mysqli_query($this->db->getDb(), $query);

            if(mysqli_num_rows($result) > 0)
                return false;
            else
                return true;
        }
   
        // check if user has a virtual profile
        public function hasProfile($idUser)
        {
            $query = "SELECT * FROM ".$this->profileTable." WHERE id_user = '$idUser'";
            $result = mysqli_query($this->db->getDb(), $query);
            
            if(mysqli_num_rows($result) > 0)
                return true;
            else
                return false;            
        }
        
        // get virtual profile id
        public function getProfileId($idUser)
        {
            $query = "SELECT id_profile FROM ".$this->profileTable." WHERE id_user = '$idUser'";
            $result = mysqli_query($this->db->getDb(), $query);
            
            if(mysqli_num_rows($result) == 0)
                    return -1;
            else
            {
                $row = mysqli_fetch_assoc($result);
                return $row['id_profile'];
            }
        }
      
        // check if password is correct
        public function isPasswordCorrect($password, $idUser)
        {
            $query = "SELECT password FROM ".$this->userTable." WHERE id_user = '$idUser'";
            $result = mysqli_query($this->db->getDb(), $query);
            $row = mysqli_fetch_row($result);
            
            return ($row['0'] == "$password");
        }
            
        public function changePassword($newPassword, $password, $idUser)
        {
            $json = array();
            
            if($this->isPasswordCorrect($password, $idUser))
            {
                $query = "UPDATE ".$this->userTable. " SET password = '$newPassword' WHERE id_user = '$idUser'";
                $result = mysqli_query($this->db->getDb(), $query);

                $json['success'] = 1;
                $json['message'] = "Hasłó zostało zmienione!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nieprawdiłowe hasło!";
            }
            
            return $json;
        }
        
        public function changeEmail($email, $password, $idUser)
        {
            $json = array();
            
            // check if email is unique
            $query = "SELECT * FROM ".$this->userTable." WHERE email = '$email' AND id_user != '$idUser'";
            $result = mysqli_query($this->db->getDb(), $query);

            if(mysqli_num_rows($result) > 0)
            {
               $json['success'] = 0;
               $json['message'] = "Ten adres email jest już zajęty!";
            }
            else
            {
                if($this->isPasswordCorrect($password, $idUser))
                {
                    $query = "UPDATE ".$this->userTable. " SET email = '$email' WHERE id_user = '$idUser'";
                    $result = mysqli_query($this->db->getDb(), $query);

                    $json['success'] = 1;
                    $json['message'] = "Adres email został zmieniony!";
                }
                else
                {
                    $json['success'] = 0;
                    $json['message'] = "Nieprawdiłowe hasło!";
                }
            }

            return $json;
        }
        
        public function changePhone($phone, $password, $idUser)
        {
            $json = array();
            
            if($this->isPasswordCorrect($password, $idUser))
            {
                $query = "UPDATE ".$this->profileTable. " SET phone = '$phone' WHERE id_user = '$idUser'";
                $result = mysqli_query($this->db->getDb(), $query);

                $json['success'] = 1;
                $json['message'] = "Numer telefonu został zmieniony!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nieprawdiłowe hasło!";
            }

            return $json;
        }
       
        public function changeState($state, $password, $idUser)
        {
            $json = array();
            
            if($this->isPasswordCorrect($password, $idUser))
            {
                $query = "UPDATE ".$this->profileTable. " SET state = '$state' WHERE id_user = '$idUser'";
                $result = mysqli_query($this->db->getDb(), $query);

                $json['success'] = 1;
                $json['message'] = "Województwo zostało zmienione!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nieprawdiłowe hasło!";
            }

            return $json;
        }
        
        public function changeCity($city, $password, $idUser)
        {
            $json = array();
            
            if($this->isPasswordCorrect($password, $idUser))
            {
                $query = "UPDATE ".$this->profileTable. " SET city = '$city' WHERE id_user = '$idUser'";
                $result = mysqli_query($this->db->getDb(), $query);

                $json['success'] = 1;
                $json['message'] = "Miejscowość została zmieniona!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nieprawdiłowe hasło!";
            }
        
            return $json;
        }
        
        public function changeHobby($hobby, $password, $idUser)
        {
            $json = array();
            
            if($this->isPasswordCorrect($password, $idUser))
            {
                $query = "UPDATE ".$this->profileTable. " SET hobby = '$hobby' WHERE id_user = '$idUser'";
                $result = mysqli_query($this->db->getDb(), $query);

                $json['success'] = 1;
                $json['message'] = "Hobby zostało zmienione!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nieprawdiłowe hasło!";
            }
            
            return $json;
        }
        
        public function changeDescription($description, $password, $idUser)
        {
            $json = array();
            
            if($this->isPasswordCorrect($password, $idUser))
            {
                $query = "UPDATE ".$this->profileTable. " SET description = '$description' WHERE id_user = '$idUser'";
                $result = mysqli_query($this->db->getDb(), $query);

                $json['success'] = 1;
                $json['message'] = "Twój opis został zmieniony!";
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Nieprawdiłowe hasło!";
            }

            return $json;
        }
        
        // login (check if user with specified details exists in database)
        public function signIn($login, $password)
        {
            $query = "SELECT u.id_user, u.account_type FROM ".$this->userTable." u WHERE (login = '$login' OR email = '$login') AND password = '$password'";
            $result = mysqli_query($this->db->getDb(), $query);
            
            if(mysqli_num_rows($result) > 0)
            {
                $row = mysqli_fetch_assoc($result);
                
                // set json response
                $json['success'] = 1;
                $json['message'] = "Zalogowano pomyślnie.";
                $json['id_user'] = $row['id_user'];
                $json['id_profile'] = $this->getProfileId($row['id_user']);
                $json['account_type'] = $row['account_type'];
                $json['has_profile'] = $this->hasProfile($row['id_user']);
            }
            else
            {
                $json['success'] = 0;
                $json['message'] = "Podano błędne dane logowania.";
            }
            
            mysqli_close($this->db->getDb());
            
            return $json;
        }
        
        // registration (insert new user into database)
        public function signUp($login, $email, $password, $accountType)
        {
            // check if login is unique
            if(!$this->isLoginUnique($login))
            {
                $json['success'] = 0;
                $json['message'] = "Użytkownik o podanym loginie już istnieje.";
            }
            else
            {   
                // check if email is unique
                if(!$this->isEmailUnique($email))
                {
                    $json['success'] = 0;
                    $json['message'] = "Użytkownik o podanym adresie email już istnieje.";
                }
                else
                {
                    // add user to databse
                    $query = "INSERT INTO ".$this->userTable." (login, email, password, account_type) VALUES ('$login', '$email', '$password', '$accountType')";
                    $result = mysqli_query($this->db->getDb(), $query);
                        
                    if($result == 1)
                    {
                        $json['success'] = 1;
                        $json['message'] = "Rejestracja zakończona pomyślnie.";
                    }
                }
            }
            
            mysqli_close($this->db->getDb());
            
            return $json;
        }
        
    }
?>

