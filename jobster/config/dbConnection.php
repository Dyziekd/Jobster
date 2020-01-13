<?php
    
    include_once 'dbConfiguration.php';
    
    class Connection
    {
        private $connection;
        
        public function __construct()
        {
            $this->connection = mysqli_connect(host, username, password, dbName);
        }
        
        public function getDb()
        {
            return $this->connection;
        }
    }    
?>