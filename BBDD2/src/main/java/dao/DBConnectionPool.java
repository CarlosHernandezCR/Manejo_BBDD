package dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import common.config.ConfigurationXML;
import common.constants.CommonConstants;
import jakarta.annotation.PreDestroy;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class DBConnectionPool {

    private ConfigurationXML config;
    private DataSource hikariDataSource;

    @Inject
    public DBConnectionPool(ConfigurationXML config) {
        this.config = config;
        hikariDataSource = getHikariPool();
    }

    private DataSource getHikariPool() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getProperty(CommonConstants.DATABASE));
        hikariConfig.setUsername(config.getProperty(CommonConstants.USER));
        hikariConfig.setPassword(config.getProperty(CommonConstants.PASSWORD));
        hikariConfig.setDriverClassName(config.getProperty(CommonConstants.DRIVER));
        hikariConfig.setMaximumPoolSize(1);

        hikariConfig.addDataSourceProperty(CommonConstants.HIKARI_CACHE_PREP_STMTS, true);
        hikariConfig.addDataSourceProperty(CommonConstants.HIKARI_PREP_STMT_CACHE_SIZE_PROP, CommonConstants.HIKARI_PREP_STMT_CACHE_SIZE);
        hikariConfig.addDataSourceProperty(CommonConstants.HIKARI_PREP_STMT_CACHE_SQL_LIMIT_PROP, CommonConstants.HIKARI_PREP_STMT_CACHE_SQL_LIMIT);

        return new HikariDataSource(hikariConfig);
    }

    @PreDestroy
    public void closePool() {
        ((HikariDataSource) hikariDataSource).close();
    }

}