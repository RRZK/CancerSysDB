#!/bin/bash
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $DIR
echo $(ls)
./build.sh
if [ ! -d "docker/OpenImage/target/" ]
then
    mkdir docker/OpenImage/target/
fi
cp -f target/csys-db.war docker/OpenImage/target/
cd docker/csys-mariadb
if [ ! -d "docker/OpenImage/target/release1" ]
then
    wget http://bifacility.uni-koeln.de/cancersysdb/cancersysSQLdump.tar.gz .
    tar -xvzf cancersysSQLdump.tar.gz
    rm cancersysSQLdump.tar.gz
fi