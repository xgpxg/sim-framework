#!/usr/bin/env bash
LINE="-------------------------------------------------------"

print_bar(){
echo ${LINE}
COUNT=0
while  [ $# -gt 0 ]
do
    echo $1
    shift
    let COUNT=COUNT+1
done
echo ${LINE}
}

SIM_HOME=$(cd "$(dirname "$0")";pwd)
SIM_HOME="${SIM_HOME/"/bin"/}"

print_bar "start build..." "base path is: $SIM_HOME"

mvn -f ${SIM_HOME}/sim-services/pom.xml clean package -Dmaven.test.skip=true

print_bar "build finished"