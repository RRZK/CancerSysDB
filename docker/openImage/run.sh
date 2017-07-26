#!/bin/bash
CHECKCOMMANDMYSQL="mysql -h CancersysMariaDB -ucsysUser -pcsysPassword  -e \"select 1\" cancersys;"
while ["$CHECKCOMMANDMYSQL" -ne 0]
do
    echo "Waiting for Mysql to Come up!"
    sleep 20
done
/etc/init.d/tomcat7 start
exec tail -f /var/log/tomcat7/catalina.out