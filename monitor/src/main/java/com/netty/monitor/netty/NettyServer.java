package com.netty.monitor.netty;

import javax.annotation.PreDestroy;

import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author: DAI
 * @date: Creat in 2020/11/8 14:45
 * @describe: netty服务
 */
@Slf4j
public class NettyServer {

    /**
     * 端口
     */
    private int port;
    private int port2;

    public NettyServer(int port, int port2) {
        this.port = port;
        this.port2 = port2;
    }

    public void run() throws Exception {
        // Netty 负责装领导的事件处理线程池
        EventLoopGroup leader = new NioEventLoopGroup();
        // Netty 负责装码农的事件处理线程池
        EventLoopGroup coder = new NioEventLoopGroup();
        try {
            // 服务端启动引导器
            ServerBootstrap server = new ServerBootstrap();

            server
                    .group(leader, coder)
                    //设置采用Nio的通道方式来建立请求连接
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //构造一个由通道处理器构成的通道管道流水线
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 此处添加服务端的通道处理器,不同的端口使用不不同的类处理
                            int localPort = socketChannel.localAddress().getPort();
                            if (localPort == 7760) {
                                socketChannel.pipeline().addLast(new NettyServerHandler());
                            } else if (localPort == 7761) {
                                socketChannel.pipeline().addLast(new NettyServerHandler2());
                            }
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 服务端绑定端口并且开始接收进来的连接请求
            ChannelFuture channelFuture = server.bind(port).sync();
            ChannelFuture channelFuture2 = server.bind(port2).sync();
            // 查看一下操作是不是成功结束了
            if (channelFuture.isSuccess()) {
                //如果没有成功结束就处理一些事情,结束了就执行关闭服务端等操作
                System.out.println("服务端启动成功,监听端口是：" + port);
            } else {
                channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
                    //通过回调只关闭自己监听的channel
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        future.channel().close();
                    }
                });
            }
            if (channelFuture2.isSuccess()) {
                //如果没有成功结束就处理一些事情,结束了就执行关闭服务端等操作
                System.out.println("服务端启动成功,监听端口是：" + port2);
            } else {
                channelFuture2.channel().closeFuture().addListener(new ChannelFutureListener() {
                    //通过回调只关闭自己监听的channel
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        future.channel().close();
                    }
                });
            }
        } finally {
            // 关闭事件处理组
            // leader.shutdownGracefully();
            // coder.shutdownGracefully();
            // System.out.println("服务端已关闭!");
        }

    }
}