package cn.geekshell.gateway.outbound.httpclient;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.util.concurrent.*;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpClient {
    private CloseableHttpClient httpclient;
    private ExecutorService proxyService;
    private String backendUrl;

    public HttpClient(){

        int cores = Runtime.getRuntime().availableProcessors() * 2;
        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();

        proxyService = new ThreadPoolExecutor(cores, cores,
                1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(2048),
                new NamedThreadFactory("proxyService"), handler);

        httpclient = HttpClients.createDefault();


    }

    public void get(final FullHttpRequest request, final ChannelHandlerContext ctx) throws Exception{

        //filter添加的请求头
        System.out.println("filter1添加的请求头1: " + request.headers().get("nio"));
        System.out.println("filter2添加的请求头2: " + request.headers().get("filter02"));

        int timeout = 60;
        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(timeout * 1000)
                .setConnectTimeout(timeout * 1000)
                .setConnectionRequestTimeout(timeout * 1000)
                .build();
        HttpGet httpget = new HttpGet(backendUrl);
        httpget.setProtocolVersion(HttpVersion.HTTP_1_0);
        httpget.setConfig(defaultRequestConfig);
        CloseableHttpResponse response = null;
        FullHttpResponse fullHttpResponser = null;
        try {
            response = httpclient.execute(httpget);

            byte[] body = EntityUtils.toByteArray(response.getEntity());

            fullHttpResponser = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
            fullHttpResponser.headers().set("Content-Type", "application/json");
            fullHttpResponser.headers().setInt("Content-Length", Integer.parseInt(response.getFirstHeader("Content-Length").getValue()));
        } catch (Exception e){
            throw e;
        } finally{
            if (request != null) {
                if (!HttpUtil.isKeepAlive(request)) {
                    ctx.write(fullHttpResponser).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(fullHttpResponser);
                }
            }
            ctx.flush();
        }
    }

    public void handle(final String adress,final FullHttpRequest request, final ChannelHandlerContext ctx){
        this.backendUrl = adress.endsWith("/")?adress.substring(0,adress.length()-1) : adress;
        proxyService.submit(() -> {
            try {
                get(request,ctx);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
    }
}
