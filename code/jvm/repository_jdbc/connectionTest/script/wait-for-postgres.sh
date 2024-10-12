#!/bin/sh
# specify the shell for the script

set -e
# set the shell to exit immediately if a command exits with a non-zero status

host="$1"
# set the host variable to the first argument passed to the script
shift
# remove the first argument from the list of arguments
cmd="$*"
# set the cmd variable to the remaining arguments

>&2 echo "waiting for postgres on $host"
until pg_isready -h "$host"; do
# until the pg_isready command returns true
  >&2 echo "Postgres is unavailable - sleeping"
  # print a message to stderr
  sleep 1
  # sleep for 1 second
done

>&2 echo "Postgres is up - executing command '$cmd'"
# print a message to stderr
exec "$cmd"
# execute the command passed to the script