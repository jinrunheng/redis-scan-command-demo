package com.github.redisscancommand;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@ContextConfiguration(classes = RedisScanCommandApplication.class)
public class RedisTest {

    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;


    @Test
    public void testScan() {
        setData(redisTemplate);
        String pattern = "K*";
        Long limit = 1000L;
        Set<String> set = scan(redisTemplate, pattern, limit);
        System.out.println(set);
    }

    private void setData(RedisTemplate redisTemplate) {
        redisTemplate.opsForValue().set("K1", "K-1");
        redisTemplate.opsForValue().set("K2", "K-2");
        redisTemplate.opsForValue().set("K3", "K-3");
        redisTemplate.opsForValue().set("K4", "K-4");
        redisTemplate.opsForValue().set("K5", "K-5");
        redisTemplate.opsForValue().set("K6", "K-6");
        redisTemplate.opsForValue().set("K7", "K-7");
        redisTemplate.opsForValue().set("K8", "K-8");
        redisTemplate.opsForValue().set("K9", "K-9");
        redisTemplate.opsForValue().set("M1", "V1");
        redisTemplate.opsForValue().set("M2", "V2");
        redisTemplate.opsForValue().set("M3", "V3");
        redisTemplate.opsForValue().set("M4", "V4");
        redisTemplate.opsForValue().set("M5", "V5");
        redisTemplate.opsForValue().set("M6", "V6");
        redisTemplate.opsForValue().set("M7", "V7");
        redisTemplate.opsForValue().set("M8", "V8");
        redisTemplate.opsForValue().set("M9", "V9");
    }

    private Set<String> scan(RedisTemplate redisTemplate, String pattern, Long limit) {
        return (Set<String>) redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                Set<String> set = new HashSet<>();
                Cursor<byte[]> cursor = connection.scan(new ScanOptions
                        .ScanOptionsBuilder()
                        .match(pattern)
                        .count(limit)
                        .build());
                while (cursor.hasNext()) {
                    byte[] bytes = connection.get(cursor.next());
                    String value = String.valueOf(redisTemplate.getValueSerializer().deserialize(bytes));
                    System.out.println(value);
                    set.add(value);
                }
                return set;
            }
        });
    }
}
