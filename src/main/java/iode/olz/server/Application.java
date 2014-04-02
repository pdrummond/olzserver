package iode.olz.server;

import javax.sql.DataSource;

import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.ds.common.BaseDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableTransactionManagement
public class Application {

	public static void main(String[] args) {
		

		SpringApplication.run(Application.class, args);        
	}    
	
	@Bean
    public DataSource dataSource() {

		BaseDataSource ds = new PGPoolingDataSource();

        try {
            ds.setDatabaseName("olz");
            ds.setUser("pdrummond");
            ds.setPassword("");
           

        } catch (Exception e) {
            System.out.print("BOOM: " + e);
        }
        return (DataSource) ds;
    }
}
