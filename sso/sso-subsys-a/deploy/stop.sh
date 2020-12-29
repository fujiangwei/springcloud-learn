pid=`ps -xu | grep java | grep -w sys-a | grep -v grep | awk '{print $2}'`
if [ -n "$pid" ];then
    kill -9 $pid
fi
