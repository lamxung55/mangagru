package com.mina.util;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;

public class HibernateUtil {

    private static Map<String, SessionFactory> sessionFactorys = new HashMap<String, SessionFactory>();
    private static final Logger logger = Logger.getLogger(HibernateUtil.class.getSimpleName());

    private static SessionFactory buildSessionFactory(String resource) {
        try {

            if (sessionFactorys.get(resource) == null) {

                Configuration cfg = new Configuration().configure(resource);
                sessionFactorys.put(resource, cfg.buildSessionFactory());
            }
            return sessionFactorys.get(resource);
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return buildSessionFactory("/hibernate.cfg.xml");
    }

    /**
     * @param resource: "/hibernate.cfg.xml";
     * @return
     * @author huynx6
     *
     */
    public static SessionFactory getSessionFactory(String resource) {
        if (resource == null) {
            return getSessionFactory();
        }
        return buildSessionFactory(resource);
    }

    public static Session openSession() {
        return getSessionFactory().openSession();
    }

    public static Session getCurrentSession() {
        return getSessionFactory().getCurrentSession();
    }

    public static ClassMetadata getClassMetadata(Class _class) {
        return getSessionFactory().getClassMetadata(_class);
    }

    public static void shutdown() {
        getSessionFactory().close();
    }

    public static Session openSession(String hibernateConfig) {
        return getSessionFactory(hibernateConfig).openSession();
    }

    public static void main(String[] args) {

    }

}
