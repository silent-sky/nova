package com.nova.paas.common.support;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.SerializationUtils;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * zhenghaibo
 * 18/4/11 15:23
 */
public class CacheManager {
    @Inject
    private RedisTemplate<String, Map> mapRedisTemplate;
    @Inject
    private RedisTemplate<String, Object> objectRedisTemplate;
    @Inject
    private StringRedisTemplate stringRedisTemplate;

    private final static StringRedisSerializer stringSerializer = new StringRedisSerializer();
    private final static GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();

    public void putString(String key, String value) {
        stringRedisTemplate.boundValueOps(key).set(value);
    }

    public void putMap(String key, Map<String, Object> map) {
        mapRedisTemplate.boundHashOps(key).putAll(map);
    }

    public String getString(String key) {
        return stringRedisTemplate.boundValueOps(key).get();
    }

    public Map getMap(String key) {
        return mapRedisTemplate.boundHashOps(key).entries();
    }

    /**
     * 添加
     */
    public void putHashObject(String key, String hashKey, Object hashValue) {
        mapRedisTemplate.opsForHash().put(key, hashKey, hashValue);
    }

    /**
     * 查询HashMap某个value
     */
    public Object getHashObject(String key, String hashKey) {
        if (StringUtils.isAnyBlank(key, hashKey)) {
            return null;
        }
        return objectRedisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 查询HashMap的entry
     *
     * @param key 键
     */
    public Object getHashEntries(String key) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return objectRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 批量获取缓存中的数据
     */
    public List<Object> getMultiObject(String key, List<Object> mapKeys) {
        if (StringUtils.isBlank(key) || CollectionUtils.isEmpty(mapKeys)) {
            return null;
        }
        return objectRedisTemplate.opsForHash().multiGet(key, mapKeys);
    }

    /**
     * 批量添加HashMap
     */
    public void putAll(String key, Map<Object, Object> objectMap) {
        if (StringUtils.isBlank(key) || objectMap == null) {
            return;
        }
        objectRedisTemplate.opsForHash().putAll(key, objectMap);
    }

    byte[] rawKey(String key) {
        return this.stringSerializer(key);
    }

    byte[] rawHashKey(String key) {
        return this.stringSerializer(key);
    }

    byte[] rawHashValue(Object value) {
        return this.hashValueSerializer(value);
    }

    byte[] stringSerializer(String str) {
        return stringSerializer.serialize(str);
    }

    byte[] hashValueSerializer(Object s) {
        return valueSerializer.serialize(s);
    }

    List<Object> deserializeHashValues(List<byte[]> rawValues) {
        return this.valueSerializer == null ? (List) rawValues : SerializationUtils.deserialize(rawValues, this.valueSerializer);
    }

    byte[][] rawValues(Object... values) {
        byte[][] rawValues = new byte[values.length][];
        int i = 0;
        Object[] var4 = values;
        int var5 = values.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            Object value = var4[var6];
            rawValues[i++] = this.rawHashValue(value);
        }
        return rawValues;
    }

    byte[][] rawValues(Collection<Object> values) {
        byte[][] rawValues = new byte[values.size()][];
        int i = 0;
        Object value;
        for (Iterator var4 = values.iterator(); var4.hasNext(); rawValues[i++] = this.rawHashValue(value)) {
            value = var4.next();
        }

        return rawValues;
    }

    byte[][] rawHashKeys(Object... hashKeys) {
        byte[][] rawHashKeys = new byte[hashKeys.length][];
        int i = 0;
        Object[] var4 = hashKeys;
        int var5 = hashKeys.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            Object hashKey = var4[var6];
            rawHashKeys[i++] = this.rawHashKey((String) hashKey);
        }
        return rawHashKeys;
    }

    Object deserializeHashValue(byte[] value) {
        return this.valueSerializer.deserialize(value);
    }

    Map<Object, Object> deserializeHashMap(Map<byte[], byte[]> entries) {
        if (entries == null) {
            return null;
        } else {
            LinkedHashMap map = new LinkedHashMap(entries.size());
            Iterator var3 = entries.entrySet().iterator();

            while (var3.hasNext()) {
                Map.Entry entry = (Map.Entry) var3.next();
                map.put(this.deserializeHashKey((byte[]) entry.getKey()), this.deserializeHashValue((byte[]) entry.getValue()));
            }
            return map;
        }
    }

    String deserializeHashKey(byte[] value) {
        return stringSerializer.deserialize(value);
    }

}
