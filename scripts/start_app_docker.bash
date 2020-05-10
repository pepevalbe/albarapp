#!/bin/bash
# Get version from pom, stop all docker containers and create albarapp container
cd /home/ec2-user/albarapp || exit
version=$(./mvnw -q -Dexec.executable=echo -Dexec.args='${project.version}' --non-recursive exec:exec)
docker stop $(docker ps -a -q)
docker run -d -p 80:8085 -v /home/ec2-user/logs-albarapp:/logs --env-file /home/ec2-user/env.albarapp albarapp/albarapp:$version
