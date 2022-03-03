#!/usr/bin/env bash
echo
echo "Starting sim-web..."
HOME=$(dirname $0)
APP_NAME=sim-web
VERSION=1.0.2

nohup java -jar ${HOME}/target/${APP_NAME}-${VERSION}.jar -Dspring.profiles.active=test > log.log 2>&1 &

echo
