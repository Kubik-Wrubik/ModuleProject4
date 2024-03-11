package com.hiber.dao;

import com.hiber.domain.CountryLanguage;
import org.hibernate.SessionFactory;

public class CountryLanguageDAO extends AbstractDAO<CountryLanguage> {
    public CountryLanguageDAO(SessionFactory sessionFactory) {
        super(CountryLanguage.class, sessionFactory);
    }


}
