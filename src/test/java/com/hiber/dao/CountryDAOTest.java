package com.hiber.dao;

import com.hiber.domain.City;
import com.hiber.domain.Continent;
import com.hiber.domain.Country;
import com.hiber.domain.CountryLanguage;
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

class CountryDAOTest {
    private static CountryDAO CountryDAO;

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
    public static void prepareCountryDAO() {
        final SessionFactory sessionFactory;

        sessionFactory = new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .buildSessionFactory();
        CountryDAOTest.CountryDAO = new CountryDAO(sessionFactory);

    }

    @Test
    public void getByIdTest() {
        Session currentSession = CountryDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        Query<Integer> query = currentSession.createQuery("select min(id) from Country", Integer.class);
        Integer id = query.getSingleResult();
        Country country = CountryDAO.getById(id);
        transaction.commit();
        Assertions.assertEquals(id, country.getId());
    }

    @Test
    public void getItemsCountTest() {
        int from = 0;
        int count = 1;
        Transaction transaction = CountryDAO.getCurrentSession().getTransaction();
        transaction.begin();
        List<Country> countryList = CountryDAO.getItems(from, count);
        transaction.commit();
        Assertions.assertEquals(countryList.size(), count);
    }

    @Test
    public void getAllTest() {
        Transaction transaction = CountryDAO.getCurrentSession().getTransaction();
        transaction.begin();
        List<Country> countryList = CountryDAO.getAll();
        Query<Long> query = CountryDAO.getCurrentSession().createQuery("select count(id) from Country", Long.class);
        Long countryAmount = query.uniqueResult();
        transaction.commit();
        Assertions.assertEquals(countryList.size(), countryAmount);
    }

    @Test
    public void createTest() {
        Session currentSession = CountryDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        Country exCountry = new Country();
        String countryName = "example country";
        Query<Integer> maxIdQuery = currentSession.createQuery("select max(id) from Country", Integer.class);
        int maxId = maxIdQuery.getSingleResult();
        exCountry.setId(maxId + 1);
        exCountry.setName(countryName);
        exCountry.setCode("exC");
        exCountry.setAlternativeCode("eC");
        exCountry.setContinent(Continent.AFRICA);
        exCountry.setRegion("example region");
        exCountry.setSurfaceArea(new BigDecimal(1));
        exCountry.setIndepYear((short) 1);
        exCountry.setPopulation(1);
        exCountry.setLifeExpectancy(new BigDecimal(1));
        exCountry.setGnp(new BigDecimal(1));
        exCountry.setGnpoId(new BigDecimal(1));
        exCountry.setLocalName("example local name");
        exCountry.setGovernmentForm("example government form");
        exCountry.setHeadOfState("example head of state");
        exCountry.setCapital(
                currentSession.createQuery("from City order by id", City.class)
                        .setMaxResults(1)
                        .getSingleResult()
        );
        CountryDAO.create(exCountry);
        Query<Country> query = currentSession.createQuery("from Country where name = :name", Country.class);
        query.setParameter("name", countryName);
        Country CountryFromDB = query.getSingleResult();
        transaction.commit();
        Assertions.assertEquals(CountryFromDB, exCountry);
    }

    @Test
    public void updateTest() {
        Session currentSession = CountryDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        Query<Country> query = currentSession.createQuery("from Country order by id", Country.class);
        Country firstCountry = query.setMaxResults(1).getSingleResult();
        firstCountry.setName("updated country name");
        CountryDAO.update(firstCountry);
        Query<Country> query2 = currentSession.createQuery("from Country order by id", Country.class);
        Country firstCountryAfterUpdate = query2.setMaxResults(1).getSingleResult();
        transaction.commit();
        Assertions.assertEquals(firstCountryAfterUpdate, firstCountry);
    }
}