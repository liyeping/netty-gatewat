# netty-gatewat
**项目说明:**
该项目是使用netty做了一个简易网关

项目结构如下
.
├── pom.xml <br>
├── src
│   ├── main
│   │   ├── java
│   │   │   └── cn
│   │   │       └── geekshell
│   │   │           └── gateway
│   │   │               ├── GateWayApplication.java   #程序入口
│   │   │               ├── filter ###过滤器相关代码
│   │   │               │   ├── HttpRequestFilter.java
│   │   │               │   ├── Server01Filter.java
│   │   │               │   └── Server02Filter.java
│   │   │               ├── inbound ###netty 入站相关代码
│   │   │               │   ├── HttpInboundHandler.java
│   │   │               │   ├── HttpInboundInitializer.java
│   │   │               │   └── HttpInboundServer.java
│   │   │               ├── outbound ###出站相关代码
│   │   │               │   ├── httpclient
│   │   │               │   └── netty 
│   │   │               │       ├── NettyHttpClient.java
│   │   │               │       └── NettyHttpClientOutboundHandler.java 
│   │   │               └── router ###路由相关代码
│   │   │                   ├── HttpEndpointRouter.java
│   │   │                   ├── RoutingAlgorithm.java ###路由负载均衡算法，目前仅简单实现随机和轮询
│   │   │                   └── RoutingTable.java
│   │   └── resources ### 网关路由配置文件
│   │       └── routes.yaml
└── 


