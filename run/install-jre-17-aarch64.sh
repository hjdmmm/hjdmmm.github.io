#!/usr/bin/env bash
if ! wget -O /tmp/jre17.tar.gz https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.13%2B11/Openjre17U-jre_aarch64_linux_hotspot_17.0.13_11.tar.gz; then
  echo '下载失败'
  exit 1
fi

if ! tar -zxvf "/tmp/jre17.tar.gz" -C "/usr/local/"; then
  echo '解压失败'
  exit 1
fi

if ! \
cat <<-'EOF' > /etc/profile.d/java.sh
export JRE_HOME=/usr/local/jdk-17.0.13+11-jre
export CLASSPATH=.:${JRE_HOME}/lib:$CLASSPATH
export PATH=$PATH:${JRE_HOME}/bin
EOF
then
  echo '环境变量配置失败'
  exit 1
fi

if ! source /etc/profile; then
  echo '环境变量配置失败'
  exit 1
fi

echo '安装jre17-aarch64成功'
