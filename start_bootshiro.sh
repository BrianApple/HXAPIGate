#!/bin/bash
# HXBootShiro 本地测试启动脚本（不修改提交的 application.yml）
# --add-opens: Ignite 2.8 在 JDK 21 下需要开放模块访问（P0 过渡期；P4 移除 Ignite 后可去掉）
export JAVA_HOME=/opt/jdk-21
cd /data/hermes_files/HXAPIGate/HXBootShiro
exec /opt/jdk-21/bin/java \
  --add-opens java.base/java.nio=ALL-UNNAMED \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  --add-opens java.base/java.util=ALL-UNNAMED \
  --add-opens java.base/java.lang.reflect=ALL-UNNAMED \
  --add-opens java.base/sun.nio.ch=ALL-UNNAMED \
  --add-opens java.base/java.net=ALL-UNNAMED \
  -jar target/HXBootShiro.jar \
  --spring.datasource.url='jdbc:mysql://127.0.0.1:13306/hxapigate?useUnicode=true&characterEncoding=utf-8&useSSL=false' \
  --spring.datasource.username=root \
  --spring.datasource.password=iotgate123 \
  --server.port=18080
