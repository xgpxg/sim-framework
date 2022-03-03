#!/usr/bin/env bash
echo "Starting sim-db-dif..."
HOME=$(dirname $0)
APP_NAME=sim-db-dif
VERSION=1.0.2
java -jar ${HOME}/target/${APP_NAME}-${VERSION}.jar > log.log 2>&1 &
echo
