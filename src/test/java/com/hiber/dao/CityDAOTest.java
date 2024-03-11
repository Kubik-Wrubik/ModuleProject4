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
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Properties;

class CityDAOTest {
    private static CityDAO cityDAO;

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
    public static void prepareCityDAO() {
        final SessionFactory sessionFactory;

        sessionFactory = new Configuration()
                .addAnnotatedClass(City.class)
                .addAnnotatedClass(Country.class)
                .addAnnotatedClass(CountryLanguage.class)
                .buildSessionFactory();
        CityDAOTest.cityDAO = new CityDAO(sessionFactory);

    }

    @Test
    public void getByIdTest() {
        Session currentSession = cityDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
            // Integer id = cityDAO.getAll().get(0).getId();       --tests myst be isolated
        Query<Integer> query = currentSession.createQuery("select min(id) from City", Integer.class);
        Integer id = query.getSingleResult();
        City city = cityDAO.getById(id);
        transaction.commit();
        Assertions.assertEquals(id, city.getId());
    }

    @Test
    public void getItemsCountTest() {
        int from = 0;
        int count = 1;
        Transaction transaction = cityDAO.getCurrentSession().getTransaction();
        transaction.begin();
        List<City> cityList = cityDAO.getItems(from, count);
        transaction.commit();
        Assertions.assertEquals(cityList.size(), count);
    }

    @Test
    public void getAllTest() {
        Transaction transaction = cityDAO.getCurrentSession().getTransaction();
        transaction.begin();
        List<City> cityList = cityDAO.getAll();
        Query<Long> query = cityDAO.getCurrentSession().createQuery("select count(id) from City", Long.class);
        Long citiesAmount = query.uniqueResult();
        transaction.commit();
        Assertions.assertEquals(cityList.size(), citiesAmount);
    }

    @Test
    public void createTest() {
        Session currentSession = cityDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        City exCity = new City();
        String cityName = "example city";
        exCity.setName(cityName);
        exCity.setCountry(
                currentSession.createQuery("from Country order by id", Country.class)
                        .setMaxResults(1)
                        .getSingleResult()
    );
        exCity.setDistrict("example district");
        exCity.setPopulation(1);
        cityDAO.create(exCity);
        Query<City> query = currentSession.createQuery("from City c where c.name = :name", City.class);
        query.setParameter("name", cityName);
        City cityFromDB = query.getSingleResult();
        transaction.commit();
        Assertions.assertEquals(cityFromDB, exCity);
    }

    @Test
    public void updateTest() {
        Session currentSession = cityDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        Query<City> query = currentSession.createQuery("from City order by id", City.class);
        City firstCity = query.setMaxResults(1).getSingleResult();
        firstCity.setName("updated city name");
        cityDAO.update(firstCity);
        Query<City> query2 = currentSession.createQuery("from City order by id", City.class);
        City firstCityAfterUpdate = query2.setMaxResults(1).getSingleResult();
        transaction.commit();
        Assertions.assertEquals(firstCityAfterUpdate, firstCity);
    }

    @Test
    public void deleteTest() {
        Session currentSession = cityDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        Query<City> query = currentSession.createQuery("from City order by id", City.class);
        City firstCity = query.setMaxResults(1).getSingleResult();
        cityDAO.delete(firstCity);
        Query<City> query2 = currentSession.createQuery("from City order by id", City.class);
        City firstCityAfterDelete = query2.setMaxResults(1).getSingleResult();
        transaction.commit();
        Assertions.assertNotEquals(firstCityAfterDelete, firstCity);
    }

    @Test
    public void deleteByIdTest() {
        Session currentSession = cityDAO.getCurrentSession();
        Transaction transaction = currentSession.getTransaction();
        transaction.begin();
        int id = currentSession.createQuery("select min(id) from City", Integer.class).getSingleResult();
        Query<City> query = currentSession.createQuery("from City where id = :id", City.class);
        City cityById = query.setParameter("id", id)
                .setMaxResults(1)
                .getSingleResult();
        cityDAO.deleteById(id);
        Query<City> query2 = currentSession.createQuery("from City where id = :id", City.class);
        Query<City> preparedQuery2 = query2.setParameter("id", id).setMaxResults(1);
        Exception exception = Assertions.assertThrows(NoResultException.class, preparedQuery2::getSingleResult);
        transaction.commit();
        Assertions.assertEquals("No entity found for query", exception.getMessage());
    }
}