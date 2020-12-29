pid=`ps -xu | grep java | grep -w sys-a | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]
then
    echo "the sys-a server is running, please stop it first"
else 
    nohup java -Xms128m -Xmx256m -XX:-UseGCOverheadLimit -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heapdump.dump -jar sys-a.jar -jfile=application.yml >a.log 2>&1 &
fi
