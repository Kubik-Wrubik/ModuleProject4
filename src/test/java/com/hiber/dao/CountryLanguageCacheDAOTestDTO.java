package com.hiber.dao;

import com.hiber.domain.City;
import com.hiber.domain.Country;
import com.hiber.domain.CountryLanguage;
import jakarta.persistence.NoResultException;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;

class CountryLanguageCacheDAOTestDTO {
    private static CountryLanguageDAO countryLanguageDAO;

    @BeforeAll
    public static void initData() throws Exception {
        Properties properties = new Properties();
        FileReader connectionProperties = new FileReader("src/test/resources/connection-test.properties");
        properties.load(connectionProperties);
        connectionProperties.close();

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("connection-string"),
                properties.getProperty("user"),
                properties.getProperty("password"))
        ) {
            ScriptRunner sr = new ScriptRunner(connection);
            BufferedReader bufferedReader = new BufferedReader(new FileReader("src/test/resources/init.sql"));
            sr.setLogWriter(null);
            sr.runScript(bufferedReader);
        }
    }

    @BeforeAll
    public static void prepareCountryLanguageDAO() {
        final SessionFactory sessionFactory;

        sessionFactory = new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .buildSessionFactory();
        CountryLanguageCacheDAOTestDTO.countryLanguageDAO = new CountryLanguageDAO(sessionFactory);

    }

    @Test
    public void getByIdTest() {
        Session currentSession = countryLanguageDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        Query<Integer> query = currentSession.createQuery("select min(id) from CountryLanguage", Integer.class);
        Integer id = query.getSingleResult();
        CountryLanguage countryLanguage = countryLanguageDAO.getById(id);
        transaction.commit();
        Assertions.assertEquals(id, countryLanguage.getId());
    }

    @Test
    public void getItemsCountTest() {
        int from = 0;
        int count = 1;
        Transaction transaction = countryLanguageDAO.getCurrentSession().getTransaction();
        transaction.begin();
        List<CountryLanguage> countryLanguageList = countryLanguageDAO.getItems(from, count);
        transaction.commit();
        Assertions.assertEquals(countryLanguageList.size(), count);
    }

    @Test
    public void getAllTest() {
        Transaction transaction = countryLanguageDAO.getCurrentSession().getTransaction();
        transaction.begin();
        List<CountryLanguage> countryLanguageList = countryLanguageDAO.getAll();
        Query<Long> query = countryLanguageDAO.getCurrentSession().createQuery("select count(id) from CountryLanguage", Long.class);
        Long citiesAmount = query.uniqueResult();
        transaction.commit();
        Assertions.assertEquals(countryLanguageList.size(), citiesAmount);
    }

    @Test
    public void createTest() {
        Session currentSession = countryLanguageDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        CountryLanguage exCountryLanguage = new CountryLanguage();
        String countryLanguageName = "example language name";
        exCountryLanguage.setLanguage(countryLanguageName);
        exCountryLanguage.setOfficial(false);
        exCountryLanguage.setPercentage(new BigDecimal(1));
        exCountryLanguage.setCountry(
                currentSession.createQuery("from Country order by id", Country.class)
                        .setMaxResults(1)
                        .getSingleResult()
        );
        countryLanguageDAO.create(exCountryLanguage);
        Query<CountryLanguage> query = currentSession.createQuery("from CountryLanguage where language = :language", CountryLanguage.class);
        query.setParameter("language", countryLanguageName);
        CountryLanguage countryLanguageFromDB = query.getSingleResult();
        transaction.commit();
        Assertions.assertEquals(countryLanguageFromDB, exCountryLanguage);
    }

    @Test
    public void updateTest() {
        Session currentSession = countryLanguageDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        Query<CountryLanguage> query = currentSession.createQuery("from CountryLanguage order by id", CountryLanguage.class);
        CountryLanguage firstCountryLanguage = query.setMaxResults(1).getSingleResult();
        firstCountryLanguage.setLanguage("updated country language name");
        countryLanguageDAO.update(firstCountryLanguage);
        Query<CountryLanguage> query2 = currentSession.createQuery("from CountryLanguage order by id", CountryLanguage.class);
        CountryLanguage firstCountryLanguageAfterUpdate = query2.setMaxResults(1).getSingleResult();
        transaction.commit();
        Assertions.assertEquals(firstCountryLanguageAfterUpdate, firstCountryLanguage);
    }

    @Test
    public void deleteTest() {
        Session currentSession = countryLanguageDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        Query<CountryLanguage> query = currentSession.createQuery("from CountryLanguage order by id", CountryLanguage.class);
        CountryLanguage firstCountryLanguage = query.setMaxResults(1).getSingleResult();
        countryLanguageDAO.delete(firstCountryLanguage);
        Query<CountryLanguage> query2 = currentSession.createQuery("from CountryLanguage order by id", CountryLanguage.class);
        CountryLanguage firstCountryLanguageAfterDelete = query2.setMaxResults(1).getSingleResult();
        transaction.commit();
        Assertions.assertNotEquals(firstCountryLanguageAfterDelete, firstCountryLanguage);
    }

    @Test
    public void deleteByIdTest() {
        Session currentSession = countryLanguageDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        int id = currentSession.createQuery("select min(id) from CountryLanguage", Integer.class).getSingleResult();
        Query<CountryLanguage> query = currentSession.createQuery("from CountryLanguage where id = :id", CountryLanguage.class);
        CountryLanguage countryLanguageById = query.setParameter("id", id)
                .setMaxResults(1)
                .getSingleResult();
        countryLanguageDAO.deleteById(id);
        Query<CountryLanguage> query2 = currentSession.createQuery("from CountryLanguage where id = :id", CountryLanguage.class);
        Query<CountryLanguage> preparedQuery2 = query2.setParameter("id", id).setMaxResults(1);
        Exception exception = Assertions.assertThrows(NoResultException.class, preparedQuery2::getSingleResult);
        transaction.commit();
        Assertions.assertEquals("No entity found for query", exception.getMessage());
    }
}