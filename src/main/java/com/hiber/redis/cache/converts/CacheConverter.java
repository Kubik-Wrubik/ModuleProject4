package com.hiber.redis.cache.converts;

import java.util.List;

public abstract class CacheConverter<T, E> {
    protected final Class<T> typeOfDTO;
    protected final Class<E> entityType;
    protected final CacheManager cacheManager;

    public CacheConverter(Class<T> typeOfDTO, Class<E> entityType) {
        this.typeOfDTO = typeOfDTO;
        this.entityType = entityType;
        cacheManager = new CacheManager();
    }

    public abstract List<T> prepareData(List<E> cities);

    public abstract List<E> extractData(List<T> cityCountries);


    public Class<T> getDTOType() {
        return typeOfDTO;
    }

    public Class<E> getEntityType() {
        return entityType;
    }
}
