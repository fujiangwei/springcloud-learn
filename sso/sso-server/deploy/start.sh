pid=`ps -xu | grep java | grep -w sso-auth | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ]
then
    echo "the sso-auth server is running, please stop it first"
else 
    nohup java -Xms128m -Xmx256m -XX:-UseGCOverheadLimit -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heapdump.dump -jar sso-auth.jar -jfile=application.yml >auth.log 2>&1 &
fi
