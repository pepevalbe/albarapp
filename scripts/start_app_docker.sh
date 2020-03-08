#!/bin/sh
# Stop all docker containers, remove all stopped docker images, create albarapp container
docker stop $(docker ps -a -q)
docker system prune --force
docker run -d -p 80:8085 --env-file /home/ec2-user/env.albarapp albarapp/albarapp