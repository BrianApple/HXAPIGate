#!/bin/bash
# HXBootShiro 本地开发启动脚本（dev profile）
# 用法：./start_bootshiro.sh
# 环境变量（可选）：
#   HXAPI_JWT_SECRET   生产环境务必设置强随机 JWT 签名密钥（管理端与网关必须一致）
#   HXAPI_DB_USERNAME  数据库用户名（默认取 application.yml dev 配置）
#   HXAPI_DB_PASSWORD  数据库口令（默认取 application.yml dev 配置）
set -e
cd "$(dirname "$0")/HXBootShiro"

# 生成本地开发 JWT 密钥（首次运行需手动创建 ~/.hxapigate_jwt_secret；生产请用独立强密钥）
if [ -z "${HXAPI_JWT_SECRET:-}" ] && [ -f ~/.hxapigate_jwt_secret ]; then
  export HXAPI_JWT_SECRET="$(cat ~/.hxapigate_jwt_secret)"
fi

exec /opt/jdk-21/bin/java \
  -jar target/HXBootShiro.jar \
  --spring.profiles.active=dev
