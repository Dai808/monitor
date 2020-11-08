package com.netty.monitor.netty;

import com.netty.monitor.util.ByteConversionUtil;
import com.netty.monitor.util.CRC16Util;
import com.netty.monitor.util.RedisUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: DAI
 * @date: Created in 2020/10/21 14:18
 * @description：
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static NettyServerHandler nettyServerHandler;

    @Resource
    private RedisUtil redisUtil;

    @PostConstruct
    public void init() {
        nettyServerHandler = this;
    }

    /**
     * 保存连接进服务端的通道数量
     */
    private static final ConcurrentHashMap<ChannelId, ChannelHandlerContext> MAP = new ConcurrentHashMap<>();

    /**
     * @param ctx
     * @throws Exception
     * @description: 有客户端连接服务器会触发此函数
     * @return: void
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        int clientPort = insocket.getPort();
        // 唯一标识
        ChannelId channelId = ctx.channel().id();
        System.out.println();
        //如果map中不包含此连接，就保存连接
        if (MAP.containsKey(channelId)) {
            log.info("连接通道数量: " + MAP.size());
        } else {
            //保存连接
            MAP.put(channelId, ctx);
            log.info("连接通道数量: " + MAP.size());
        }
    }

    /**
     * @param ctx
     * @description: 有客户端终止连接服务器会触发此函数
     * @return: void
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        ChannelId channelId = ctx.channel().id();
        //包含此客户端才去删除
        if (MAP.containsKey(channelId)) {
            //删除连接
            MAP.remove(channelId);
            log.info("连接通道数量: " + MAP.size());
        }
    }


    /**
     * @param ctx
     * @description: 客户端发消息会触发此函数
     * @return: void
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 获取客户端ip地址，作为key存放设备编号
        InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
        Channel channel = ctx.channel();
        SocketAddress socketAddress = channel.localAddress();
        String clientIp = insocket.getAddress().getHostAddress();
        if (msg == null || msg == "") {
            log.info("客户端响应空的消息");
            ctx.flush();
            return;
        }
        int port = insocket.getPort();
        log.info("发送数据的客户端端口是: " + port);

        ByteBuf buf = (ByteBuf) msg;
        // 获取到的数据，后面可以做处理
        String content = ByteBufUtil.hexDump(buf);
    }

    /**
     * channelReadComplete channel 通道 Read 读取 Complete 完成
     * 在通道读取完成后会在这个方法里通知，对应可以做刷新操作 ctx.flush()
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    /**
     * @param clientIp  ip地址
     * @param channelId 连接通道唯一id
     * @description: 服务端给客户端发送消息
     * @return: void
     */
    public void channelWrite(ChannelId channelId, String clientIp) throws Exception {
       // msg是向客户端发送的消息，此处的需求是发送16进制数据
        ByteBuf res = Unpooled.wrappedBuffer(ByteConversionUtil.hexStrToBinaryStr("msg"));
        ChannelHandlerContext ctx = MAP.get(channelId);
        if (ctx == null) {
            log.info("通道【" + channelId + "】不存在");
            ctx.flush();
            return;
        }
        //将客户端的信息直接返回写入ctx、刷新缓存区
        ctx.writeAndFlush(res);
    }

    /**
     * @description: 异常捕捉
     * @return: void
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.flush();
        ctx.close();
    }
}
