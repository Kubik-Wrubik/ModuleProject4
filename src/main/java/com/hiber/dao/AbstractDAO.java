package com.hiber.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.List;

public class AbstractDAO<E> {
    private Class<E> clazz;
    private SessionFactory sessionFactory;

    public AbstractDAO(Class<E> clazz, SessionFactory sessionFactory) {
        this.clazz = clazz;
        this.sessionFactory = sessionFactory;
    }

    public E getById(final int id) {
        Query<E> query = getCurrentSession().createQuery("from " + clazz.getName() + " where id = :id", clazz);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    public List<E> getItems(int from, int count) {
        Query<E> query = getCurrentSession().createQuery("from " + clazz.getName() + " order by id", clazz);
        query.setFirstResult(from);
        query.setMaxResults(count);
        return query.getResultList();
    }

    public List<E> getAll() {
        return getCurrentSession().createQuery("from " + clazz.getName(), clazz).list();
    }

    public E create(final E entity) {
        getCurrentSession().saveOrUpdate(entity);
        return entity;
    }

    public E update(final E entity) {
        return (E) getCurrentSession().merge(entity);
    }

    public void delete(final E entity) {
        getCurrentSession().delete(entity);
    }

    public void deleteById(final int entityId) {
        final E entity = getById(entityId);
        delete(entity);
    }

    protected Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }

    public String getEntityName() {
        return clazz.getSimpleName();
    }
}
