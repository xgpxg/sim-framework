#!/usr/bin/env bash
loglevel=0 #debug:0; info:1; warn:2; error:3
logfile=$0".log"
function log {
        local msg;local logtype
        logtype=$1
        msg=$2
        datetime=`date +'%F %H:%M:%S'`
        logformat="[${logtype}] ${msg}"
        {
        case $logtype in
                debug)
                        [[ $loglevel -le 0 ]] && echo -e "\033[34m${logformat}\033[0m" ;;
                info)
                        [[ $loglevel -le 1 ]] && echo -e "\033[37m${logformat}\033[0m" ;;
                warn)
                        [[ $loglevel -le 2 ]] && echo -e "\033[33m${logformat}\033[0m" ;;
                error)
                        [[ $loglevel -le 3 ]] && echo -e "\033[31m${logformat}\033[0m" ;;
        esac
        }
}