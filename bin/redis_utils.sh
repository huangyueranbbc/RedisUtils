#!/usr/bin/env bash
if [ -z "${JAVA_HOME}" ]; then
  echo "JAVA_HOME NOT FOUND!"
  exit -1
fi

APP_NAME="RedisUtils"     #如果是jar方式启动，必须与jar文件名相同

#FILE PATH
JAR_PATH="/"

#OPERATION TYPE
OPERATION_TYPE="1"
#input host:port. 集群模式支持只输出某一节点地址.
REDIS_HOST="192.168.0.193:7001"

# 变量定义规则，如果没有可删除或将变量值设置为""
# java参数
# JAVA_OPTS="-Xmx1024m"
JAVA_OPTS=""
# gc参数
# CUR_DATE=`date +%Y%m%d`
# GC_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=600 -Xloggc:${JAR_PATH}/${APP_NAME}_${CUR_DATE}_gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+PrintTenuringDistribution -XX:+PrintHeapAtGC -XX:+HeapDumpBeforeFullGC -XX:+PrintClassHistogramBeforeFullGC -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=${JAR_PATH}/${APP_NAME}_${CUR_DATE}_oom_dump.log"
GC_OPTS="

nohup java -jar ${JAVA_OPTS} ${GC_OPTS} ${JAR_PATH}/${APP_NAME}.jar ${OPERATION_TYPE} ${REDIS_HOST} >>/dev/null 2>&1 &
