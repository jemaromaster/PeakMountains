package polimi.awt.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
@ComponentScan("polimi.awt.security")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private static String REALM="MY_TEST_REALM";

	@Autowired
	private MyUserDetailsService userDetailsService;

	/*@Autowired
	public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("bill").password("abc123").roles("ADMIN");
		auth.inMemoryAuthentication().withUser("tom").password("abc123").roles("USER");
	}*/
	
	/*@Override
	protected void configure(HttpSecurity http) throws Exception {
 
	  http
	  	.authorizeRequests()
	  	.antMatchers("/**").hasRole("ADMIN")
		.and().httpBasic().realmName(REALM).authenticationEntryPoint(getBasicAuthEntryPoint());
 	}*/

	@Override
	protected void configure(final HttpSecurity http) throws Exception {


		http.csrf().disable()
				.authorizeRequests()
				.antMatchers("/").permitAll()
//				.antMatchers(HttpMethod.GET, "/users/**").permitAll()
//				.antMatchers(HttpMethod.GET, "/campaigns/**").permitAll()
//				.antMatchers(HttpMethod.GET, "/ministerio/**").permitAll()
//				.antMatchers(HttpMethod.GET, "/sas/pagos/tree/**").permitAll()
//				.antMatchers(HttpMethod.GET, "/sas/beneficiarios/tree/**").permitAll()
//				.antMatchers(HttpMethod.GET, "/periodoSAS/pagos/").permitAll()
//				.antMatchers(HttpMethod.GET, "/periodoSAS/beneficiarios/").permitAll()
//				.antMatchers(HttpMethod.GET, "/lcc/").permitAll()
//				.antMatchers(HttpMethod.GET, "/hacienda/").permitAll()
//				.antMatchers("/user").hasAuthority("admin")
//				.antMatchers("/user/**").hasAuthority("admin")
//				.antMatchers("/sas/**").hasAuthority("sas")
				.antMatchers(HttpMethod.POST, "/users/login").permitAll()
//				.anyRequest().authenticated()
				.and().httpBasic().authenticationEntryPoint(getBasicAuthEntryPoint())
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);//We don't need sessions to be created.

	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		//authProvider.setPasswordEncoder(encoder());
		return authProvider;
	}

	/*@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(11);
	}*/

	@Bean
	public CustomBasicAuthenticationEntryPoint getBasicAuthEntryPoint(){
		return new CustomBasicAuthenticationEntryPoint();
	}

	/*@Bean
	public CustomBasicAuthenticationEntryPoint getBasicAuthEntryPoint(){
		return new CustomBasicAuthenticationEntryPoint();
	}*/
	
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }
}
