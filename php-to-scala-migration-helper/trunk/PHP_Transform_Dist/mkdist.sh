
cd ..
zip -r PHP_Transform_Dist/php-to-scala.`date +%Y%m%d`-user.zip PHP_Transform -x '*/CVS/*' -x \*.class -x '*/bin/*'
tar --exclude=CVS --exclude=\*.class --exclude=bin  -zcf PHP_Transform_Dist/php-to-scala.`date +%Y%m%d`-full.tgz PHP_Transform PHP_Transform_Lib
