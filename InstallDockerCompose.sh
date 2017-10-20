#!/bin/bash
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $DIR
echo $(ls)
./dockerPrepare.sh
cd docker
docker-compose build
#docker-compose run -p 8080:8080 cancersys