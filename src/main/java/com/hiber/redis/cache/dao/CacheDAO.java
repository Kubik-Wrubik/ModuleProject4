package com.hiber.redis.cache.dao;

import com.hiber.dao.AbstractDAO;
import com.hiber.redis.cache.CacheGenerator;
import com.hiber.redis.cache.converts.CacheConverter;
import com.hiber.redis.cache.repository.RedisRepository;
import redis.clients.jedis.Jedis;

import java.util.List;

public class CacheDAO<E, T> {
    private final AbstractDAO<E> DAO;
    private final CacheConverter<T, E> converter;
    private final RedisRepository<T> redisRepository;
    private final CacheGenerator cacheGenerator;

    public CacheDAO(AbstractDAO<E> DAO, CacheConverter<T, E> converter, Jedis jedis) {
        this.DAO = DAO;
        this.converter = converter;
        this.redisRepository = new RedisRepository<>(jedis, converter.getDTOType());
        this.cacheGenerator = new CacheGenerator(redisRepository.getJedis());
    }

    public E cachedGetById(final int id) {
        List<E> result;
        String queryString = String.format("%s::getById(%d)", DAO.getEntityName(), id);
        result = pullCitiesIfKeyInRedisExist(queryString);
        if (result == null) {
            result = List.of(DAO.getById(id));
            pushCitiesIfFrequencyMoreThanTen(result, queryString);
        }
        return result.get(0);
    }

    public List<E> cachedGetItems(int from, int count) {
        List<E> result;
        String queryString = String.format("%s::getItems(%d, %d)", DAO.getEntityName(), from, count);
        result = pullCitiesIfKeyInRedisExist(queryString);
        if (result == null) {
            result = DAO.getItems(from, count);
            pushCitiesIfFrequencyMoreThanTen(result, queryString);
        }
        return result;
    }

    public List<E> cachedGetAll() {
        List<E> result;
        String queryString = String.format("%s::getAll()", DAO.getEntityName());
        result = pullCitiesIfKeyInRedisExist(queryString);
        if (result == null) {
            result = DAO.getAll();
            pushCitiesIfFrequencyMoreThanTen(result, queryString);
        }
        return result;
    }

    public E cachedCreate(E e) {
        cacheGenerator.updateCache();
        return DAO.create(e);
    }

    public E cachedUpdate(E e) {
        cacheGenerator.updateCache();
        return DAO.update(e);
    }

    public void cachedDelete(E e) {
        cacheGenerator.updateCache();
        DAO.delete(e);
    }

    public void cachedDeleteById(int eId) {
        cacheGenerator.updateCache();
        DAO.deleteById(eId);
    }

    private List<E> pullCitiesIfKeyInRedisExist(String queryString) {
        cacheGenerator.updateOrAddToFrequencyMap(queryString);
        if (cacheGenerator.isKeyInRedisExist(queryString)) {
            return converter.extractData(redisRepository.pullDataFromRedis(queryString));
        }
        return null;
    }

    private void pushCitiesIfFrequencyMoreThanTen(List<E> eList, String queryString) {
        if (cacheGenerator.isQueryFrequencyMoreThanTen(queryString)) {
            redisRepository.pushDataToRedis(queryString, converter.prepareData(eList));
        }
    }
}
