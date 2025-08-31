#!/usr/bin/env bash

# Run database seeding scripts

usage() { echo "Usage: $0 [-hs] [-e ENVIRONMENT] [-d DOMAIN] [-p PATH]" 1>&2; }
printError() { echo "$1"; exit 1; }

declare    SCRIPT_DIR="./scripts"
declare    DATA_DIR="./scripts/data"

declare -i sslFlag=0
declare    environment="" domain="" data="${DATA_DIR}"
while getopts ":e:d:p:hs" opt; do
  case "${opt}" in
    h) usage && exit 0;;
    s) sslFlag=1;;
    e) environment="${OPTARG}";;
    d) domain="${OPTARG}";;
    p) data="${OPTARG}";;
    *) usage && exit 1;;
  esac
done
shift $((OPTIND-1))

# The order of this array determines the script execution order.
declare -a scripts=( "event_seed.py" "team_seed.py" "division_seed.py" "series_seed.py" )

for s in "${scripts[@]}"; do
  cmd=( "${s}" )
  [ ${sslFlag} -eq 1 ] && cmd+=( "-s" )
  [ -n "${environment}" ] && cmd+=( "-e" "${environment}" )
  [ -n "${domain}" ] && cmd+=( "-d" "${domain}" )
  cmd+=( "-p" "${data}" )
  # Execute
  "${SCRIPT_DIR}/${cmd[*]}"
done
