package org.homework.utils;

import cn.hutool.core.util.HashUtil;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zhanghaifeng
 * 不规范操作redis导致的逆天事件 https://www.sohu.com/a/255077492_465914
 */
@Component
@Slf4j
public class RedisUtil {
    public static final int ONE_MINUTE = 60;
    public static final int ONE_HOUR = 60 * ONE_MINUTE;
    public static final int ONE_DAY = 24 * ONE_HOUR;
    // scan命令中的每页大小，过大可能会造成网络堵塞
    public static final int SCAN_GROUP_SIZE = 10000;
    @Value("${spring.data.redis.LOGIN_KEY}")
    public String LOGIN_KEY;
    // 此处可能会因为spring-data-redis版本的改变而出现问题，修改@Value中的映射即可
    @Value("${spring.data.redis.database}")
    String database;
    @Resource
    RedisTemplate<String, String> redisTemplate;

    /**
     * 处理对象的哈希值
     *
     * @param key 输入的对象，用于计算哈希值
     * @return 返回处理后的哈希值，保证在长整型范围内
     */
    public static long dealWithHash(Object key) {
        // 计算哈希值
        long hashValue = Math.abs(HashUtil.mixHash((String) key));
        // 对hash值取余
        return (long) (hashValue % Math.pow(2, 32));
    }

    /**
     * 将对象存储到Redis中。
     * <p>
     * 此方法用于将给定的对象转换为JSON字符串，并存储到Redis中，使用指定的键进行标识。
     * 如果键没有文本内容（即为空或null），则不进行存储操作。
     *
     * @param key    存储对象的键，必须是非空文本。
     * @param object 要存储的对象，可以是任何类型的对象，会被转换为JSON字符串。
     */
    public void set(String key, Object object) {
        // 检查键是否为空，如果为空则直接返回，不进行存储操作。
        if (!StringUtils.hasText(key)) {
            return;
        }
        // 如果是字符串对象则不用转换
        if (object instanceof String) {
            // 使用Redis模板将JSON字符串存储到Redis中，对应的键为参数key。
            redisTemplate.opsForValue().set(key, (String) object);
        } else {
            // 将对象转换为JSON字符串。
            String json = JSON.toJSONString(object);
            // 使用Redis模板将JSON字符串存储到Redis中，对应的键为参数key。
            redisTemplate.opsForValue().set(key, json);
        }
    }

    /**
     * 如果键不存在，则在Redis中设置键值对。
     * 如果键已存在，操作将不会有任何效果。
     *
     * @param key    键，用于标识存储在Redis中的值。
     * @param object 要存储的对象，可以是字符串或其他对象。
     */
    public void setIfNotExist(String key, Object object) {
        // 检查键是否为空，如果为空则直接返回，不进行存储操作。
        if (!StringUtils.hasText(key)) {
            return;
        }
        // 如果是字符串对象则不用转换
        if (object instanceof String) {
            // 使用Redis模板将JSON字符串存储到Redis中，对应的键为参数key。
            redisTemplate.opsForValue().setIfAbsent(key, (String) object);
        } else {
            // 将对象转换为JSON字符串。
            String json = JSON.toJSONString(object);
            // 使用Redis模板将JSON字符串存储到Redis中，对应的键为参数key转换后的字符串。
            redisTemplate.opsForValue().setIfAbsent(key, json);
        }
    }

    /**
     * 将对象存储到Redis中。
     * 如果键为空，则不执行存储操作。
     * 如果对象是字符串类型，直接存储；否则，将对象转换为JSON字符串后存储。
     *
     * @param key            键，用于标识存储在Redis中的数据
     * @param object         需要存储的对象，可以是字符串或其他对象
     * @param expirationTime 数据的过期时间，单位为秒
     */
    public void set(String key, Object object, int expirationTime) {
        // 检查键是否为空，如果为空则不进行存储操作
        if (!StringUtils.hasText(key)) {
            return;
        }
        // 如果是字符串对象则不用转换
        if (object instanceof String) {
            // 使用Redis模板将JSON字符串存储到Redis中，对应的键为参数key。
            redisTemplate.opsForValue().set(key, (String) object, expirationTime, TimeUnit.SECONDS);
        } else {
            // 将对象转换为JSON字符串。
            String json = JSON.toJSONString(object);
            // 使用Redis模板将JSON字符串存储到Redis中，对应的键为参数key。
            redisTemplate.opsForValue().set(key, json, expirationTime, TimeUnit.SECONDS);
        }
    }

