package cn.geekshell.gateway.outbound.netty;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyHttpClientOutboundHandler  extends ChannelInboundHandlerAdapter {
    private static Logger logger = LoggerFactory.getLogger(NettyHttpClientOutboundHandler.class);
    private ChannelHandlerContext gatewayServerCtx;

    public NettyHttpClientOutboundHandler (final ChannelHandlerContext gatewayServerCtx) {
        this.gatewayServerCtx = gatewayServerCtx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        try {
            this.gatewayServerCtx.writeAndFlush(msg).addListeners(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        ctx.channel().read();
                    } else {
                        channelFuture.channel().close();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        this.gatewayServerCtx.flush();
        ctx.close();//这里不关闭，当前请求会一直阻塞
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}