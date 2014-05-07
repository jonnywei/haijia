#!/bin/sh
baseDirForScriptSelf=$(cd "$(dirname "$0")"; pwd)

TWITTER_HOME=`dirname $baseDirForScriptSelf`
TWITTER_LOG_HOME=/home/wjj/log

isUserScan=$1
Main_Class="com.sohu.wap.HaijiaNetMain"
if [  $1 =='scan' ]  then
    Main_Class="com.sohu.wap.HaijiaNetScanner"
fi
#export LANG=zh_CN.GBK
#export JAVA_HOME=/usr/local/jdk
#export PATH=$JAVA_HOME/bin:$PATH
#CLASSPATH=.:$JAVA_HOME/lib:$JAVA_HOME/lib/tools.jar:$JAVA_HOME/jre/lib/ext/*.jar
CLASSPATH=.

for i in "$TWITTER_HOME"/lib/*.jar
do
    CLASSPATH="$i:$CLASSPATH"
done

CLASSPATH=$TWITTER_HOME/config:$CLASSPATH

export CLASSPATH

java -server -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=70 -XX:NewSize=20m -XX:PermSize=80m  -XX:MaxPermSize=256m -Xss256K -Xms40m -Xmx500m -Dsun.rmi.transport.tcp.responseTimeout=5000 -Dsun.rmi.dgc.server.gcInterval=3600000 -XX:+DisableExplicitGC -verbose:GC -Xloggc:$TWITTER_LOG_HOME/rmi_gc.log com.sohu.wap.HaijiaNetMain   2>&1  
echo "Start Haijia-Yuche SUCCESS!"
