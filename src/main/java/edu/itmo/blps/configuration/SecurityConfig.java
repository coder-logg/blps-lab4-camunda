package edu.itmo.blps.configuration;


//import edu.itmo.blps.filter.JwtTokenFilter;
import edu.itmo.blps.service.UserService;
import org.camunda.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
		prePostEnabled = true,
		securedEnabled = true,
		jsr250Enabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Bean
	public PasswordEncoder encoder(){
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private UserService userService;

//	@Autowired
//	private JwtTokenFilter tokenFilter;


//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		http.csrf().disable()
//				.authorizeRequests()
//				.antMatchers("/user/login", "/user/signup","/test")
//				.permitAll()
//				.and()
//				.headers().frameOptions().sameOrigin()
//				.and()
//				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//		http.antMatcher("/camunda/app/**")
//				.authorizeRequests()
//				.anyRequest()
//				.authenticated()
//				.and()
//				.httpBasic();
//		http.authorizeRequests().expressionHandler(webExpressionHandler());
//		http.addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
//	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable()
			.antMatcher("/camunda/app/**")
			.authorizeRequests().anyRequest().authenticated()
			.and()
				.httpBasic();
//			.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//			.and()
//			.expressionHandler(webExpressionHandler());;// this is just an example, use any auth mechanism you like
	}

//	@Override
//	public void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.inMemoryAuthentication()
//				.withUser("drukhary")
////                .password("{noop}$2a$12$OfqngPEHsEhMfiZz7IndOO12EvXXHj5sX0VwA1xEM4x2E.NDcmxqq") // {noop} means no password encoder
//				.password("{noop}drukhary") // {noop} means no password
//				.roles("ADMIN");
//	}

	@Bean
	public FilterRegistrationBean<ContainerBasedAuthenticationFilter> containerBasedAuthenticationFilter(){
		FilterRegistrationBean<ContainerBasedAuthenticationFilter> filterRegistration = new FilterRegistrationBean<>();
		filterRegistration.setFilter(new ContainerBasedAuthenticationFilter());
		filterRegistration.setInitParameters(Collections.singletonMap("authentication-provider", "edu.itmo.blps.filter.SpringSecurityAuthenticationProvider"));
		filterRegistration.setOrder(101);
		filterRegistration.addUrlPatterns("/camunda/app/*");
		return filterRegistration;
	}

	@Override
	public void configure(AuthenticationManagerBuilder builder) throws Exception {
		PasswordEncoder encoder = encoder();
		builder.userDetailsService(userService).passwordEncoder(encoder);
		builder.inMemoryAuthentication().withUser("admin").password(encoder.encode("admin")).roles("ADMIN");
	}

	@Bean
	public RoleHierarchy roleHierarchy() {
		RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
		hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER " +
				"ROLE_ADMIN > ROLE_CUSTOMER " +
				"ROLE_ADMIN > ROLE_COMPANY " +
				"ROLE_CUSTOMER > ROLE_USER " +
				"ROLE_COMPANY > ROLE_USER");
		return hierarchy;
	}

	private SecurityExpressionHandler<FilterInvocation> webExpressionHandler() {
		DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
		defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy());
		return defaultWebSecurityExpressionHandler;
	}

}
