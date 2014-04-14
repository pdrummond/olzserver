package iode.olzserver;

import static iode.olzserver.OlzProfiles.CLOUDFOUNDRY;
import static iode.olzserver.OlzProfiles.PRODUCTION;
import static iode.olzserver.OlzProfiles.STAGING;
import static iode.olzserver.OlzProfiles.STANDALONE;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableTransactionManagement
public class Application extends SpringApplication {

    private static final Log logger = LogFactory.getLog(Application.class);
    
    public Application() {
    	super();
    }

    public Application(Class<?> configClass) {
        super(configClass);
    }

    /**
     * Enforce mutual exclusivity and implicit activation of profiles as described in
     * {@link OlzProfiles}.
     */
    @Override
    protected void configureProfiles(ConfigurableEnvironment environment, String[] args) {
        super.configureProfiles(environment, args);

        boolean standaloneActive = environment.acceptsProfiles(STANDALONE);
        boolean stagingActive = environment.acceptsProfiles(STAGING);
        boolean productionActive = environment.acceptsProfiles(PRODUCTION);

        if (stagingActive && productionActive) {
            throw new IllegalStateException(String.format("Only one of the following profiles may be specified: [%s]",
                    StringUtils.arrayToCommaDelimitedString(new String[] { STAGING, PRODUCTION })));
        }

        if (stagingActive || productionActive) {
            logger.info(String.format("Activating '%s' profile because one of '%s' or '%s' profiles have been specified.",
                    CLOUDFOUNDRY, STAGING, PRODUCTION));
            environment.addActiveProfile(CLOUDFOUNDRY);
        }
        else if (standaloneActive) {
            logger.info("The default 'standalone' profile is active because no other profiles have been specified.");
        }
        else {
            throw new IllegalStateException(String.format("Unknown profile(s) specified: [%s]. Valid profiles are: [%s]",
                    StringUtils.arrayToCommaDelimitedString(environment.getActiveProfiles()),
                    StringUtils.arrayToCommaDelimitedString(new String[] {
                    		StringUtils.arrayToCommaDelimitedString(environment.getDefaultProfiles()), STAGING, PRODUCTION })));
        }
    }
    
    public static void main(String[] args) {
		Application.run(Application.class, args);        
	}
}