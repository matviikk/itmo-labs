package ru.itmo.web.lab3.beans;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class ApplicationStartupListener implements ServletContextListener {
    @Inject
    private MBeansConfig config;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        config.registerMBeans();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        config.unregisterMBeans();
    }
}
