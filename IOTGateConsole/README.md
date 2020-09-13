
# IOTGateConsole

### 介绍
IOTGateConsole是IOTGate智能网关的控制台程序，用于查看当前IOTGate CLUSTER的运行状态，执行 网关规约解析服务的启动、关闭、重启以及网关多规约策略配置等操作

### 环境要求
zookeeper集群环境 、jdk1.8、mysql5.5以及IOTGate节点

### GATE CLUSTER 结构图
![集群版IOTGate架构](https://images.gitee.com/uploads/images/2019/0402/194105_f06b6623_1038477.png "IOTGate整体架构图.png")
GATE Console 是一个web工程，用户登录之后可以查看当前GATE CLUSTER的运行状态监控，执行网关重启、关闭、启动，网关多规约支持策略等操作（目前不考虑监控网关服务器状态功能）

### 入口类
	IotGateConsoleApplication
### 操作说明
- 1.首先搭建靠zookeeper集群，开启mysql服务并建立好数据库名称
- 2.在/IOTGateConsole/src/main/resources/application.properties配置文件中配置数据库参数和zookeeper相关参数
- 3.将本项目打成一个可执行jar包，启动jar包，默认端口为8686，可以在application.properties中自定义
- 4.访问地址如：http://127.0.0.1:8686/rpc/index ，第一次f访问会跳转到登录页让输入用户名密码，这个随意填写，没有存库！登录之后便可以发现查看当前zookeeper中注册的网关节点已经当前数据库中的规约信息，如何配置请看:[https://blog.csdn.net/sinat_28771747/article/details/88783309](https://blog.csdn.net/sinat_28771747/article/details/88783309)
### 交流群
- QQ群：844082385
