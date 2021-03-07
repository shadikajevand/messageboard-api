#!/bin/sh

curl -s localhost:8080 > /dev/null
if [ "$?" = "7" ]; then
    echo "The application isn't running. Start it by executing ./mvnw spring-boot:run' or ./startDocker.sh "
    exit 1
fi

echo '########################################## Get all messages: ####################################################'
curl -s localhost:8080/messages | json_pp


echo '######################################### Login: ###############################################################'
TOKEN=$(curl -s \
  -d '{"username":"john.doe@email.com", "password":"password"}' \
  -H "Content-Type: application/json" \
  -X POST localhost:8080/login)

echo 'Logged in, token: '
echo $TOKEN


echo '######################################## Post, add message: ##################################################'
MESSAGE=$(curl -s -d '{"subject":"subject", "content":"content"}' \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -X POST localhost:8080/messages)

echo $MESSAGE | json_pp
MESSAGEID=$(echo $MESSAGE | grep -Po '"id":\d*?,' | grep -Po '\d*')

echo '########################################## Get latest message: ####################################################'
curl -s "localhost:8080/messages/$MESSAGEID" | json_pp

echo '######################################## Put, update message: ##################################################'
curl -s -d '{"subject":"subjectUpdated", "content":"contentUpdated"}' \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -X PUT "localhost:8080/messages/$MESSAGEID" | json_pp

echo '######################################## Delete, delete message: ##################################################'
curl -s -i \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -X DELETE "localhost:8080/messages/$MESSAGEID"
