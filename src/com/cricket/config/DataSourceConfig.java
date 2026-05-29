package com.cricket.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.cricket.util.CricketUtil;

@Configuration
@PropertySource("classpath:db.properties")
@EnableTransactionManagement
public class DataSourceConfig {

    private DriverManagerDataSource dataSource;
    private LocalSessionFactoryBean sessionFactory;
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "hibernate.connection.driver_class";
    private static final String PROPERTY_NAME_DATABASE_URL = "hibernate.connection.local.url";
    private static final String PROPERTY_NAME_MEN_DATABASE_URL = "hibernate.connection.men.url";
    private static final String PROPERTY_NAME_WOMEN_DATABASE_URL = "hibernate.connection.women.url";

    @Autowired
    private Environment env;
    
    @Bean
    public DataSource dataSource() {
        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        dataSource.setUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
        
        DriverManagerDataSource men = new DriverManagerDataSource();
        men.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        men.setUrl(env.getRequiredProperty(PROPERTY_NAME_MEN_DATABASE_URL));
        
        DriverManagerDataSource women = new DriverManagerDataSource();
        women.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        women.setUrl(env.getRequiredProperty(PROPERTY_NAME_WOMEN_DATABASE_URL));
        
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("LOCAL", dataSource);
        targetDataSources.put("MEN", men);
        targetDataSources.put("WOMEN", women);

        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(dataSource);

        return routingDataSource;
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