    /**
     * 如果键不存在，则在Redis中设置值。
     * 该方法用于在Redis中条件性地设置键值对，仅当键不存在时才设置。
     * 支持直接存储字符串对象或自动将其他对象转换为JSON字符串后存储。
     *
     * @param key            键，用于标识存储在Redis中的值。
     * @param object         需要存储的值，可以是字符串或其他对象。
     * @param expirationTime 值的过期时间，单位为秒。
     */
    public void setIfNotExist(String key, Object object, int expirationTime) {
        // 检查键是否为空，如果为空则不进行存储操作
        if (!StringUtils.hasText(key)) {
            return;
        }
        // 如果是字符串对象则不用转换
        if (object instanceof String) {
            // 使用Redis模板将JSON字符串存储到Redis中，对应的键为参数key。
            redisTemplate.opsForValue().setIfAbsent(key, (String) object, expirationTime, TimeUnit.SECONDS);
        } else {
            // 将对象转换为JSON字符串。
            String json = JSON.toJSONString(object);
            // 使用Redis模板将JSON字符串存储到Redis中，对应的键为参数key。
            redisTemplate.opsForValue().setIfAbsent(key, json, expirationTime, TimeUnit.SECONDS);
        }
    }

    /**
     * 从Redis中获取指定键对应的对象。
     * <p>
     * 该方法通过键值查询Redis中的数据，并将查询到的JSON格式数据转换为指定类型的对象。
     * 如果键不存在或者键对应的值为空，则返回null。
     *
     * @param key    Redis中的键。
     * @param tClass 指定数据转换的目标类型。
     * @param <T>    泛型参数，表示目标类型。
     * @return 键对应的对象，如果键不存在或值为空，则返回null。
     */
    public <T> T getObject(String key, Class<T> tClass) {
        // 检查键是否为空
        if (!StringUtils.hasText(key)) {
            return null;
        }
        // 从Redis中获取键对应的JSON字符串
        String json = Objects.requireNonNull(redisTemplate.opsForValue().get(key));
        // 将JSON字符串转换为指定类型的对象
        return JSON.parseObject(json, tClass);
    }

    /**
     * 根据键获取存储的值。
     *
     * @param key 键，用于查找对应的值。
     * @return 如果键存在且值不为空，则返回去除了首尾双引号的值；如果键不存在或值为空，则返回null。
     */
    public String get(String key) {
        // 检查键是否为空，如果为空则直接返回null
        if (!StringUtils.hasText(key)) {
            return null;
        }
        // 通过redisTemplate的opsForValue方法获取键对应的值，如果值不存在，则返回null
        return Objects.requireNonNull(redisTemplate.opsForValue().get(key));
    }

    /**
     * 删除Redis中的多个键。
     * <p>
     * 此方法接受可变长参数keys，允许用户一次性传入多个键进行删除操作。如果传入的键数组为空，则不执行任何操作直接返回。
     * 使用Java 8的流式操作将键数组转换为List集合，然后通过redisTemplate的delete方法进行批量删除。
     *
     * @param keys 要删除的键，可以是零个或多个。
     */
    public void delete(String... keys) {
        // 检查传入的键数组是否为空，如果为空则直接返回，不执行删除操作
        if (keys.length == 0) {
            return;
        }
        // 使用流式操作将键数组转换为List集合，为适应redisTemplate的delete方法要求
        List<String> collect = Arrays.stream(keys).collect(Collectors.toList());
        // 执行批量删除操作
        redisTemplate.delete(collect);
    }


    /**
     * 批量删除缓存中的键
     * <p>
     * 此方法接收一个键的集合，将这些键从缓存中删除它首先检查集合是否为空，
     * 如果为空，则不执行任何操作这可以避免不必要的操作调用和资源消耗
     *
     * @param keyCollection 要从缓存中删除的键的集合不能为空如果集合为空，方法将直接返回，不执行删除操作
     */
    public void delete(Collection<String> keyCollection) {
        // 检查传入的键数组是否为空，如果为空则直接返回，不执行删除操作
        if (keyCollection.isEmpty()) {
            return;
        }
        // 执行批量删除操作
        redisTemplate.delete(keyCollection);
    }

