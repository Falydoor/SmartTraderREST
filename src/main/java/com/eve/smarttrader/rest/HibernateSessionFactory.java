package com.eve.smarttrader.rest;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

public class HibernateSessionFactory {
    private static final ServiceRegistry REGISTRY;
    private static final SessionFactory FACTORY;

    static {
        Configuration configuration = new Configuration();
        configuration.addResource("hibernate.cfg.xml");
        configuration.configure();
        REGISTRY = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
        FACTORY = configuration.buildSessionFactory(REGISTRY);
    }

    public static Session getSession() {
        return FACTORY.openSession();
    }
}
