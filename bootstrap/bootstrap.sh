#! /bin/bash


#mkdir cs5300_p1
#cd cs5300_p1
#touch config.csv
touch servers.txt
#set environmental vars
sudo echo "export AWS_ACCESS_KEY_ID=AKIAJKIFIX34IMXFC5ZQ" >> /tmp/profile
sudo echo "export AWS_DEFAULT_REGION=us-east-1" >> /tmp/profile
sudo echo "export AWS_DEFAULT_OUTPUT=text" >> /tmp/profile
sudo echo "export AWS_SECRET_ACCESS_KEY=toXaBtobc78s5Ku6XKESco0ylWrgqI77oaSohPYj" >> /tmp/profile

# set environment vars for W, WQ, R, N, F, RPC_PORT, MAX_MESSAGE_SIZE, SESSION_TIMEOUT_MILLIS
# ACK_TIMEOUT_MILLISECONDS, REBOOT_ID, SERVER_FILEPATH, and SIMPLEDB_ADDRESS
sudo echo 'export W=2' >> /tmp/profile
sudo echo 'export WQ=2' >> /tmp/profile
sudo echo 'export R=2' >> /tmp/profile
sudo echo 'export N=2' >> /tmp/profile
sudo echo 'export F=1' >> /tmp/profile

IP_pub_addr=$(ec2-metadata -v | cut -d  ':' -f 2 | sed 's/^.\{1\}//')
IP_addr=$(ec2-metadata -o | cut -d  ':' -f 2 | sed 's/^.\{1\}//')
Server_index=$(ec2-metadata -l | cut -d  ':' -f 2 | sed 's/^.\{1\}//')

sudo echo "export PUBLIC_IP=$IP_pub_addr" >> /tmp/profile
sudo echo "export PRIVATE_IP=$IP_addr" >> /tmp/profile
sudo echo "export SERVER_INDEX=$Server_index" >> /tmp/profile

sudo echo 'export RPC_PORT=5300' >> /tmp/profile
sudo echo 'export MAX_MESSAGE_SIZE=1024' >> /tmp/profile
sudo echo 'export SESSION_TIMEOUT_MILLISECONDS=86400000' >> /tmp/profile
sudo echo 'export ACK_TIMEOUT_MILLISECONDS=2000' >> /tmp/profile
sudo echo 'export REBOOT_ID=0' >> /tmp/profile
sudo echo 'export SERVER_FILEPATH=servers.txt' >> /tmp/profile
sudo echo 'export SIMPLEDB_ADDRESS=/stub/address/to/db' >> /tmp/profile

touch tomcat8.conf

sudo echo 'W=2' >> tomcat8.conf
sudo echo 'WQ=2' >> tomcat8.conf
sudo echo 'R=2' >> tomcat8.conf
sudo echo 'N=2' >> tomcat8.conf
sudo echo 'F=1' >> tomcat8.conf

sudo echo "PUBLIC_IP=$IP_pub_addr" >> tomcat8.conf
sudo echo "PRIVATE_IP=$IP_addr" >> tomcat8.conf
sudo echo "SERVER_INDEX=$Server_index" >> tomcat8.conf

sudo echo 'RPC_PORT=5300' >> tomcat8.conf
sudo echo 'MAX_MESSAGE_SIZE=1024' >> tomcat8.conf
sudo echo 'SESSION_TIMEOUT_MILLISECONDS=86400000' >> tomcat8.conf
sudo echo 'ACK_TIMEOUT_MILLISECONDS=2000' >> tomcat8.conf
sudo echo 'REBOOT_ID=0' >> tomcat8.conf
sudo echo 'SERVER_FILEPATH=servers.txt' >> tomcat8.conf
sudo echo 'SIMPLEDB_ADDRESS=/stub/address/to/db' >> tomcat8.conf


sudo bash -c 'cat /tmp/profile >> /etc/profile'
sudo bash -c 'cat tomcat8.conf >> /etc/tomcat8/tomcat8.conf'

source /etc/profile
#install java 8 and remove java 7
sudo yum -y install java-1.8.0 java-1.8.0-openjdk-devel
sudo yum -y erase java-1.7.0
#install tomcat8
sudo yum -y install tomcat8-webapps
sudo yum -y install tomcat8-docs-webapp
sudo yum -y install tomcat8-admin-webapps

aws s3 cp s3://edu-cornell-cs-cs5300s16-project1b/reboot.sh .
chmod 755 reboot.sh
#aws s3 fetch war file
aws s3 cp s3://edu-cornell-cs-cs5300s16-project1b/project1b.war .
aws s3 cp s3://edu-cornell-cs-cs5300s16-project1b/redeploy_war_file.sh .
# puts WAR file in webapps folder and starts TomCat
sudo cp project1b.war /var/lib/tomcat8/webapps
sudo service tomcat8 start
