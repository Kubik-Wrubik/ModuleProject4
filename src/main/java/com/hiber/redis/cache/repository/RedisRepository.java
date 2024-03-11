package com.hiber.redis.cache.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class RedisRepository<T> {
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
                preparedRecords.append(mapper.writeValueAsString(t)).append("\n");
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
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
            throw new RuntimeException(e);
        }
        return cityCountries;
    }
    public Jedis getJedis() {
        return jedis;
    }
}
