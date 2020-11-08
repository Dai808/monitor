package com.netty.monitor;

import com.netty.monitor.netty.NettyServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;

@SpringBootApplication
@MapperScan("com.netty.monitor.mapper")
public class MonitorApplication implements ApplicationContextAware {

    private static ApplicationContext applicationContext;
    private static DefaultListableBeanFactory defaultListableBeanFactory;

    public static void main(String[] args) {
        SpringApplication.run(MonitorApplication.class, args);
        try {
            new NettyServer(7761,7760).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        MonitorApplication.applicationContext = applicationContext;
        defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
    }

    public static <T> T getBean(Class<T> clazz) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        String className = clazz.getName();
        defaultListableBeanFactory.registerBeanDefinition(className, beanDefinitionBuilder.getBeanDefinition());
        return (T) applicationContext.getBean(className);
    }

    public static void destroy(String className) {
        defaultListableBeanFactory.removeBeanDefinition(className);
        System.out.println("destroy " + className);
    }

}
