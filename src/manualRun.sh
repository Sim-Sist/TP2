#!/bin/bash
mainClass=Main

scriptDir=`dirname $0`
classDir=${scriptDir}/../bin;
sourceDir=${scriptDir}/main/java
libsDir=${scriptDir}/../libs
log4jDir=${libsDir}/apache-log4j-2.17.2-bin

find ${sourceDir} -name "*.java" > ${scriptDir}/utils/sources.txt
javac -d ${classDir} @${scriptDir}/utils/sources.txt;
java -Xmx6G -cp ${classDir} ${mainClass} ${@:1};