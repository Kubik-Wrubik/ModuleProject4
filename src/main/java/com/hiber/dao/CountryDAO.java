package com.hiber.dao;

import com.hiber.domain.Country;
import org.hibernate.SessionFactory;

import java.util.List;

public class CountryDAO extends AbstractDAO<Country> {
    public CountryDAO(SessionFactory sessionFactory) {
        super(Country.class, sessionFactory);
    }

    @Override
    public List<Country> getAll() {
        return getCurrentSession().createQuery("select distinct c from Country c left join fetch c.languages", Country.class).list();
    }
}