    /**
     * 删除以指定字符串为前缀的键值对。
     * <p>
     * 本方法通过构建模糊删除条件，对Redis中以指定字符串为前缀的键值对进行批量删除。
     * 主要用于处理需要批量删除一类键值对的情况，例如清理过期的缓存数据。
     *
     * @param key 前缀字符串，用于匹配需要删除的键值对。如果传入的字符串为空或仅包含空白字符，则不执行任何操作。
     */
    public void deleteRightLike(String key) {
        // 检查传入的key是否为空或仅包含空白字符，如果是，则直接返回，不执行删除操作
        if (!StringUtils.hasText(key)) {
            return;
        }

        // 构建ScanOptions对象，设置匹配规则为key后跟任意字符，即匹配所有以key为前缀的键，并设置每次扫描的个数为SCAN_GROUP_SIZE
        ScanOptions options = ScanOptions.scanOptions().match(key + "*").count(SCAN_GROUP_SIZE).build();

        // 调用fuzzyDelete方法，传入构建好的ScanOptions，执行模糊删除操作
        fuzzyDelete(options);
    }

    /**
     * 根据指定的键前缀删除符合条件的数据。
     * 本方法主要用于删除数据存储中以特定前缀开头的一组键值对。通过匹配键的前缀，可以灵活地定位并删除一批数据，
     * 而不需要确切知道每个键的完整值。这种方法适用于需要批量删除数据的场景，例如清理过期数据或批量删除特定类型的数据。
     *
     * @param key         前缀键，用于匹配要删除的键值对。如果此参数为空或没有文本内容，则方法不执行任何操作。
     * @param scanDelType 扫描并删除的类型，决定了删除操作的具体行为。不同的枚举值可能对应不同的删除策略或条件。
     */
    public void deleteRightLike(String key, ScanDelType scanDelType) {
        // 检查输入的key是否有文本内容，如果没有则直接返回，不执行删除操作。
        if (!StringUtils.hasText(key)) {
            return;
        }
        // 构建扫描选项，指定匹配规则为键前缀为key，并设置每次扫描的条目数。
        // 这里的SCAN_GROUP_SIZE是一个预定义的常量，用于控制每次删除操作的范围。
        ScanOptions options = ScanOptions.scanOptions().match(key + "*").count(SCAN_GROUP_SIZE).build();
        // 执行模糊删除操作，根据指定的扫描选项和删除类型进行数据删除。
        fuzzyDelete(options, scanDelType);
    }

    /**
     * 删除以指定字符串开头的数据项。
     * <p>
     * 本方法通过构造模糊查询条件，匹配以给定字符串开头的键值，并删除这些键值对应的记录。
     * 这种方式的删除操作适用于需要批量清除一类数据的情况，比如清理过期的缓存数据。
     *
     * @param key 开头字符串，用于匹配需要删除的数据项。如果此参数为空或只包含空白字符，则不执行任何操作。
     */
    public void deleteLeftLike(String key) {
        // 检查输入的key是否有有效文本，如果没有则直接返回，不执行删除操作
        if (!StringUtils.hasText(key)) {
            return;
        }
        // 构造查询选项，设置匹配规则为以key开头的字符串，以及每次查询的记录数量
        ScanOptions options = ScanOptions.scanOptions().match("*" + key).count(SCAN_GROUP_SIZE).build();
        // 执行模糊查询并删除匹配到的数据项
        fuzzyDelete(options);
    }

    /**
     * 根据指定的键前缀删除左侧匹配的数据。
     * <p>
     * 本方法通过构造模糊匹配条件，对Redis中的数据进行扫描，并根据指定的删除策略删除匹配的数据。
     * 主要用于处理需要批量删除一类数据的场景，如清理过期的缓存数据等。
     *
     * @param key         数据的键前缀。只有键名以该前缀开头的数据会被删除。
     *                    如果传入的key为空或只包含空白字符，则不执行任何操作。
     * @param scanDelType 删除策略类型。决定了扫描和删除数据的方式。
     *                    可以根据实际需求选择不同的删除策略，以优化删除操作的性能和效果。
     */
    public void deleteLeftLike(String key, ScanDelType scanDelType) {
        // 检查key是否为空或只包含空白字符，如果是，则直接返回，不执行删除操作
        if (!StringUtils.hasText(key)) {
            return;
        }
        // 构造扫描选项，设置匹配模式为键名以key为前缀的数据，每次扫描返回的条目数为SCAN_GROUP_SIZE
        ScanOptions options = ScanOptions.scanOptions().match("*" + key).count(SCAN_GROUP_SIZE).build();
        // 根据指定的删除策略，执行模糊匹配数据的删除操作
        fuzzyDelete(options, scanDelType);
    }

