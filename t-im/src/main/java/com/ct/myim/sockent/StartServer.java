package com.ct.myim.sockent;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.log.StaticLog;
import com.ct.myim.im.config.Config;
import com.ct.myim.sockent.handler.DispatchHandler;
import com.ct.myim.sockent.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.concurrent.GenericFutureListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Service
public class StartServer {

    @Value("${webSockent.prot}")
    private int port;

    //启动方法
    public void start() throws Exception {
        //负责接收客户端的连接的线程。线程数设置为1即可，netty处理链接事件默认为单线程，过度设置反而浪费cpu资源
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //负责处理数据传输的工作线程。线程数默认为CPU核心数乘以2
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup);
            bootstrap.channel(NioServerSocketChannel.class);
            //在ServerChannelInitializer中初始化ChannelPipeline责任链，并添加到serverBootstrap中
            bootstrap.childHandler(new ChannelInitializer() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    //添加HTTP编解码
                    channel.pipeline().addLast("decoder", new HttpRequestDecoder());
                    channel.pipeline().addLast("encoder", new HttpResponseEncoder());
                    //消息聚合器，将消息聚合成FullHttpRequest
                    channel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                    //支持大文件传输
                    channel.pipeline().addLast("chunked", new ChunkedWriteHandler());
                    //自定义Handler
                    channel.pipeline().addLast("dispatchHandler", new DispatchHandler());
                    channel.pipeline().addLast("webSocketHandler", new WebSocketHandler());
                }
            });
            //标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            //Netty4使用对象池，重用缓冲区
            bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            //是否启用心跳保活机制
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            //禁止使用Nagle算法，便于小数据即时传输
            bootstrap.childOption(ChannelOption.TCP_NODELAY, true);
            bootstrap.localAddress(new InetSocketAddress(port));
            //开始绑定server
            ChannelFuture channelFuture = bootstrap.bind();
            channelFuture.addListener(new GenericFutureListener<ChannelFuture>() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (!future.isSuccess()) {
                        // 释放掉所有资源包括创建的线程
                        workerGroup.shutdownGracefully();
                        bossGroup.shutdownGracefully();
                    } else {
                        StaticLog.info("WebSockent服务启动成功");
                        future.channel().closeFuture();
                    }
                }
            });
        } catch (Exception e) {
            //释放资源
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            StaticLog.error("WebSockent服务停止");
        }
    }
}
