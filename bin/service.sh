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

print_all_log(){
tail -1f log.log | sed --unbuffered -e 's/\(.*FATAL.*\)/\o033[1;31m\1\o033[0;39m/' -e 's/\(.*ERROR.*\)/\o033[31m\1\o033[39m/' -e 's/\(.*FAILED.*\)/\o033[31m\1\o033[39m/' -e 's/\(.*WARN.*\)/\o033[33m\1\o033[39m/' -e 's/\(.*INFO.*\)/\o033[37m\1\o033[39m/' -e 's/\(.*DEBUG.*\)/\o033[34m\1\o033[39m/' -e 's/\(.*TRACE.*\)/\o033[30m\1\o033[39m/' -e 's/\(.*[Ee]xception.*\)/\o033[1;33m\1\o033[0;39m/'
}



LINE="----------------------------------------------------------------------"
declare -A APPS


SIM_HOME=$(cd "$(dirname "$0")";pwd)
SIM_HOME="${SIM_HOME/"/bin"/}"

########### init database #########
db=~/sim.mv.db
if [[ ! -d ~/database ]]
  then
    mkdir ~/database
fi
if [[ ! -f ~/database/sim.mv.db ]]
  then
    cp ${SIM_HOME}/bin/database/sim.mv.db ~/database/sim.mv.db
fi
###################################

. ${SIM_HOME}/bin/log.sh


APPS=(
["sim-auth-server"]="${SIM_HOME}/sim-services/sim-security/sim-auth-server" \
["sim-gateway-server"]="${SIM_HOME}/sim-services/sim-gateway/sim-gateway-server" \
["sim-web"]="${SIM_HOME}/sim-services/sim-web" \
)
APPS_STR=""
for key in ${!APPS[@]}
do
  APPS_STR="${APPS_STR} [${key}]"
done
echo ${APPS_STR}
print_bar "base path is: $SIM_HOME" "usable apps: ${APPS_STR}" "tip: sim-auth-server and sim-gateway-server are required, if you want view web page, must start them"


start() {
if [[ $1 = 'all' ]]
then
    for key in ${!APPS[@]}
    do
        sh ${APPS[$key]}/start.sh
    done
 else
    exist=0
     for key in ${!APPS[@]}
        do
            if [[ ${key} = $1 ]]
            then
                exist=1
                sh ${APPS[$key]}/start.sh
                break
            fi
        done
     if [[ ${exist} = 0 ]]
     then
         log error "no app found : $1"
     fi
fi
echo service is starting, use command '[./service status all]' check service status.

}
stop() {
if [[ $1 = 'all' ]]
then
    for key in ${!APPS[@]}
     do
        sh ${APPS[$key]}/stop.sh
    done
 else
     for key in ${!APPS[@]}
        do
            if [[ ${key} = $1 ]]
            then
                echo ${APPS[$key]}
                sh ${APPS[$key]}/stop.sh
                break
            fi
        done
fi

}

function status() {
if [[ $1 = 'all' ]]
    then
        for key in ${!APPS[@]}
         do
            status ${key}
        done
    else
        PID=$(jps -l | grep $1  | awk '{print $1}')
        if [[ -z ${PID} ]]; then
            printf "\033[31m%-20s %-20s %-20s \033[0m" $1 "Not running"
            echo
        else
            path=$(jps -l | grep $1 | awk '{print $2}')
            port=$(netstat -nap | grep $PID | awk '$1~/tcp/' | awk '{print $4}' | awk 'NR==1{print substr($0,4)}')
            printf "\033[32m%-20s %-20s %-10s %-10s %-20s\033[0m" $1 "Running" "${PID}" "${port}" ${path}
            echo
        fi
    fi
}

function usage() {
	echo "Usage: $0 {start|stop|restart|status|stop -f}"
	echo "Example: $0 start"
	exit 1
}

case $1 in
start)
	start $2
	;;

stop)
	stop $2
	;;

restart)
	restart
	;;

status)
    printf "\033[37m%-20s %-20s %-10s %-10s %-20s \033[0m" "name" "status" "pid" "port" "path"
    echo
    echo ${LINE}
	status $2
	;;

*)
	usage
	;;
esac