    /**
     * 删除符合指定关键字的记录。
     * <p>
     * 本方法通过模糊匹配关键字的方式，删除数据存储中与关键字相关的所有记录。
     * 使用模糊匹配是为了应对关键字可能出现在字段的任意位置的情况。
     *
     * @param key 删除操作的关键字。如果关键字为空或只包含空白字符，则不执行删除操作。
     *            关键字将被用于构建模糊匹配模式，以找到所有与之相关的记录。
     */
    public void deleteLike(String key) {
        // 检查关键字是否有效，无效则直接返回，不执行删除操作
        if (!StringUtils.hasText(key)) {
            return;
        }
        // 构建扫描选项，指定匹配模式和每次扫描的数量
        ScanOptions options = ScanOptions.scanOptions().match("*" + key + "*").count(SCAN_GROUP_SIZE).build();
        // 执行模糊删除操作
        fuzzyDelete(options);
    }

    /**
     * 根据指定的键值模式删除数据。
     * <p>
     * 本方法通过模糊匹配键值的方式，删除与给定键值模式匹配的数据。这在需要批量删除一类数据但又不确切知道所有键值时非常有用。
     *
     * @param key         需要删除的数据的键值模式的一部分。必须是非空字符串，否则方法将直接返回不做任何操作。
     *                    例如，如果键值模式是 "user_id_123"，而 key 为 "123"，则会匹配并删除所有以 "123" 结尾的键值。
     * @param scanDelType 删除操作的类型，决定了模糊匹配的方式和删除的策略。具体的类型定义和使用方式请参考 ScanDelType 类的文档。
     */
    public void deleteLike(String key, ScanDelType scanDelType) {
        // 检查键值参数是否为空，如果为空则直接返回，不执行删除操作。
        if (!StringUtils.hasText(key)) {
            return;
        }
        // 构建模糊匹配选项，设置匹配模式为键值模式的星号加给定的键值加星号，这样可以匹配到所有包含这个键值的键。
        // 同时设置每次扫描的数量为 SCAN_GROUP_SIZE，这样可以控制每次删除的数据量。
        ScanOptions options = ScanOptions.scanOptions().match("*" + key + "*").count(SCAN_GROUP_SIZE).build();
        // 执行模糊删除操作，根据给定的删除类型进行数据删除。
        fuzzyDelete(options, scanDelType);
    }

