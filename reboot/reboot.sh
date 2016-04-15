#! /bin/bash
#reboot script here!
#make call to simple DB
#update files
INC_REBOOT=$(( $REBOOT_ID + 1 ))
echo "export REBOOT_ID=$INC_REBOOT" >> /tmp/profile
sudo bash -c 'cat /tmp/profile >> /etc/profile'
source /etc/profile
sudo service tomcat8 restart
