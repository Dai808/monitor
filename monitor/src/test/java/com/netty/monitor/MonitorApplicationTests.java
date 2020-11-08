package com.netty.monitor;

import com.netty.monitor.util.ByteConversionUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionCommands;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;


class MonitorApplicationTests {


    private static final Map<String, String> MAP;

    static {
        MAP = new HashMap<String, String>();
        MAP.put("4b51303031", "KQ001");
        MAP.put("4b51303032", "KQ002");
        MAP.put("4b51303033", "KQ003");
        MAP.put("595a303031", "YZ001");
        MAP.put("595a303032", "YZ002");
        MAP.put("595a303033", "YZ003");
    }

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        String content = "01031a024700fb00d900240052000000d90002fffd006a00018b17027734a0";
        StringBuffer sb = new StringBuffer(content.substring(0, content.length() - 4));
        int length = content.length();
        String code = content.substring(length - 2, length) + content.substring(length - 4, length - 2);
        int index;
        for (index = 2; index < sb.length(); index += 3) {
            sb.insert(index, ',');
        }
        String codeFormat = "0x" + code;
        String[] data = sb.toString().split(",");
        System.out.println(sb.toString());
        System.out.println(codeFormat);
        System.out.println(data.length);

    }

    @Test
    void redisTest() {
        redisTemplate.opsForValue().set("aa", "123");
        System.out.println(redisTemplate.opsForValue().get("aa"));
        System.out.println(redisTemplate.execute(RedisConnectionCommands::ping));
    }

    @Test
    void test() {
       for (Map.Entry<String,String> map:MAP.entrySet()){
           System.out.println(map.getKey()   +"     "+map.getValue());
       }
    }

    @Test
    void test1() {
        System.out.println(ByteConversionUtil.stringSplicing("42"));
    }


}
