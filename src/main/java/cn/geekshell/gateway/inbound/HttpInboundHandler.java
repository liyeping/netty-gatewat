package cn.geekshell.gateway.inbound;

import cn.geekshell.gateway.outbound.netty.NettyHttpClient;
import cn.geekshell.gateway.router.RoutingTable;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpInboundHandler extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(HttpInboundHandler.class);
    private NettyHttpClient nettyClient;
    
    public HttpInboundHandler() {
        nettyClient = new NettyHttpClient();
    }
    
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FullHttpRequest fullRequest = (FullHttpRequest) msg;

            //通过负载均衡算法，获取目标端地址
            String address = RoutingTable.getUri(fullRequest.getUri());
            System.out.println("目标地址：" + address);

            //处理filter
            List<String> filters = RoutingTable.getFilter(fullRequest.getUri());
            handleFilters(filters,fullRequest,ctx);
            
            if(Objects.isNull(address)){
                handleException(ctx,"路由配置不匹配，请检查请求连接与路由配置");
            }else {
                nettyClient.get(address,fullRequest,ctx);
            }
        } catch(Exception e) {
            e.printStackTrace();
            handleException(ctx,e.getMessage());
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    private void handleFilters(List<String> filters,FullHttpRequest request,ChannelHandlerContext ctx) {
        //反射创建filter对象，并执行filter
        try {
            if(filters != null && filters.size() > 0){
                for (String filter: filters) {
                    Class<?> filterC = Class.forName(filter);
                    Method filterM = filterC.getMethod("filter", FullHttpRequest.class, ChannelHandlerContext.class);
                    filterM.invoke(filterC.newInstance(),request,ctx);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
//            throw e;
        }
    }

    private void handleException(ChannelHandlerContext ctx,String msg){
        FullHttpResponse response = null;
        try {
            response = new DefaultFullHttpResponse(HTTP_1_1, BAD_GATEWAY, Unpooled.wrappedBuffer(msg.getBytes("UTF-8")));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", response.content().readableBytes());

        } catch (Exception e) {
            logger.error("处理测试接口出错", e);
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
        } finally {
            ctx.write(response);
        }
    }
}
