package iode.olzserver;

import javax.sql.DataSource;

import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.Assert;

public abstract class DatabaseConfig {

    public static final String CACHE_NAME = "cache.database";
    public static final String CACHE_TTL = "${cache.database.timetolive:60}";

    @Bean
    public abstract DataSource dataSource();

    protected void configureDataSource(org.apache.tomcat.jdbc.pool.DataSource dataSource) {
        dataSource.setMaxActive(20);
        dataSource.setMaxIdle(8);
        dataSource.setMinIdle(8);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);

        //this.migrateSchema(dataSource);
    }

    /*protected void migrateSchema(DataSource dataSource) {
        Flyway flyway = new Flyway();
        flyway.setLocations("database");
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }*/
}

@Configuration
@Profile(OlzProfiles.STANDALONE)
class StandaloneDatabaseConfig extends DatabaseConfig {

    @Bean
    public DataSource dataSource() {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost/olz");
        dataSource.setUsername("pdrummond");
        dataSource.setPassword("");
        configureDataSource(dataSource);
        return dataSource;
    }
}

@Configuration
@Profile(OlzProfiles.CLOUDFOUNDRY)
class CloudFoundryDatabaseConfig extends DatabaseConfig {

    @Bean
    public Cloud cloud() {
        return new CloudFactory().getCloud();
    }

    @Bean
    public DataSource dataSource() {
        DataSource dataSource = cloud().getServiceConnector("olz-db", DataSource.class, null);
        Assert.isInstanceOf(org.apache.tomcat.jdbc.pool.DataSource.class, dataSource);
        configureDataSource((org.apache.tomcat.jdbc.pool.DataSource) dataSource);
        return dataSource;
    }
}

@Configuration
@Profile(OlzProfiles.HEROKU)
class HerokuDatabaseConfig extends DatabaseConfig {

    @Bean
    public Cloud cloud() {
        return new CloudFactory().getCloud();
    }

    @Bean
    public DataSource dataSource() {
        DataSource dataSource = cloud().getServiceConnector("olz-db", DataSource.class, null);
        Assert.isInstanceOf(org.apache.tomcat.jdbc.pool.DataSource.class, dataSource);
        configureDataSource((org.apache.tomcat.jdbc.pool.DataSource) dataSource);
        return dataSource;
    }
}

