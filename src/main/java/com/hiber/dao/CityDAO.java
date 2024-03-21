package com.hiber.dao;

import com.hiber.domain.City;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

public class CityDAO extends AbstractDAO<City> {
    public CityDAO(SessionFactory sessionFactory) {
        super(City.class, sessionFactory);
    }

    public int getTotalCount() {
        Query<Long> query = getCurrentSession().createQuery("select count(c) from City c", Long.class);
        return Math.toIntExact(query.uniqueResult());
    }

    @Override
    public City getById(final int id) {
        Query<City> query = getCurrentSession().createQuery("select c from City c join fetch c.country where c.id = :ID", City.class);
        query.setParameter("ID", id);
        return query.getSingleResult();
    }
}
