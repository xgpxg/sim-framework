#!/usr/bin/env bash
echo "Starting sim-cache-server..."
HOME=$(dirname $0)
APP_NAME=sim-cache-server
VERSION=1.0.2
nohup java -jar ${HOME}/target/${APP_NAME}-${VERSION}.jar > log.log 2>&1 &
