
exec 2>&1

LIBJARS=../lib/php-scala.jar:../lib/quercus.jar:../lib/javaee-16.jar

[ -d bin ] || mkdir bin

for i in test_*.php
do

  echo ==================== converting: $i

  scala_src=`basename $i .php`.scala
  php ../php-to-scala/php-to-scala.php $i > $scala_src
  wc $scala_src
  
  echo ==================== compiling: $scala_src

  scalac -d bin -classpath $LIBJARS $scala_src
  class=`echo $i | sed 's/\./_/g'`

  echo ==================== running: $class

  java -classpath bin:/usr/share/scala/lib/scala-library.jar:$LIBJARS $class 50

done
