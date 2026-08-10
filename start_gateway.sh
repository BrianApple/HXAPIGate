#!/bin/bash
# HXAPIGate 网关本地开发启动脚本
# 用法：./start_gateway.sh
# 环境变量（可选）：
#   HXAPI_JWT_SECRET   生产环境务必设置强随机 JWT 签名密钥（管理端与网关必须一致）
# 前置依赖：
#   - Redis（127.0.0.1:6379，路由缓存与分布式限流，见 DistributeCacheInfo.xml）
#   - 管理端 HXBootShiro 已启动（API 路由通过 Redis 下发）
set -e
cd "$(dirname "$0")/HXAPIGate"

# 生成本地开发 JWT 密钥（首次运行需手动创建 ~/.hxapigate_jwt_secret；生产请用独立强密钥）
if [ -z "${HXAPI_JWT_SECRET:-}" ] && [ -f ~/.hxapigate_jwt_secret ]; then
  export HXAPI_JWT_SECRET="$(cat ~/.hxapigate_jwt_secret)"
fi

exec /opt/jdk-21/bin/java \
  -jar target/HXAPIGate-3.0.1-SNAPSHOT.jar
