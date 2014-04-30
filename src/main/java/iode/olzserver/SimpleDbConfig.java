package iode.olzserver;

import java.net.URI;
import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SimpleDbConfig {
	private final Logger log = Logger.getLogger(getClass());

	@Bean
	public DataSource dataSource()  {    

		log.info("************* BOOM!!! **********");


		URI dbUri;
		try {
			String username = "pdrummond";
			String password = "";
			String url = "jdbc:postgresql://localhost/olz";
			String dbProperty = System.getProperty("database.url");
			if(dbProperty != null) {
				dbUri = new URI(dbProperty);

				log.info("DATABASE_URL IS " + dbUri);

				username = dbUri.getUserInfo().split(":")[0];
				password = dbUri.getUserInfo().split(":")[1];
				url = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
			} 

			BasicDataSource basicDataSource = new BasicDataSource();
			basicDataSource.setUrl(url);
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
