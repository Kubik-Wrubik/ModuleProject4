package com.hiber;

import com.hiber.dao.CityDAO;
import com.hiber.domain.City;
import com.hiber.domain.Country;
import com.hiber.domain.CountryLanguage;
import com.hiber.redis.cache.converts.CityCacheConverter;
import com.hiber.redis.cache.dao.CacheDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;

import java.util.List;

import static java.util.Objects.nonNull;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final SessionFactory sessionFactory;
    private final JedisPool pool;


    public Main() {
        sessionFactory = prepareRelationalDb();
        pool = prepareRedisClient();
    }

    public static void main(String[] args) {
        Main main = new Main();
        CityDAO cityDAO = new CityDAO(main.sessionFactory);
        CityCacheConverter converter = new CityCacheConverter();
        var languageCacheDAO = new CacheDAO<>(cityDAO, converter, main.pool.getResource());
        Session session = main.sessionFactory.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        for (int i = 0; i < 30; i++) {
            long start = System.currentTimeMillis();
            List<City> cities = languageCacheDAO.cachedGetItems(0, 10);
            long stop = System.currentTimeMillis();
            cities.forEach(System.out::println);
            System.out.println(stop - start + " ms   i:" + i);

            if (i == 19) {
                languageCacheDAO.cachedDeleteById(26);
            }
        }
        transaction.commit();
        main.shutdown();

    }

    private JedisPool prepareRedisClient() {
        return new JedisPool("localhost", 6379);
    }

    private SessionFactory prepareRelationalDb() {
        final SessionFactory sessionFactory;

        sessionFactory = new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .buildSessionFactory();
        return sessionFactory;
    }

    private void shutdown() {
        if (nonNull(sessionFactory)) {
            sessionFactory.close();
        }
        if (nonNull(pool)) {
            pool.close();
        }
    }
}