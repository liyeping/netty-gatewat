# netty-gatewat
**项目说明:**
该项目是使用netty做了一个简易网关  <br>

项目结构如下  <br>
.
├── pom.xml <br>
├── src  <br>
│   ├── main  <br>
│   │   ├── java  <br>
│   │   │   └── cn  <br>
│   │   │       └── geekshell  <br>
│   │   │           └── gateway  <br>
│   │   │               ├── GateWayApplication.java   #程序入口   <br>
│   │   │               ├── filter ###过滤器相关代码  <br>
│   │   │               │   ├── HttpRequestFilter.java  <br>
│   │   │               │   ├── Server01Filter.java  <br>
│   │   │               │   └── Server02Filter.java  <br>
│   │   │               ├── inbound ###netty 入站相关代码  <br>
│   │   │               │   ├── HttpInboundHandler.java  <br>
│   │   │               │   ├── HttpInboundInitializer.java  <br>
│   │   │               │   └── HttpInboundServer.java  <br>
│   │   │               ├── outbound ###出站相关代码  <br>
│   │   │               │   ├── httpclient  <br>
│   │   │               │   └── netty   <br>
│   │   │               │       ├── NettyHttpClient.java  <br>
│   │   │               │       └── NettyHttpClientOutboundHandler.java   <br>
│   │   │               └── router ###路由相关代码  <br>
│   │   │                   ├── HttpEndpointRouter.java  <br>
│   │   │                   ├── RoutingAlgorithm.java ###路由负载均衡算法，目前仅简单实现随机和轮询  <br>
│   │   │                   └── RoutingTable.java  <br>
│   │   └── resources ### 网关路由配置文件  <br>
│   │       └── routes.yaml  <br>
└──   <br>


