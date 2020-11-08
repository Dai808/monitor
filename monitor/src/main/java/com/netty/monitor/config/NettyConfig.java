package com.netty.monitor.config;

import com.netty.monitor.netty.NettyServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: DAI
 * @date: Created in 2020/10/21 14:29
 * @description： 给构造方法注入属性值
 */
@Configuration
public class NettyConfig {

    private Integer port,port2;

    @Bean
    public NettyServer server(){
        return new NettyServer(port,port2);
    }
}
