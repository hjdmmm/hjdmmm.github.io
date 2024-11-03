if ! apt-get install mysql-server-5.7 -y; then
  echo 'MySQL安装失败'
  exit 1
fi
