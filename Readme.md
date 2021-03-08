# Message board Rest API

**This is a Java Spring Boot application, that implements a Rest Api for a message board.**

## Project Structure

The Project uses Java 13, Spring Boot, Maven, H2 in memory database and Docker.

## Building and running

Prerequisites: JDK 13 installed on the machine, Docker if the docker container is to be used 

### Run the tests

To run the tests, execute the following command (for Windows, the ``./`` is not needed):

```
./mvnw test
```

### Run without Docker

To build the project with Maven, execute the following command (for Windows, the ``./`` is not needed):
```
./mvnw package
```

To run the project with maven, execute the following command:
```
./mvnw spring-boot:run
```

###Run with Docker

Optionally, it's possible to run the application in a Docker container. The script startDocker.sh/startDocker.cmd
runs the Maven build, the Docker build and starts the container in detached mode:
 
```
./startDocker.sh or startDocker.cmd
```

To stop the Docker container, execute the stopDocker.sh/stopDocker.cmd script:
```
./stopDocker.sh or stopDocker.cmd
```

## CRUD Requests
The API has the following CRUD operations:
 - GET http:/localhost:8080/messages - returns a list of all messages
 - GET http:/localhost:8080/messages/{id} - returns a message with a given id
 - POST http:/localhost:8080/login - performs authentication against the user database, returns a token that should be used for Bearer
    authentication for any modifying requests
 - POST http:/localhost:8080/messages - adds a message, requires authentication
 - PUT http:/localhost:8080/messages/{id} - updates the message with the given id, only authorized for the user that created the message
 - DELETE http:/localhost:8080/messages/{id} - deletes the message with the given id, only authorized for the user that created the message

## Authentication
The GET requests and login request does not require authentication, all other requests require that. The authentication
flow is as follows:
 - POST request to http:/localhost:8080/login, with the json body ``{"username":"john.doe@email.com", "password":"password"}``
 - The response is the required token, in JWT format
 - In subsequent requests, include an authorization header: ``Authorization: Bearer {jwtToken}``

## Default user credentials
The users added by default are:
- john.doe@email.com, password: password
- sam.doe@email.com, password: password

## Testing the API requests
There is a shell script set up to test the CRUD requests. It will execute all the CRUD operations defined above (*nix only):
```
./requests.sh
```