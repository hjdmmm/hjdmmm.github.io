java_path=$(which java);
if ! java_path; then
  echo '未找到Java'
  exit 1
fi

if ! \
sed "s|replace_your_java_path|${java_path}|g" blog.service > /etc/systemd/system/blog.service
then
  echo '生成服务失败'
  exit 1
fi

if ! systemctl enable blog.service; then
  echo '设置服务开机自启失败'
  exit 1
fi

echo '设置博客服务blog.service开机自启成功'
echo 'journalctl -fu blog.service查看日志'
