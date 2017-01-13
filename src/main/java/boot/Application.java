package boot;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.boot.builder.*;
import org.springframework.boot.context.web.*;

import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.servlet.config.annotation.*;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.*;

import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;

import org.springframework.security.cas.*;
import org.springframework.security.cas.web.*;
import org.springframework.security.cas.authentication.*;

import org.jasig.cas.client.validation.*;

import org.slf4j.*;

import java.util.*;
import java.util.stream.*;
import javax.annotation.*;

import org.h2.server.web.WebServlet;
import org.springframework.boot.context.embedded.ServletRegistrationBean;

//@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class Application extends SpringBootServletInitializer {

	// externalize application configuration
	// TODO: put application.properties under resources/{appName}/ in source code
	// TODO: put application.properties under lib/{appName}/ in tomcat
	static Properties getProperties() {
		Properties props = new Properties();
		props.put("spring.config.location", "classpath:boot/");
		return props;
	}

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	// security settings, review all TODOs
	@Configuration
	@EnableWebSecurity
	public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		public class CustomizedUserDetailsService 
		    implements AuthenticationUserDetailsService, UserDetailsService {

	        // form authentication via /login and login.html
	        @Override
	        public UserDetails loadUserByUsername(String username) 
	            throws UsernameNotFoundException {
	            List<GrantedAuthority> authorities = new ArrayList<>();

	            // TODO: load role, optionally, password from DB
	            // TODO: role used for access control
	            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
	            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

	            // TODO: password used ONLY for form authentication
	            String password = username;
	            return new User(username, password, authorities);
	        }

	        // CAS authentication
	        @Override
	        public UserDetails loadUserDetails(Authentication token) 
	            throws UsernameNotFoundException {
	            return loadUserByUsername(token.getPrincipal()+"");
	        }
	    }

	    @Override
	    protected void configure(HttpSecurity http) throws Exception {
	        // TODO: setup access control via role
	        if (!application_security) {
	        	log.warn("***** No Access Control *****");
	        	http.authorizeRequests()
	            	.antMatchers("/**").permitAll();
	        } else {
		        http.authorizeRequests()
		            .antMatchers("/", "/index.html", "/bye.html").permitAll()
		            .antMatchers("/task/**").hasRole("ADMIN")
		            .anyRequest().authenticated();
	        }

	        // TODO: optional form based authentication
	        http.formLogin().loginPage("/login").permitAll();

	        // TODO: optional CAS Authentication
	        if (!("NULL".equals(cas_server_url))) {
	        	// filter to check authenticaton
	            http.addFilter(casAuthenticationFilter());
	            // where redirect to login page
	            http.exceptionHandling().authenticationEntryPoint(
	            	casAuthenticationEntryPoint());        
	        }

	    	// TODO: additional in template or Ajax js
	        http.csrf().disable();

	        // TODO: optional disable xframe for h2 console
	        http.headers().frameOptions().disable();

	        // Setup logout
	        http.logout().logoutSuccessUrl("/bye.html").permitAll();
	    }

	    // ******** Usually no need to change code below

	    @Value("${application.security:true}")
	    private boolean application_security;

	    // TODO: define CAS propeeties in application.properties	    
	    @Value("${cas.server-url:NULL}")
	    private String cas_server_url;
	    @Value("${cas.server-login-url:NULL}")
	    private String cas_server_login_url;
	    @Value("${cas.client-url:NULL}")
	    private String cas_client_url;

	    @Autowired
	    public void configureGlobal(AuthenticationManagerBuilder auth) 
	    throws Exception {
	        CustomizedUserDetailsService userDeatailService = 
	            new CustomizedUserDetailsService();

	        // Optional if form based login used    
	        auth.userDetailsService(userDeatailService);

	        // Optional - if CAS authentication used
	        if (!("NULL".equals(cas_server_url))) {
	            CasAuthenticationProvider casAuthenticationProvider = 
	            casAuthenticationProvider();
	            casAuthenticationProvider.setAuthenticationUserDetailsService(
	                userDeatailService);
	            auth.authenticationProvider(casAuthenticationProvider);
	        }
	    }

	    public ServiceProperties serviceProperties() {
	        ServiceProperties serviceProperties = new ServiceProperties();
	        serviceProperties.setService(cas_client_url);
	        serviceProperties.setSendRenew(false);
	        return serviceProperties;
	    }

	    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
	        CasAuthenticationEntryPoint casAuthenticationEntryPoint = 
	            new CasAuthenticationEntryPoint();
	        casAuthenticationEntryPoint.setLoginUrl(
	        	cas_server_login_url);  
	        casAuthenticationEntryPoint.setServiceProperties(
	        	serviceProperties());
	        return casAuthenticationEntryPoint;
	    }

	    public CasAuthenticationProvider casAuthenticationProvider() {
	        CasAuthenticationProvider casAuthenticationProvider = 
	            new CasAuthenticationProvider();
	        casAuthenticationProvider.setServiceProperties(serviceProperties());

	        // TODO should allow this to be configed for SAML or CAS30
	        casAuthenticationProvider.setTicketValidator(
	            new Cas20ServiceTicketValidator(cas_server_url));
	        
	        casAuthenticationProvider.setKey("cas_auth_provider");
	        return casAuthenticationProvider;
	    }

	    public CasAuthenticationFilter casAuthenticationFilter() 
	    throws Exception {
	        CasAuthenticationFilter casAuthenticationFilter = 
	        	new CasAuthenticationFilter();
	        casAuthenticationFilter.setAuthenticationManager(
	        	authenticationManager());
	        return casAuthenticationFilter;
	    }
	} 
	
    @Configuration
    public class MvcConfig extends WebMvcConfigurerAdapter {
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            // choose login form as templates/login.html
            registry.addViewController("/login").setViewName("login");
        }
    }

    // TODO disable H2 web console 
	@Configuration
	public class H2ConsoleConfiguration {
	    @Bean
	    ServletRegistrationBean h2servletRegistration(){
	        ServletRegistrationBean registrationBean = new ServletRegistrationBean( new WebServlet());
	        registrationBean.addUrlMappings("/console/*");
	        return registrationBean;
	    }
	}

    @Override
    protected SpringApplicationBuilder configure(
    SpringApplicationBuilder springApplicationBuilder) {
		return springApplicationBuilder
			.sources(Application.class)
			.properties(getProperties());
    }

    public static void main(String[] args) {
		new SpringApplicationBuilder(Application.class)
			.sources(Application.class)
			.properties(getProperties())
			.build()
			.run(args);
    }    
}
