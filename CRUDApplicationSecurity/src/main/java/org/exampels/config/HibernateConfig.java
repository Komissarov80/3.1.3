package org.exampels.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

    @Configuration
    @ComponentScan ("org.exampels")
    @EnableTransactionManagement
    @PropertySource(value = "classpath:db.properties")
    public class HibernateConfig {
        private Environment environment;

        @Autowired
        public void setEnvironment(Environment environment) {
            this.environment = environment;
        }

        private Properties hibernateProperties() {
            Properties properties = new Properties();
            properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
            properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
            return properties;
        }

        @Bean
        public DataSource dataSource() {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
            dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
            dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
            dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
            return dataSource;
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean sessionFactory() {
            LocalContainerEntityManagerFactoryBean sessionFactory = new LocalContainerEntityManagerFactoryBean();
            sessionFactory.setDataSource(dataSource());
            sessionFactory.setPackagesToScan("org.exampels");
            JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
            sessionFactory.setJpaVendorAdapter(vendorAdapter);
            sessionFactory.setJpaProperties(additionalProperties());        
            return sessionFactory;
        }

        private Properties additionalProperties() {
            Properties properties = new Properties();
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
            properties.setProperty("hibernate.show_sql", "true");
            //properties.setProperty("hibernate.hbm2ddl.auto", "create");
            return properties;
        }

        @Bean
        public PlatformTransactionManager transactionManager() {
            JpaTransactionManager transactionManager = new JpaTransactionManager();
            transactionManager.setEntityManagerFactory(sessionFactory().getObject());
            return transactionManager;
        }

    }

