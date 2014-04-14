package iode.olzserver.config;

import java.net.URISyntaxException;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;

//@Configuration
public class DbConfig {
	//private final Logger log = Logger.getLogger(getClass());

    @Bean
    public DataSource dataSource() throws URISyntaxException {
    	/*String databaseUrl = System.getProperty("database.url");
    	if(databaseUrl == null) {
    		throw new RuntimeException("database.url is not defined");
    	}
        if(log.isDebugEnabled()) {
        	log.debug("*** DATABASE URL: " + databaseUrl);
        }
    	
        URI dbUri = new URI(databaseUrl);
        
        String userInfo = dbUri.getUserInfo();

        String username = "pdrummond";
        String password = "";
        String host = "localhost";
        int port = 5432;
        String path = "/olz";
        
        if(userInfo != null) {
        	username = userInfo.split(":")[0];
        	password = userInfo.split(":")[1];
        }
        if(dbUri.getHost() != null) {
        	host = dbUri.getHost();
        }
        if(dbUri.getPort() != -1) {
        	port = dbUri.getPort();
        }
        if(dbUri.getPath() != null) {
        	path = dbUri.getPath();
        }
        //String dbUrl = "jdbc:postgresql://" + host + ':' + port + path;
        if(log.isDebugEnabled()) {
        	log.debug("dbUrl= " + databaseUrl);
        }
        BaseDataSource ds = new PGPoolingDataSource();

        try {
            ds.setDatabaseName(path.replace("/", ""));
            ds.setUser(username);
            ds.setPassword(password);
        } catch (Exception e) {
            System.out.print("BOOM: " + e);
        }
        return (DataSource) ds;
        DriverManagerDataSource basicDataSource = new DriverManagerDataSource();
        basicDataSource.setUrl(dbUrl);
        basicDataSource.setUsername(username);
        basicDataSource.setPassword(password);

        return basicDataSource;*/
    	return null;
    }
}