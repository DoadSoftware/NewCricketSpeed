package com.cricket.config;

import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.cricket.util.CricketUtil;

@Configuration
@EnableTransactionManagement
public class DataSourceConfig {

    private DriverManagerDataSource dataSource;
    private LocalSessionFactoryBean sessionFactory;

    @Bean
    public DataSource dataSource() {

        if (dataSource == null) {
            dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("net.ucanaccess.jdbc.UcanaccessDriver");
            dataSource.setUrl("jdbc:ucanaccess://" + CricketUtil.CRICKET_DIRECTORY 
            	+ CricketUtil.DATABASE_DIRECTORY + CricketUtil.CRICKET_TEAMS_MDB);
        }
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        if (sessionFactory == null) {
        	sessionFactory = new LocalSessionFactoryBean();
            sessionFactory.setDataSource(dataSource());
            sessionFactory.setPackagesToScan("com.cricket.model");
            sessionFactory.setHibernateProperties(hibernateProperties());
        }
        return sessionFactory;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.connection.pool_size", "5");
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.boot.allow_jdbc_metadata_access", "false");
        properties.put("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.put("hibernate.archive.autodetection", "class");
        properties.put("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect");
        return properties;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory);
        return transactionManager;
    }

    public synchronized void switchDatabase(String databasePath) {
        dataSource.setUrl("jdbc:ucanaccess://" + databasePath + CricketUtil.DATABASE_DIRECTORY + CricketUtil.CRICKET_TEAMS_MDB);
        System.out.println("Database switched to: " + databasePath + CricketUtil.DATABASE_DIRECTORY + CricketUtil.CRICKET_TEAMS_MDB);
    }
}