## 简介
### 概念
HXAPIGate（中文名：浩心API网关）——如果觉得可以请star本项目。
HXAPIGate基于Netty+Shiro开发的一款高性能API网关，对基于REST服务的细粒度API资源的权限管理平台，支持http,dubbo等多协议微服务接口代理，前端工程则继续使用bootshiro的usthe即可（usthe是基于angular开发的前端项目 https://gitee.com/tomsun28/usthe  ）。
![输入图片说明](https://images.gitee.com/uploads/images/2019/1112/152324_e14eb0c7_1038477.png "屏幕截图.png")

### MECHA--机甲
电影阿凡达肯定很多人都看过，地球大军在潘多拉星球的穿着各种武装机甲（mecha）战斗的场面很是震撼！
![macha](https://images.gitee.com/uploads/images/2020/0706/202838_f57c9abc_2067850.jpeg "macha.jpg")

大家可以想象一下，机甲里面的战士就是我们的微服务，虽然我们的战士在不借助机甲的情况下也能战斗，但是穿上机甲能让我们变得更加强大，同时提供一些我们所不具备的其他能力。同理，单纯的一个微服务个体虽然能够处理各种业务逻辑，
但是能力总是不足以完全胜任越来越严苛的运行环境，比如微服务的分布式限流、服务熔断、权限认证等，如果把这些功能都下放到微服务本身，除了会增加微服务开发人员的学习成本外，还会导致微服务的研发不能专注于业务本身，而需要做
大量与业务无关的操作。

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

### 特色

目前多数授权管理平台都只单单对api路径资源本身授权，而不能做到更细粒度的权限控制，HXAPIGate通过组合bootshiroPro实现了对“api资源+请求方式”的授权模式。
如：
新增如下四个接口

| 接口路径 | 请求方式 |
|--|--|
|“/user/list”| GET |
|“/user/list”| POST |
|“/user/list”| DELETE |
|“/user/list”| PUT |

传统授权模式下，这四个接口会被当做一个接口（因为接口路径一致）授权给第三方，而通过HXAPIGate可分别对每个资源进行授权，当仅仅授权“/user/list”+“GET”给第三方平台时，被授权放无法访问同一资源的POST、DELETE、PUT请求当时的接口！

### 授权认证时序图

![授权流程](https://images.gitee.com/uploads/images/2019/1112/113303_d06bcb01_1038477.jpeg "授权流程.jpg")

### 性能
2000并发事务压测报告（jdk1.8，jvm堆内存512M）
![API网关2000并发压测图（jvm=512M）](https://images.gitee.com/uploads/images/2019/1112/113504_8b9b126e_1038477.png "API网关2000并发压测图（jvm=512M）.png")

## 操作演示

### 登录
![输入图片说明](https://images.gitee.com/uploads/images/2020/0914/122220_5765ed47_1038477.gif "登录.gif")
### 新增接口类别
![输入图片说明](https://images.gitee.com/uploads/images/2020/0914/122334_6e1cac3c_1038477.gif "新建API类别.gif")
### 新增接口
![输入图片说明](https://images.gitee.com/uploads/images/2020/0914/122348_628ecd93_1038477.gif "新建API接口.gif")
### 接口授权
![输入图片说明](https://images.gitee.com/uploads/images/2020/0914/122404_9b01e94b_1038477.gif "给用户角色授权ceshi接口.gif")

### 目前网关已实现功能
1. 授权、鉴权管理
2. 路由配置
3. 路由负载（轮寻和赋权值）
4. HTTP、dubbo多协议协议
5. 接口分布式限流
6. 金丝雀发布
7. 接口降级

### 项目进度
 目前HXAPIGate仍在继续进行项目重构和功能完善中，目前HXAPIGate的分布式特性只完成了一部分，由于作者和团队的精力实在有限，因此开发进度会比较缓慢，希望大家谅解，同时请star并持续关注本项目！

### 相关博文
《netty整合shiro,报There is no session with id [xxxxxx]问题定位及解决》

https://blog.csdn.net/sinat_28771747/article/details/105245229

## 感谢
bootshiro 项目及作者，bootshiro项目路径：https://gitee.com/tomsun28/bootshiro 

## 参与贡献

当前项目仍处在研发过程中，由于目前只有两三个人贡献源码，且都是利用周末研发，导致目前研发进度缓慢，
因此迫切希望有识之士一起参与到浩欣API网关的建设中来，共同推进该项目的研发进程，力争在2020年年底发
布alpha版本，2021年3月发布第一个稳定版本，有意报名者请发送邮件至：ycheng155@gmail.com；邮件中简单
描述一下自己对网络编程、网络通信协议的理解，注明自己目前主要使用的编程语言（java、golang等），前
端研发的同学注明自己主要使用的前端框架技术（angular、vue等）。欢迎大家积极参与到HXAPIGate的项目建设中来。
