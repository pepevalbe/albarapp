#!/bin/bash
# Get version from jar file, stop all docker containers and create albarapp container
cd /home/ec2-user/albarapp || exit
version=$(ls target/albarapp-*.jar | awk -F"-|.jar" '{ print $2 }') || exit
docker stop $(docker ps -a -q) || true
docker run -d -p 80:8085 -v /home/ec2-user/logs-albarapp:/logs --env-file /home/ec2-user/env.albarapp albarapp/albarapp:$version