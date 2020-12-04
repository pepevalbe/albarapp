#!/bin/bash
# Get version from albarapp jar file and build docker image
cd /home/ec2-user/albarapp || exit
version=$(ls target/albarapp-*.jar | awk -F"-|.jar" '{ print $2 }') || exit
docker build -t albarapp/albarapp:$version .
