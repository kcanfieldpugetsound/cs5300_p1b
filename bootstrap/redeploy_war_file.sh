#!/bin/bash
aws s3 cp s3://edu-cornell-cs-cs5300s16-project1b/project1b.war .
# puts WAR file in webapps folder and starts TomCat
sudo cp project1b.war /var/lib/tomcat8/webapps
sudo service tomcat8 start