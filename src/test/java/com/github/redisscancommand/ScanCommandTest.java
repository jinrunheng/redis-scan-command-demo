package com.github.redisscancommand;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = RedisScanCommandApplication.class)
public class ScanCommandTest {

    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;


    @Test
    public void testScan() {
        String pattern = "K*";
        Long limit = 1000L;
        Map<String, String> map = scan(redisTemplate, pattern, limit);
        System.out.println(map);
    }

    private Map<String, String> scan(RedisTemplate redisTemplate, String pattern, Long limit) {
        return (Map<String, String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                Map<String, String> map = new HashMap<>();
                Cursor<byte[]> cursor = connection.scan(new ScanOptions
                        .ScanOptionsBuilder()
                        .match(pattern)
                        .count(limit)
                        .build());
                while (cursor.hasNext()) {
                    byte[] bytesKey = cursor.next();
                    byte[] bytesValue = connection.get(bytesKey);
                    String key = String.valueOf(redisTemplate.getKeySerializer().deserialize(bytesKey));
                    String value = String.valueOf(redisTemplate.getValueSerializer().deserialize(bytesValue));
                    map.put(key, value);
                }
                return map;
            }
        });
    }
}
