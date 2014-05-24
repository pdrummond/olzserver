package iode.olzserver.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	DataSource dataSource;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.jdbcAuthentication()
				.dataSource(dataSource)				
				.usersByUsernameQuery("SELECT userId AS username, password, enabled FROM users WHERE userId=?")               
				.authoritiesByUsernameQuery("SELECT userId AS username, authority FROM authorities WHERE userId = ?")                
				.rolePrefix("ROLE_");
	}
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http
		.addFilterAfter(new CsrfTokenGeneratorFilter(), CsrfFilter.class)
        .authorizeRequests()
            .anyRequest().authenticated()
            .and()
        .formLogin() 
            .permitAll();  
    }

	
	/*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterAfter(new CsrfTokenGeneratorFilter(), CsrfFilter.class)
                .authorizeRequests()
                    .antMatchers("/public/**").permitAll()
                    .antMatchers("/**").authenticated()
                    .and()
                .formLogin()
                    .loginPage("/login").permitAll();
    }*/
		
	//@Configuration
	//@EnableWebMvcSecurity
	//public class WebSecurityConfig { //extends WebSecurityConfigurerAdapter {

	/*@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/home").permitAll()
                .anyRequest().authenticated();
        http
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .and(
            .logout()
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .inMemoryAuthentication()
                .withUser("user").password("password").roles("USER");
    }*/
		
}