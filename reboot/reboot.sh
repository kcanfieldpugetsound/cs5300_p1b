#! /bin/bash
#reboot script here!
#make call to simple DB
#update files

IP_pub_addr=$(ec2-metadata -v | cut -d  ':' -f 2 | sed 's/^.\{1\}//')
sudo echo "export PUBLIC_IP=$IP_pub_addr" >> /tmp/profile
sudo echo "PUBLIC_IP=$IP_pub_addr" >> tomcat8.conf

INC_REBOOT=$(( $REBOOT_ID + 1 ))
echo "export REBOOT_ID=$INC_REBOOT" >> /tmp/profile
sudo bash -c 'cat /tmp/profile >> /etc/profile'
sudo bash -c 'cat tomcat8.conf >> /etc/tomcat8/tomcat8.conf'
source /etc/profile
sudo service tomcat8 restart