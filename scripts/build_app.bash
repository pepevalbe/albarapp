#!/bin/bash
# Build albarapp jar 
cd /home/ec2-user/albarapp || exit
./mvnw clean install
