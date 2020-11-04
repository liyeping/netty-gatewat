package cn.geekshell.gateway.router;

import org.ho.yaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutingTable {
    private static Logger logger = LoggerFactory.getLogger(RoutingTable.class);
    private List<Map<String,Object>>  routes = new ArrayList<>();
    public static Map<String,Map> routerTable = new HashMap<>();

    public void setRoutes(List<Map<String, Object>> routes) {
        this.routes = routes;
    }
    public List<Map<String, Object>> getRoutes() {
        return routes;
    }

    public static void initTable(){
        try {
            File dumpFile=new File(RoutingTable.class.getResource("/routes.yaml").toURI());
            RoutingTable father = Yaml.loadType(dumpFile, RoutingTable.class);
            father.routes.forEach( map -> {
                routerTable.put(map.get("id").toString(),map);
            });
            System.out.println(routerTable);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getUri(String uri){
        String backendUrl = null;
        try {
            String serviceId = uri.strip().substring(1,uri.indexOf("/",1));
            if(routerTable.containsKey(serviceId)){
                List<String> urls = (List<String>) routerTable.get(serviceId).get("urls");
                //负载均衡处理
                String loadbalance = (String)routerTable.get(serviceId).get("loadbalance");
                switch (loadbalance){
                    case "random":
                        backendUrl = RoutingAlgorithm.random(urls);
                        break;
                    case "polling":
                        backendUrl = RoutingAlgorithm.polling(urls,serviceId);
                        break;
                    default:
                        backendUrl = RoutingAlgorithm.random(urls);
                }
                backendUrl = backendUrl + uri.substring(uri.indexOf("/",1));
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取uri ："  + uri +"错误，请检查配置；错误信息: " + e.getMessage());
        }
        return backendUrl;
    }
    public static List<String> getFilter(String uri){
        List<String> filter = null;
        try {
            String serviceId = uri.strip().substring(1,uri.indexOf("/",1));
            if(routerTable.containsKey(serviceId)){
                filter = (List<String>) routerTable.get(serviceId).get("filters");
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("获取filter ："  + uri +"错误，请检查配置；错误信息: " + e.getMessage());
        }
        return filter;
    }

    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        RoutingTable.initTable();
        while (true){
            String uri = RoutingTable.getUri("/server01/test");
            System.out.println("url : " + uri);
            Thread.sleep(1000);
        }
//        List<String> filter = RoutingTable.getFilter("/server01/test");
//        System.out.println(filter);
    }
}
