package org.homework.utils;

import org.springframework.data.redis.core.*;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public enum ScanDelType {

    HASH {
        @Override
        public void del(RedisTemplate<String, String> rt, String key,
                        int groupMaxSize, ScanOptions scanOptions, List<Object> keyList) {
            HashOperations<String, Object, Object> hashOperations = rt.opsForHash();
            try (Cursor<Map.Entry<Object, Object>> scan = hashOperations.scan(key, scanOptions)) {
                while (scan.hasNext()) {
                    Map.Entry<Object, Object> next = scan.next();
                    keyList.add(next.getKey());
                    // 当list的size等于预设的最大值时，批量删除这些key，并清除list
                    if (keyList.size() == groupMaxSize) {
                        hashOperations.delete(key, keyList);
                        keyList.clear();
                    }
                }
            }
        }
    },
    SET {
        @Override
        public void del(RedisTemplate<String, String> rt, String key,
                        int groupMaxSize, ScanOptions scanOptions, List<Object> keyList) {
            SetOperations<String, String> setOperations = rt.opsForSet();
            try (Cursor<String> scan = setOperations.scan(key, scanOptions)) {
                while (scan.hasNext()) {
                    Object next = scan.next();
                    keyList.add(next);
                    // 当list的size等于预设的最大值时，批量删除这些key，并清除list
                    if (keyList.size() == groupMaxSize) {
                        setOperations.remove(key, keyList);
                        keyList.clear();
                    }
                }
            }
        }
    }, ZSET {
        @Override
        public void del(RedisTemplate<String, String> rt, String key,
                        int groupMaxSize, ScanOptions scanOptions, List<Object> keyList) {
            ZSetOperations<String, String> zSetOperations = rt.opsForZSet();
            try (Cursor<ZSetOperations.TypedTuple<String>> scan = zSetOperations.scan(key, scanOptions)) {
                while (scan.hasNext()) {
                    ZSetOperations.TypedTuple<String> next = scan.next();
                    // 这里相当于key
                    String value = next.getValue();
                    keyList.add(value);
                    if (keyList.size() == groupMaxSize) {
                        zSetOperations.remove(key, keyList);
                        keyList.clear();
                    }
                }
            }
        }
    }, LIST {
        // 需要注意的是，redis中并不存在针对list数据类型的scan命令
        // 这边只是为了方便管理代码才写在这个枚举类中
        @Override
        public void del(RedisTemplate<String, String> rt,
                        String key, int groupMaxSize,
                        ScanOptions scanOptions, List<Object> keyList) {
            if (!StringUtils.hasText(key)) {
                return;
            }
            ListOperations<String, String> opsForList = rt.opsForList();
            // 获取长度
            Long size = opsForList.size(key);
            if (Objects.isNull(size)) {
                return;
            }
            // 删去的每组数量
            while (size > (long) groupMaxSize) {
                // 通过trim进行切割
                opsForList.trim(key, groupMaxSize, -1);
                size -= (long) groupMaxSize;
            }
            rt.delete(key);
        }
    }, String {
        @Override
        public void del(RedisTemplate<String, String> rt, String key, int groupMaxSize, ScanOptions scanOptions, List<Object> keyList) {
            // 保持空实现即可
        }
    };

    public abstract void del(RedisTemplate<String, String> rt, String key,
                             int groupMaxSize, ScanOptions scanOptions, List<Object> keyList);

}
