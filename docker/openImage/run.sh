#!/bin/bash
while ! $(mysql -h CancersysMariaDB -ucsysUser -pcsysPassword  -e "select 1" cancersys;)
do
    echo "Waiting for Mysql to Come up!"
    sleep 60
done
/etc/init.d/tomcat7 start
exec tail -f /var/log/tomcat7/catalina.out