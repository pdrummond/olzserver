package iode.olzserver;

import java.net.URI;
import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
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
	private final Logger log = Logger.getLogger(getClass());

	@Bean
	public Cloud cloud() {
		return new CloudFactory().getCloud();
	}

	@Bean
	public DataSource dataSource()  {    
		
		log.info("************* CALLING HEROKU PROFILE **********");
		

		URI dbUri;
		try {
			dbUri = new URI(System.getenv("DATABASE_URL"));
			
			log.info("DATABASE_URL IS " + dbUri);

			String username = dbUri.getUserInfo().split(":")[0];
			String password = dbUri.getUserInfo().split(":")[1];
			String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

			BasicDataSource basicDataSource = new BasicDataSource();
			basicDataSource.setUrl(dbUrl);
			basicDataSource.setUsername(username);
			basicDataSource.setPassword(password);

			return basicDataSource;

		} catch (URISyntaxException e) {
			log.error("Exception getting datasource", e);
			return null;
		}

		/*DataSource dataSource = cloud().getServiceConnector("olz-db", DataSource.class, null);
        Assert.isInstanceOf(org.apache.tomcat.jdbc.pool.DataSource.class, dataSource);
        configureDataSource((org.apache.tomcat.jdbc.pool.DataSource) dataSource);
        return dataSource;*/
	}
}

