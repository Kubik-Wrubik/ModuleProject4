package com.hiber.redis.cache;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

public class CacheGenerator {

    private final static int MAX_FREQUENCY_BEFORE_CREATING_CACHE = 10;
    private final Map<String, Integer> queryFrequencyMap;
    private final Jedis jedis;

    public CacheGenerator(Jedis jedis) {
        queryFrequencyMap = new HashMap<>();
        this.jedis = jedis;
    }

    public void updateOrAddToFrequencyMap(String query) {
        if (queryFrequencyMap.containsKey(query)) {
            int currentFrequency = queryFrequencyMap.get(query);
            queryFrequencyMap.put(query, currentFrequency + 1);
        } else {
            queryFrequencyMap.put(query, 0);
        }
    }

    public boolean isQueryFrequencyMoreThanTen(String query) {
        return queryFrequencyMap.get(query) >= MAX_FREQUENCY_BEFORE_CREATING_CACHE;
    }

    public boolean isKeyInRedisExist(String query) {
        return jedis.get(query) != null;
    }

    public void updateCache() {
        queryFrequencyMap.replaceAll((k, v) -> 0);
        jedis.flushAll();
    }
}
