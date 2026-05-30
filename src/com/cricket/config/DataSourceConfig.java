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
@EnableTransactionManagement
public class DataSourceConfig {

    private DriverManagerDataSource dataSource;

    
    @Bean
    public DataSource dataSource() {

        dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("net.ucanaccess.jdbc.UcanaccessDriver");
        dataSource.setUrl("jdbc:ucanaccess://C:/Sports/Cricket/Database/CricketTeams.mdb");

        DriverManagerDataSource men = new DriverManagerDataSource();
        men.setDriverClassName("net.ucanaccess.jdbc.UcanaccessDriver");
        men.setUrl("jdbc:ucanaccess://C:/Sports/CricketMen/Database/CricketTeams.mdb");

        DriverManagerDataSource women = new DriverManagerDataSource();
        women.setDriverClassName("net.ucanaccess.jdbc.UcanaccessDriver");
        women.setUrl("jdbc:ucanaccess://C:/Sports/CricketWomen/Database/CricketTeams.mdb");

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("LOCAL", dataSource);
        targetDataSources.put("MEN", men);
        targetDataSources.put("WOMEN", women);

        RoutingDataSource routingDataSource = new RoutingDataSource();
        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(dataSource);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }

    @Bean
    public LocalSessionFactoryBean sessionFactory() {

        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.cricket.model");
        sessionFactory.setHibernateProperties(hibernateProperties());

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