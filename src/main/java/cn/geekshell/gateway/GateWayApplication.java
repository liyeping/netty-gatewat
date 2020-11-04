package cn.geekshell.gateway;

import cn.geekshell.gateway.inbound.HttpInboundServer;
import cn.geekshell.gateway.router.RoutingTable;

public class GateWayApplication {
    public final static String GATEWAY_NAME = "NIOGateway";
    public final static String GATEWAY_VERSION = "1.0.0";

    public static void main(String[] args) {

        //初始化路由配置，配置文件在classpath:/routes.yaml
        RoutingTable.initTable();
        String proxyPort = System.getProperty("proxyPort","8888");

        int port = Integer.parseInt(proxyPort);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" starting...");
        HttpInboundServer server = new HttpInboundServer(port);
        System.out.println(GATEWAY_NAME + " " + GATEWAY_VERSION +" started at http://localhost:" + port);
        try {
            server.run();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
