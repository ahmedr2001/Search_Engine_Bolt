# Search_Engine_Bolt

/// All of this is unused due to docker xD! Jump to the docker section 
-------------------------------------------------------------------------------------------------------
To install MongoDB: 
  - Ubuntu: 
      Run the following commands: <br>
        - wget -qO - https://www.mongodb.org/static/pgp/server-5.0.asc | sudo apt-key add - <br>
        - echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/5.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-5.0.list <br>
        - sudo apt-get update <br>
        - sudo apt-get install -y mongodb-org <br>

To run MongoDB: <br>
  - Linux: <br>
    Run the following commands: <br>
      - sudo systemctl start mongod <br>
-----------------------------------------------------------------------------------
*Docker Section:* 
Just type in the command: 
- docker-compose up --build <br> 
And then navigate to : http://localhost:8081/ 
#Hola !
