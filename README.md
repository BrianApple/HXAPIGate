## 简介

HXAPIGate（中文名：浩心API网关）由HXAPIGate、bootshiroPro组成。
HXAPIGate基于Netty+Shiro开发的一款高性能API网关，对基于REST服务的细粒度API资源的权限管理平台，bootshiroPro是开源项目bootshiro的定制化版本，前端工程则继续使用bootshiro的usthe即可（usthe是基于angular开发的前端项目 https://gitee.com/tomsun28/usthe  ）。
### 特色

目前多数授权管理平台都只单单对api路径资源本身授权，而不能做到更细粒度的权限控制，HXAPIGate通过组合bootshiroPro实现了对“api资源+请求方式”的授权模式。
如：
新增如下四个接口

| 接口路径 | 请求方式 |
|--|--|
| /user/list/*/* | GET |
| /user/list/*/* | POST |
| /user/list/*/* | DELETE |
| /user/list/*/* | PUT |
传统授权模式下，这四个接口会被当做一个接口（因为接口路径一致）授权给第三方，而通过HXAPIGate可分别对每个资源进行授权，当仅仅授权“/user/list/*/* ”+“GET”给第三方平台时，被授权放无法访问同一资源的POST、DELETE、PUT请求当时的接口！

## 感谢
bootshiro 项目及作者，bootshiro项目路径：https://gitee.com/tomsun28/bootshiro](https://gitee.com/tomsun28/bootshiro 
## 参与贡献

1. Fork项目到自己的repo
2. clone到本地
3. 修改代码(dev分支)
4. commit后push到自己的库（dev分支）
5.  pull request
6. 等待作者合并