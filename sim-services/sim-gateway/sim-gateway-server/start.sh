#!/usr/bin/env bash
echo
echo "Starting sim-gateway-server..."
HOME=$(dirname $0)
APP_NAME=sim-gateway-server
VERSION=1.0.2
nohup java -jar ${HOME}/target/${APP_NAME}-${VERSION}.jar > log.log 2>&1 &
echo
