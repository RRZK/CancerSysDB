#!/bin/bash
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
cd $DIR
echo $(ls)
./dockerPrepare.sh
cd docker
docker-compose build