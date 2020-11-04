package cn.geekshell.gateway.router;

import java.util.*;

public class RoutingAlgorithm {
    private static Map<String,Integer> lastTime  = new HashMap<>();

    public static String random(List<String> urls){
        String result = null;
        if(Objects.nonNull(urls)){
            Random random = new Random();
            int i = random.nextInt(urls.size());
            result = urls.get(i);
        }
        return result;
    }

    public synchronized static String polling(List<String> urls,String serverId){
        String result = null;
        if(Objects.nonNull(urls)){
            if(lastTime.containsKey(serverId)){
                Integer lt = lastTime.get(serverId);
                if( lt >= urls.size()-1){
                    lt = 0;
                    result = urls.get(0);
                    lastTime.put(serverId,0);
                }else {
                    result = urls.get(lt+1);
                    lastTime.put(serverId,++lt);
                }
            }else {
                result = urls.get(0);
                lastTime.put(serverId,0);
            }
        }
        return result;
    }
}
