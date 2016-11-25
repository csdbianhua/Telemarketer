##!/bin/sh
#
#OUTPUT_PATH=$1
#if [ -z ${OUTPUT_PATH} ]; then
#	echo "Usage : $0 output_path"
#	exit
#fi
#if [ -d ${OUTPUT_PATH} ];then
#	read -p "所输入的目录已存在,是否覆盖 : (y/n) " yn
#	if [ "$yn" != "Y" ] && [ "$yn" != "y" ]; then
#		echo "build终止.."
#		exit
#	else
#		rm -rf ${OUTPUT_PATH}
#	fi
#fi
#
#
#PROJECT_PATH=$(cd `dirname $0`; pwd)
#SRC_PATH=${PROJECT_PATH}/src
#RESOURCE_PATH=${PROJECT_PATH}/resources
#
#find ${SRC_PATH} -name *.java > ${SRC_PATH}/sources.list
#
#echo "正在复制资源.."
#cp -r ${RESOURCE_PATH} ${OUTPUT_PATH}
#
#CLASSPATH=.
#for filename in `ls ${OUTPUT_PATH}/libs/`
#    do
#        CLASSPATH=${CLASSPATH}:${OUTPUT_PATH}/libs/${filename}
#    done
#export CLASSPATH
#
#echo "正在编译工程..."
#javac -d ${OUTPUT_PATH} @${SRC_PATH}/sources.list
#
#echo "编译完成..."
#rm ${SRC_PATH}/sources.list
#
#chmod +x ${OUTPUT_PATH}/manage.sh
