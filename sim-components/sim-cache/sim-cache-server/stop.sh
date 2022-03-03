#!/usr/bin/env bash
echo
echo "Stopping sim-cache-server..."
HOME=$(dirname $0)
APP_NAME=sim-cache-server
VERSION=1.0.2
APP=${APP_NAME}-${VERSION}.jar
PID=$(jps -l | grep ${APP}  | awk '{print $1}')
if [[ ! -n ${PID} ]]
then
    echo "${APP_NAME} Stopped"
else
    kill -9 ${PID}
    if [[ $? != 0 ]]
    then
        echo -e "\033[31mStop fail!\033[0m"
    else
        echo "${APP_NAME}(${PID}) Stopped!"
    fi
fi
echo

