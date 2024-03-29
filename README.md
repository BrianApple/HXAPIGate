## 简介
### 更新说明
- 当前：
    
    增加接口熔断功能，同时对新增接口的路由信息增加安全限制保障服务运行安全等

### 概念
HXAPIGate（中文名：浩心API网关）——如果觉得可以请star本项目。
HXAPIGate基于Netty+Shiro开发的一款高性能API网关，对基于REST服务的细粒度API资源的权限管理平台，支持http,dubbo等多协议微服务接口代理。**——本软件著作权归原作者所有**
![输入图片说明](https://images.gitee.com/uploads/images/2019/1112/152324_e14eb0c7_1038477.png "屏幕截图.png")

### 软件特色

目前多数授权管理平台都只单单对api路径资源本身授权，而不能做到更细粒度的权限控制，HXAPIGate通过组合bootshiroPro实现了对“api资源+请求方式”的授权模式。
如：
新增如下四个接口

| 接口路径 | 请求方式 |
|--|--|
|“/user/list”| GET |
|“/user/list”| POST |
|“/user/list”| DELETE |
|“/user/list”| PUT |

传统授权模式下，这四个接口会被当做一个接口（因为接口路径一致）授权给第三方，**而通过HXAPIGate可分别对每个资源进行授权，当仅仅授权“/user/list”+“GET”给第三方平台时，被授权放无法访问同一资源的POST、DELETE、PUT请求当时的接口！**


### MECHA--机甲

浩心网关是微服务思想结合mecha 思想落地的产物。如下图所示，描述微服务与浩心网关的关系，内部浅绿色区域为业务相关微服务区，浩心网关所在区域为外部分布式特性区域，
由图可知，微服务不需要考虑任何分布式特性，更不需要在服务的生命周期中引入与业务功能不相关的任何第三方分
布式组件特性（典型如spring cloud全家桶），当一个业务微服务单元发布之后，浩心网关会直接赋予该服务所有分
布式组件特性。当然大家如果了解过service mesh，就会发现与其有神似之处，未来将浩心网关打造成一款工业级sidecar,
也是我希望的能够达到的目标之一。

![输入图片说明](https://images.gitee.com/uploads/images/2020/0904/211047_342c4125_1038477.png "HXAPIGate.png")


### 为什么不选择springCloud or dubbo
- 不选择springcloud的原因很简单，除非贵公司项目全是新上马的项目，没有任何历史遗留问题——就算果真如此，在某些情况下也不建议使用springCloud（springCloud的学习成本等因素）--不选择
springCloud的原因就是因为考虑到很多公司遗留的一些历史问题，无法改用springCloud,但是如果在无法改用dubbo或者springCloud却想在不更改或者极少更改的情况下实现微服务的分布式限流、服务熔断等
分布式服务特性，那么恭喜您，HXAPIGate的目的正是如此。
- 不选择dubbo的原因，首先HXAPIGate其实是兼容了dubbo协议的，用户侧目前统一为HTTP协议，因此虽然API网关本身不对外提供dubbo服务对外服务，但是可以代理dubbo微服务，同时也可以实现对dubbo的分布式限流。


## 项目文档
项目文档请参加项目的Wiki，里面会介绍项目的使用方法已经路由的配置方法等信息。如果觉得项目不错，别忘了给个star！谢谢！


### 授权认证时序图
![授权流程](HXBootShiro/src/main/resources/static/images/img.png "授权流程.jpg")

### 性能
2000并发事务压测报告（jdk1.8，jvm堆内存512M）
![API网关2000并发压测图（jvm=512M）](https://images.gitee.com/uploads/images/2019/1112/113504_8b9b126e_1038477.png "API网关2000并发压测图（jvm=512M）.png")

### 网关部署结构
HXAPIGate支持集群部署，支持被代理接口的分布式限流、负载等，分布式部署时建议部署zookeeper集群提供网关和HXBootshiro授权平台的节点发现机制，本软件所依赖的分布式特性，如分布式限流、分布式缓存
等依靠ignite提供底层能力！
![输入图片说明](HXBootShiro/src/main/resources/static/images/HXAPIGate3D.png)

## 操作演示

### 首页（用户名密码为：admin/123456）
![输入图片说明](HXBootShiro/src/main/resources/static/images/index.png "index.png")
### 新增接口类别
接口类别管理==项目管理，是一类API接口的集合

![输入图片说明](HXBootShiro/src/main/resources/static/images/type.png "type.png")
### 新增接口
管理API接口，对API接口的基本信息（路由、负载策略、协议类型等等）进行管理
![输入图片说明](HXBootShiro/src/main/resources/static/images/api.png "api.png")
新增接口功能截图：
![输入图片说明](HXBootShiro/src/main/resources/static/images/addApi.png "addApi.png")
### 接口授权
以角色为桥梁，分别对用户、API接口进行授权
![输入图片说明](HXBootShiro/src/main/resources/static/images/auth.png "auth.png")

### 目前网关已实现功能
1. 授权、鉴权管理
2. 路由配置
3. 路由负载（轮寻和赋权值）
4. HTTP、dubbo多协议协议
5. 接口分布式限流
6. 金丝雀发布
7. 接口熔断

### 项目进度
 目前HXAPIGate网关对API接口的管理已经基本开发完成，后续主要对API接口支持代理的协议以及网关bug进行扩展和完善
同时将对管理平台的功能进行扩展，提供更加丰富多元的管理功能

### 相关博文

- [HXAPIGate系列——HXAPIGate快速入门](https://blog.csdn.net/sinat_28771747/article/details/126610401?spm=1001.2014.3001.5501)
- [《netty整合shiro,报There is no session with id [xxxxxx]问题定位及解决》](https://blog.csdn.net/sinat_28771747/article/details/105245229)

## 感谢
- Netty 项目及作者，项目地址：    https://github.com/netty/netty
- ignite 项目及作者，项目地址：   https://github.com/apache/ignite
- shiro 项目及作者，项目地址：    https://github.com/apache/shiro
- dubbo 项目及作者，项目地址：    https://github.com/apache/dubbo
- bootshiro 项目及作者，项目地址：https://gitee.com/tomsun28/bootshiro 


