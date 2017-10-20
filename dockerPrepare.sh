#!/bin/bash
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $DIR
echo $(ls)
./build.sh
if [ ! -d "docker/openImage/target/" ]
then
    mkdir docker/openImage/target/
fi
cp -f target/csys-db-pub.war docker/openImage/target/
cd docker/csys-mariadb
if [ ! -d "release1" ]
then
    wget http://bifacility.uni-koeln.de/cancersysdb/cancersysSQLdump.tar.gz .
    tar -xvzf cancersysSQLdump.tar.gz
    rm cancersysSQLdump.tar.gz
    echo "SET GLOBAL innodb_lru_scan_depth=256;" > release1/AAA.sql
fi