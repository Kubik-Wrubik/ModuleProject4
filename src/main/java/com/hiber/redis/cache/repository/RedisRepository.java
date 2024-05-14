package com.hiber.redis.cache.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class RedisRepository<T> {
    private static final Logger logger = LoggerFactory.getLogger(RedisRepository.class);
    private final Jedis jedis;
    private final ObjectMapper mapper;
    private final Class<T> typeOfDTO;

    public RedisRepository(Jedis jedis, Class<T> clazz) {
        this.jedis = jedis;
        this.mapper = new ObjectMapper();
        this.typeOfDTO = clazz;
    }

    public void pushDataToRedis(String query, List<T> preparedData) {
        StringBuilder preparedRecords = new StringBuilder();
        for (T t : preparedData) {
            try {
                preparedRecords.append(mapper.writeValueAsString(t)).append('\n');
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        logger.info("Cache has been pushed to redis database");
        jedis.set(query, preparedRecords.toString());
    }

    public List<T> pullDataFromRedis(String query) {
        ArrayList<T> cityCountries = new ArrayList<>();
        String dataFromRedis = jedis.get(query);
        try (BufferedReader bufferedReader = new BufferedReader(new StringReader(dataFromRedis))) {
            String obj;
            while ((obj = bufferedReader.readLine()) != null) {
                cityCountries.add(mapper.readValue(obj, typeOfDTO));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("Cache has been pulled from redis database");
        return cityCountries;
    }
    public Jedis getJedis() {
        return jedis;
    }
}
