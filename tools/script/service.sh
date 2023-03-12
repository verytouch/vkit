APP_NAME=$2

if [ "" == "$APP_NAME" ]; then
  echo "two params needed: param1=start|stop|restart|status, param2=app_name"
  exit 0
fi

check() {
  pid=`pgrep -f $APP_NAME.jar`
}

start() {
  check
  if [ "" != "$pid" ]; then
    echo "$APP_NAME already running, pid=$pid"
  else
    echo "starting $APP_NAME.jar"
    nohup java -server -Xmx128m -Xloggc:$APP_NAME.gc -jar $APP_NAME.jar > $APP_NAME.log 2>&1 &
    sleep 3
    check
    if [ "" != "$pid" ]; then
      echo "$APP_NAME start success, pid=$!"
    else
      echo "$APP_NAME start failed in 3s"
    fi
  fi
}

stop() {
  check
  if [ -z "$pid" ]; then
      echo "$APP_NAME not running"
    else
      kill -9 $pid
      echo "$APP_NAME stoped, pid=$pid"
  fi
}

restart() {
  stop
  sleep 3
  start
}

case "$1" in
  "status")
    check
    if [ -z "$pid" ]; then
      echo "$APP_NAME not running"
    else
      echo "$APP_NAME is running, pid=$pid"
   fi
  ;;
  "start")
    start
  ;;
  "stop")
    stop
  ;;
  "restart")
    restart
  ;;
  *)
    echo "two params needed: param1=start|stop|restart|status, param2=app_name"
  ;;
esac

exit 0
