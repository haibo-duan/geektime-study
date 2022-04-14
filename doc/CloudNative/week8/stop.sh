#!/bin/sh

echo ""
echo "begin to shutdown..."

#根据端口号查一下pid
PID=$(ps -ef   |grep java |grep httpserver |grep -v "grep" |awk '{print $2}')

if [ ! -n "$PID" ];
  then
    echo ""
    echo "pid is not exist"
    exit 1
fi

#do not use "kill -9" for stoppint service
kill $PID

#看看进程还在吗
UP_STATUS=$(ps -ef   |grep java |grep httpserver |grep -v "grep" |awk '{print $2}' | wc -l)

if (($UP_STATUS <= 0)); 
  then
    echo "shutdown succesful..."
  else
    echo ""
    echo "pid = $PID"
    TIME_OUT=90
    TIME_COUNT=0
    while [ $TIME_COUNT -le $TIME_OUT ]
    do
      UP_STATUS=$(ps -ef   |grep java |grep httpserver |grep -v "grep" |awk '{print $2}' | wc -l)
      if (($UP_STATUS <= 0))
      then
        TIME_COUNT=999
      else
        TIME_COUNT=$(($TIME_COUNT+1))
      fi
      echo "waiting for shutdown ..."
      sleep 1
    done  
    if (($UP_STATUS <= 0))
      then
        echo ""
        echo "shutdown successful..."
      else
        echo ""
        ps -ef | grep "$PID" | grep -v grep | grep "$SERVER_NAME"
        echo ""
        echo "shutdown failed..."
    fi
   
fi