    /**
     * 模糊删除Redis中的键。
     * 使用ScanOptions配置扫描选项，迭代扫描Redis中的键，并根据配置的条件进行删除。
     * 这种方式适用于需要批量删除大量但不确定具体键名的场景，如清理过期数据等。
     *
     * @param options 扫描选项，用于配置扫描的行为和条件。
     */
    public void fuzzyDelete(ScanOptions options) {
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            // 将扫描结果转换为List，以便进一步处理。
            List<String> keys = cursor.stream().toList();
            // 如果扫描结果为空，则无需进行删除操作。
            if (CollectionUtils.isEmpty(keys)) {
                return;
            }
            // 使用Iterator逐个处理键，以分批删除。
            Iterator<String> iterator = keys.iterator();
            // 用于批量删除的键集合。
            Set<String> keySet = new HashSet<>();
            while (iterator.hasNext()) {
                keySet.add(iterator.next());
                // 当集合中的键数量达到批量删除的大小时，执行删除操作。
                if (keySet.size() == SCAN_GROUP_SIZE) {
                    redisTemplate.delete(keySet);
                    keySet.clear();
                }
            }
            // 处理最后一批键，如果有的话。
            // 最后的检查
            if (keySet.size() != 0) {
                redisTemplate.delete(keys);
            }
        }
    }

    /**
     * 模糊删除数据。
     * 根据提供的选项和删除类型，模糊匹配并删除相应数据。如果删除类型为字符串，则直接调用单个键的删除方法；
     * 否则，通过扫描匹配所有键并逐个删除。
     *
     * @param options     扫描选项，定义了模糊匹配的条件。
     * @param scanDelType 删除类型，决定删除操作的具体实现方式。
     */
    public void fuzzyDelete(ScanOptions options, ScanDelType scanDelType) {
        // 如果删除类型为字符串，则直接调用单个键的删除方法
        if (scanDelType.equals(ScanDelType.String)) {
            fuzzyDelete(options);
            return;
        }
        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            // 将扫描结果转换为列表
            List<String> keys = cursor.stream().toList();
            // 如果键列表为空，则无需进行删除操作
            if (CollectionUtils.isEmpty(keys)) {
                return;
            }
            // 遍历所有匹配的键，并根据删除类型执行删除操作
            // 对于每一个 key 都执行对应方法
            for (String next : keys) {
                delByScan(next, scanDelType);
            }
        }
    }

    /**
     * 将集合值存储到Redis列表中。
     *
     * @param key    Redis中的键。
     * @param values 要存储到列表中的值集合。
     *               <p>
     *               如果键不存在，则创建一个新的列表并添加值。
     *               如果键已存在且列表非空，则先删除现有列表中的所有元素，然后再添加新值。
     *               </p>
     * @param <T>    值的泛型类型。
     */
    public <T> void setList(String key, Collection<T> values) {
        // 检查键或值集合是否为空，如果为空则不执行任何操作
        if (!StringUtils.hasText(key) || CollectionUtils.isEmpty(values)) {
            return;
        }
        // 如果键已存在，则先删除现有的列表项，为添加新值做准备
        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            delByScan(key, ScanDelType.LIST);
        }
        // 将值集合转换为JSON字符串数组
        // 转为 json array
        String[] collect = values.stream().map(JSON::toJSONString).toArray(String[]::new);
        // 将JSON字符串数组添加到Redis列表的尾部
        redisTemplate.opsForList().rightPushAll(key, collect);
    }

    /**
     * 根据给定的键和目标类型，从Redis列表中获取对应类型的对象列表。
     * <p>
     * 该方法首先通过键获取Redis列表中的所有元素，然后将这些字符串元素映射为指定类型的对象。
     * 如果列表为空，则返回null。否则，返回转换后的对象列表。
     *
     * @param key    Redis中列表的键。
     * @param tClass 指定列表元素要转换成的目标类型。
     * @param <T>    泛型参数，表示目标类型。
     * @return 转换后的对象列表，如果原始列表为空，则返回null。
     */
    public <T> List<T> getList(String key, Class<T> tClass) {
        // 从Redis中获取指定键对应的列表的所有元素
        List<String> range = redisTemplate.opsForList().range(key, 0, -1);

        // 检查获取的列表是否为空，如果为空则直接返回null
        if (CollectionUtils.isEmpty(range)) {
            return null;
        }

        // 将字符串元素映射为指定类型的对象，并过滤掉转换失败的元素
        return range.stream()
                .map(item -> JSON.parseObject(item, tClass))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 根据给定的键从Redis列表中获取所有值。
     * <p>
     * 该方法首先尝试从Redis列表中获取指定键的所有元素。如果列表为空，方法将返回null。
     * 否则，它将对列表中的每个元素应用JSON解析，将JSON字符串转换为字符串对象，并过滤掉任何null结果。
     * 最后，将处理后的元素收集到一个新的列表中并返回。
     *
     * @param key Redis中列表的键。
     * @return 包含Redis列表中所有元素的字符串列表，如果列表为空，则为null。
     */
    public List<String> getList(String key) {
        // 从Redis列表中获取指定键的所有元素
        List<String> range = redisTemplate.opsForList().range(key, 0, -1);

        // 检查结果列表是否为空，为空则返回null
        if (CollectionUtils.isEmpty(range)) {
            return null;
        }

        // 对列表中的每个元素进行JSON解析，过滤掉null，并收集到新的列表中
        return range.stream()
                .map(item -> JSON.parseObject(item, String.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 将指定集合中的元素转换为JSON字符串，并添加到Redis的列表中
     * 请注意添加的元素与Redis列表的元素类型必须相同。
     *
     * @param key    要添加元素的列表的键
     * @param values 要添加到列表中的元素集合
     * @param <T>    泛型参数，表示集合中元素的类型
     */
    public <T> void addToList(String key, Collection<T> values) {
        // 如果集合为空或键值为空字符串，则不执行添加操作
        if (values.isEmpty() || !StringUtils.hasText(key)) {
            return;
        }
        // 将集合中的每个元素转换为JSON字符串
        String[] array = values.stream().map(JSON::toJSONString).toArray(String[]::new);
        // 将转换后的JSON字符串列表添加到Redis列表的末尾
        redisTemplate.opsForList().rightPushAll(key, array);
    }

    /**
     * 向指定列表添加元素
     *
     * @param key   列表的键
     * @param value 要添加的值对象
     *              <p>
     *              此方法用于将给定的对象值转换为JSON字符串，并将其添加到指定键的列表中
     *              <p>
     *              请注意添加的元素与Redis列表的元素类型必须相同。
     *              <p>
     *              如果集合为空，或者键值为空字符串，則不执行添加操作
     */
    public void addToList(String key, Object value) {
        // 如果集合为空或键值为空字符串，则不执行添加操作
        if (value == null || !StringUtils.hasText(key)) {
            return;
        }
        // 将对象转换为JSON字符串并存入
        redisTemplate.opsForList().rightPush(key, JSON.toJSONString(value));
    }

    /**
     * 从Redis列表中移除指定值
     *
     * @param key   要操作的列表的键
     * @param value 要移除的值对象
     */
    public void removeFormList(String key, Object value) {
        // 如果集合为空或键值为空字符串，则不执行添加操作
        if (value == null || !StringUtils.hasText(key)) {
            return;
        }
        // 将值对象转换为JSON字符串，并从列表的尾部开始移除匹配的元素
        redisTemplate.opsForList().remove(key, -1, JSON.toJSONString(value));
    }

    /**
     * 从Redis列表中批量移除指定的元素
     * 注意集合的大小不应过大，否则会影响Redis性能
     *
     * @param key    要操作的列表的键值
     * @param values 要移除的元素集合
     * @param <T>    泛型参数，表示集合中元素的类型
     */
    public <T> void removeFormList(String key, Collection<T> values) {
        // 如果集合为空或键值为空字符串，则不执行添加操作
        if (values == null || !StringUtils.hasText(key)) {
            return;
        }
        // 遍历集合，将每个值对象转换为JSON字符串，并从列表的尾部开始移除匹配的元素
        for (T value : values) {
            String s = JSON.toJSONString(value);
            redisTemplate.opsForList().remove(key, -1, s);
        }
    }

    /**
     * 删除指定的键。
     * <p>
     * 使用unlink命令而不是delete，是因为unlink在删除键的同时，还能在后台回收内存，这样可以避免因大量删除操作导致的性能问题。
     * 这个方法接受可变参数keys，允许一次性删除多个键，提高了操作的便利性。
     *
     * @param keys 要删除的键，可以是多个键。
     */
    public void unLinkKey(String... keys) {
        // 将可变参数转换为List集合，为的是调用redisTemplate的unlink方法，该方法要求参数类型为Collection。
        List<String> collect = Arrays.stream(keys).collect(Collectors.toList());
        // 调用redisTemplate的unlink方法，删除指定的键。
        redisTemplate.unlink(collect);
    }

    /**
     * 根据指定的键和删除类型，删除Redis中的键。
     * 使用扫描策略逐步删除键，以避免一次性删除大量键可能导致的性能问题。
     *
     * @param key  要删除的键，如果键为空字符串或null，则不执行删除操作。
     * @param type 删除策略类型，定义了具体的删除逻辑。
     */
    public void delByScan(String key, ScanDelType type) {
        // 检查键是否为空，如果为空则直接返回，不执行删除操作。
        if (!StringUtils.hasText(key)) {
            return;
        }

        // 设置扫描选项，指定每次扫描的元素数量。
        ScanOptions scanOptions = ScanOptions.scanOptions().count(SCAN_GROUP_SIZE).build();

        // 用于存储扫描到的键的列表。
        List<Object> keyList = new ArrayList<>();

        // 根据指定的删除类型，执行删除操作。逐步删除键，直到完成所有键的删除。
        type.del(redisTemplate, key, SCAN_GROUP_SIZE, scanOptions, keyList);

        // 最后，直接删除原始键。
        redisTemplate.delete(key);
    }

    /**
     * 将集合中的元素添加到Redis的集合中。
     * 通过将集合中的每个元素转换为JSON字符串，确保数据的序列化一致性，
     * 然后将这些JSON字符串作为成员添加到指定键的Redis集合中。
     * 如果集合为空，则不执行任何操作。
     *
     * @param key    Redis中集合的键。
     * @param values 要添加到集合中的值的集合。
     */
    public <T> void addToSet(String key, Collection<T> values) {
        // 参数校验
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        // 转为 String 数组
        String[] array = values.stream().map(JSON::toJSONString).toArray(String[]::new);
        redisTemplate.opsForSet().add(key, array);
    }

    /**
     * 将给定的值添加到指定键对应的集合中。
     * 如果值为null，则不进行任何操作。
     * 使用JSON.toJSONString将值转换为字符串格式，然后添加到Redis的集合中。
     * 这种方法适用于需要将Java对象存储为字符串的场景。
     *
     * @param key   集合的键，用于在Redis中唯一标识集合。
     * @param value 要添加到集合中的值，可以是任何Java对象，将被转换为JSON字符串。
     */
    public void addToSet(String key, Object value) {
        // 检查值是否为null，避免不必要的转换和操作。
        if (value == null) {
            return;
        }
        // 将值转换为JSON字符串，并添加到指定键的集合中。
        // 转为 json 并存入
        redisTemplate.opsForSet().add(key, JSON.toJSONString(value));
    }

    /**
     * 根据给定的键和目标类类型，从Redis中获取对应集合的值，并将其转换为目标类型。
     *
     * @param key    Redis中集合的键。
     * @param tClass 目标元素的类类型。
     * @return 转换后的目标类型集合。如果键不存在或为空，则返回null。
     */
    public <T> Set<T> getSet(String key, Class<T> tClass) {
        // 检查键是否为空
        if (!StringUtils.hasText(key)) {
            return null;
        }
        // 通过Redis模板操作集合，使用SCAN命令分批获取集合中的元素。
        // 使用Stream API对获取的元素进行类型转换，并过滤掉转换后的null值。
        // 最后将转换后的元素收集到一个Set中返回。
        return redisTemplate.opsForSet().scan(key, ScanOptions.scanOptions().count(SCAN_GROUP_SIZE).build())
                .stream()
                .map(item -> JSON.parseObject(item, tClass))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     * 从Redis集合中移除指定的值。
     * <p>
     * 此方法用于从Redis的Set数据结构中移除指定的值。如果指定的值为null，则不执行任何操作。
     * 使用JSON.toJSONString将值转换为字符串，以适应Redis的存储需求。
     *
     * @param key   Set的键，用于在Redis中定位集合。
     * @param value 要移除的值，如果为null，则方法直接返回。
     */
    // 从redis set中删除
    public void removeFromSet(String key, Object value) {
        if (value == null) {
            return;
        }
        redisTemplate.opsForSet().remove(key, JSON.toJSONString(value));
    }

    /**
     * 从Redis集合中移除指定的元素。
     * <p>
     * 此方法用于从Redis的集合类型数据中移除给定的一组值。它首先将Java集合转换为JSON字符串数组，
     * 然后调用Redis模板操作集合，执行移除操作。
     *
     * @param key    Redis中集合的键。
     * @param values 要移除的值的集合。如果此集合为空或null，则方法直接返回，不执行任何操作。
     * @param <T>    泛型参数，表示集合中元素的类型。
     */
    public <T> void removeFromSet(String key, Collection<T> values) {
        // 检查集合是否为空，如果为空，则不执行任何操作
        // 参数校验
        if (CollectionUtils.isEmpty(values)) {
            return;
        }
        // 将集合中的每个元素转换为JSON字符串，然后转换为字符串数组
        // 转为json数组
        String[] array = values.stream().map(JSON::toJSONString).toArray(String[]::new);
        // 使用Redis模板的集合操作接口，从指定的集合中移除指定的元素
        redisTemplate.opsForSet().remove(key, array);
    }

    /**
     * 将给定的值存储到Redis哈希表中。
     * <p>
     * 此方法用于将一个对象值存储到Redis的哈希表中，特定的字段由hashKey指定，对象由value表示。
     * 如果key或hashKey为空，或者value为null，则不进行任何操作。
     * 使用JSON.toJSONString将value转换为JSON字符串，以确保对象可以被序列化并存储到Redis中。
     *
     * @param key     哈希表的键，用于在Redis中唯一标识一个哈希表。
     * @param hashKey 在哈希表中，用于唯一标识一个字段的键。
     * @param value   要存储在哈希表中的对象值。
     */
    public void putToHash(String key, String hashKey, Object value) {
        // 检查key和hashKey是否为空，如果为空则不进行操作
        // 检查键是否为空
        if (!StringUtils.hasText(key) || !StringUtils.hasText(hashKey)) {
            return;
        }
        // 检查value是否为null，如果为null则不进行操作
        // 检查值是否为null
        if (value == null) {
            return;
        }
        // 使用Redis模板的opsForHash方法将value序列化为JSON字符串，并存储到指定的key和hashKey对应的位置
        // 将值转换为JSON字符串，并使用Redis模板的哈希操作接口将键值对存储到Redis中
        redisTemplate.opsForHash().put(key, hashKey, JSON.toJSONString(value));
    }

    /**
     * 从Redis的哈希表中获取指定键值对的值，并将其转换为指定的Java类型。
     *
     * @param key     Redis中的哈希表的键。
     * @param hashKey 哈希表中要获取的具体键。
     * @param tClass  指定的Java类型，用于将获取的值转换为该类型。
     * @param <T>     泛型参数，表示转换后的类型。
     * @return 转换后的指定类型的对象。
     */
    public <T> T getFromHash(String key, String hashKey, Class<T> tClass) {
        // 使用RedisTemplate的opsForHash方法获取指定键值对的值，确保值不为空。
        return JSON.parseObject(Objects.requireNonNull(redisTemplate.opsForHash().get(key, hashKey)).toString(), tClass);
    }

    /**
     * 从Redis的哈希表中删除指定的字段。
     * <p>
     * 此方法用于从一个特定的Redis哈希表中删除指定的字段。哈希表由键（key）和字段（hashKey）唯一标识，
     * 删除操作只会影响指定的字段，不会影响哈希表中的其他字段。
     *
     * @param key     哈希表的键，用于唯一标识哈希表。
     * @param hashKey 要删除的字段，在哈希表中由该字段名标识。
     */
    public void removeFromHash(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 从Redis的哈希表中删除指定的字段。
     * <p>
     * 此方法用于从一个特定的Redis哈希表中删除一个或多个字段。它首先通过redisTemplate的opsForHash()方法获取到操作哈希表的接口，
     * 然后调用delete()方法来删除指定的字段。这个方法特别适用于需要一次性删除多个字段的场景，提高了操作的效率。
     *
     * @param key      哈希表的键，指定要操作的哈希表。
     * @param hashKeys 要删除的字段集合，这些字段属于指定的哈希表。
     *                 <p>
     *                 注意：这个方法不会返回删除操作的结果，因为删除操作在Redis中总是成功的。
     */
    public void delFromHash(String key, Collection<String> hashKeys) {
        redisTemplate.opsForHash().delete(key, hashKeys.toArray());
    }

    /**
     * 使用布隆过滤器检查某个元素是否存在
     *
     * @param checkItem 待检查的元素名称，用于在布隆过滤器中定位
     * @param key       待检查的元素键值，用于计算哈希值
     * @return boolean 表示待检查元素是否可能存在
     * <p>
     * 注意：布隆过滤器可能会产生误报（False Positive），即不存在的元素被误判为存在
     * 但不会将存在的元素误判为不存在。因此，该方法返回true仅表示可能存在，需要进一步验证；
     * 而返回false则可以确定元素不存在。
     */
    public boolean checkWithBloomFilter(String checkItem, String key) {
        // 根据key计算元素在布隆过滤器中的索引位置
        long index = dealWithHash(key);

        // 通过Redis的getBit方法检查索引位置的值是否为1，即该元素是否存在
        boolean isExist = Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(checkItem, index));

        // 记录日志，输出检查结果
        log.info("----------> key:{} 对应坑位下标:{} 是否存在:{}", key, index, isExist);

        // 返回检查结果
        return isExist;
    }

    /**
     * 将指定的项添加到布隆过滤器中
     * 布隆过滤器是一种空间效率高、用于判断一个元素是否可能在一个集合中的数据结构
     *
     * @param checkItem 待添加到布隆过滤器的项，通常是一个标识符或检查的元素
     * @param key       用于生成索引的键，该索引将用于在布隆过滤器中设置位
     */
    public void addToBloomFilter(String checkItem, Object key) {
        // 根据提供的键计算要设置的位的索引
        long index = dealWithHash(key);
        // 使用计算出的索引在Redis中设置位，表示该项已添加到布隆过滤器
        redisTemplate.opsForValue().setBit(checkItem, index, true);
    }
}
