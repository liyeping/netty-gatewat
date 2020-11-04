package cn.geekshell.gateway.outbound.netty;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import java.net.URL;

public class NettyHttpClient {

    public void connect(final String address,final ChannelHandlerContext ctx, final FullHttpRequest fullRequest) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            URL url = new URL(address);
            //设置目标地址的请求路径
            fullRequest.setUri(url.getPath());
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
//                    ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new NettyHttpClientOutboundHandler(ctx));
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(url.getHost(),url.getPort()).sync();
            //filter添加的请求头
            System.out.println("filter1添加的请求头1: " + fullRequest.headers().get("nio"));
            System.out.println("filter2添加的请求头2: " + fullRequest.headers().get("filter02"));
            f.channel().write(fullRequest);
            f.channel().flush();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    public void get(String address,final FullHttpRequest fullRequest,final ChannelHandlerContext ctx) {
        try {
            this.connect(address,ctx,fullRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}