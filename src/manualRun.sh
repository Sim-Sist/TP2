#!/bin/bash
mainClass=App

scriptDir=`dirname $0`
classDir=${scriptDir}/../bin;
sourceDir=${scriptDir}/main/java
find ${sourceDir} -name "*.java" > ${scriptDir}/utils/sources.txt
javac -d ${classDir} @${scriptDir}/utils/sources.txt;
java -Xmx6G -cp ${classDir} ${mainClass};