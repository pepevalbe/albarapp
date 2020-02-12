#!/bin/sh
kill `cat /home/ec2-user/albarapp.pid`
rm /home/ec2-user/albarapp.pid
source /home/ec2-user/setenv.sh
nohup java -jar -Dspring.profiles.active=production /home/ec2-user/albarapp/target/*.jar >/dev/null 2>&1 & echo $! > /home/ec2-user/albarapp.pid