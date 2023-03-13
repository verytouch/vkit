APP_NAME=$2
TIPS="two params needed: param1=start|stop|restart|check, param2=app_name"

if [ "" == "$APP_NAME" ]; then
  echo "$TIPS"
  exit 0
fi

find_pid() {
  pid=$(pgrep -f "$APP_NAME".jar)
}

start() {
  find_pid
  if [ -n "$pid" ]; then
    echo "$APP_NAME already running, pid=$pid"
  else
    echo "starting $APP_NAME.jar..."
    nohup java -server -Xmx128m -Xloggc:"$APP_NAME".gc -jar "$APP_NAME".jar > "$APP_NAME".log 2>&1 &
    sleep 3
    find_pid
    if [ -n "$pid" ]; then
      echo "$APP_NAME start success, pid=$!"
    else
      echo "$APP_NAME start failed in 3s"
    fi
  fi
}

stop() {
  find_pid
  if [ -z "$pid" ]; then
      echo "$APP_NAME not running"
    else
      echo "stopping $APP_NAME.jar using kill..."
      kill "$pid"
      sleep 3
      find_pid
      if [ -z "$pid" ]; then
        echo "$APP_NAME stop success"
      else
        echo "$APP_NAME stop failed in 3s"
        echo "stopping $APP_NAME.jar using kill -9..."
        kill -9 "$pid"
        echo "$APP_NAME stop success"
      fi
  fi
}

restart() {
  stop
  start
}

case "$1" in
  "check")
    find_pid
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
    echo "$TIPS"
  ;;
esac

exit 0
