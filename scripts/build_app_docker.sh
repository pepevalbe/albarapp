#!/bin/sh
# Get version from pom, build jar and build docker image
cd /home/ec2-user/albarapp
version=$(./mvnw -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
./mvnw clean install
docker build -t albarapp/albarapp:$version .