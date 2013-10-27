#!/bin/sh
CP=bin:/usr/share/scala/lib/scala-library.jar:../lib/php-scala.jar:../lib/quercus.jar:../lib/javaee-16.jar
java -server -classpath $CP test_1_php $*
