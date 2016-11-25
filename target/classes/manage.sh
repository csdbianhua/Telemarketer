#!/bin/sh

if [ -z $1 ] ; then
	echo "Usage : $0 [operation] [args...]"
	exit
fi

PROJECT_PATH=$(cd `dirname $0`; pwd)

cd ${PROJECT_PATH}

if [ "$1" == "start" ]; then
	echo "正在启动..."
	java -classpath ${PROJECT_PATH}:${PROJECT_PATH}/libs/* edu/telemarketer/Server start $2
	exit
fi

echo "没有对应操作.."
