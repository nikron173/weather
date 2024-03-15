package com.nikron.weather.listener;

import com.nikron.weather.util.BuildEntityManagerUtil;
import com.nikron.weather.util.DbMigration;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

@WebListener
@Slf4j
public class InitConfig implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        DbMigration.dbMigration();
        BuildEntityManagerUtil.initEntityManagerFactory();
        log.info("EntityManagerFactory open");
        ServletContextListener.super.contextInitialized(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        log.info("EntityManagerFactory isOpen - {}", BuildEntityManagerUtil.getEntityManagerFactory().isOpen());
        if (BuildEntityManagerUtil.getEntityManagerFactory().isOpen()) {
            BuildEntityManagerUtil.getEntityManagerFactory().close();
            log.info("EntityManagerFactory close");
        }
        ServletContextListener.super.contextDestroyed(sce);
    }
}
