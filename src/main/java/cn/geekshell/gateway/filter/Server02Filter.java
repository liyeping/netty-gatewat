package cn.geekshell.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public class Server02Filter implements HttpRequestFilter{
    @Override
    public void filter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        //do something
        fullRequest.headers().set("filter02","liyeping");
    }
}
