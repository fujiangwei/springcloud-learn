pid=`ps -xu | grep java | grep -w sys-b | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]
then
    echo "the sys-b server is running, please stop it first"
else 
    nohup java -Xms128m -Xmx256m -XX:-UseGCOverheadLimit -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heapdump.dump -jar sys-b.jar -jfile=application.yml --spring.profiles.active=prod >b.log 2>&1 &
fi
