#!/bin/bash 
basedir=`dirname $0`

if [ $# -ne 3 ]
then
    echo "Usage: install_samples.sh [IP:PORT] [USERNAME] [PASSWORD]"
    echo "IP:PORT is your Inceptor address"
    echo "USERNAME is your username to inceptor"
    echo "PASSWORD is the password for your username"
    exit 1
fi

samples=$basedir/../samples
inceptor=$1
username=$2
password=$3

MIDAS_HOME=$HOME/.Midas
target="$MIDAS_HOME/repositories/Local_Repository/"

if  [ ! -d $target ] 
then
  echo "Target folder not exist, create an empty one: $target"
  mkdir -p $target
fi
echo "Copy files from source: $samples to target: $target"
cp -rf $samples "$target"
echo "Finish copying"

echo "Start replacing Inceptor host/port"
for dir in $target/samples/*/
do
    for f in $dir/*.rmp
    do
        echo "Handling file: $f"
        sed -i "s|jdbc:hive2:\/\/[0-9]\+\.[0-9]\+\.[0-9]\+\.[0-9]\+:[0-9]\+|jdbc:hive2:\/\/$inceptor|g" $f
        sed -i "s|key=\"username\" value=\"[a-zA-Z0-9]\+\"|key=\"username\" value=\"$username\"|g" $f
        sed -i "s|key=\"password\" value=\"[a-zA-Z0-9]\+\"|key=\"password\" value=\"$password\"|g" $f
    done
done
echo "Finish replacing Inceptor host/port"
