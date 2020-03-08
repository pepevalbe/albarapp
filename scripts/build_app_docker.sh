#!/bin/sh
cd /home/ec2-user/albarapp
./mvnw clean install
docker build -t albarapp/albarapp .