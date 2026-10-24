
package vn.ubm.usedbookmarketplace.config;

import jakarta.servlet.DispatcherType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.authorizeHttpRequests(auth -> auth

				// Allow internal forwards and error dispatches
				.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()

				// Allow JSP views to be rendered internally
				.requestMatchers("/WEB-INF/views/**").permitAll()

				// Public pages and authentication endpoints
				.requestMatchers("/", "/books", "/books/**", "/shops", "/auth/**", "/css/**", "/js/**", "/images/**",
						"/assets/**", "/webjars/**", "/favicon.ico", "/error")
				.permitAll()

				// ADMIN access
				.requestMatchers("/admin/**").hasRole("ADMIN")

				// MANAGER and ADMIN access
				.requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")

				// SELLER and ADMIN access
				.requestMatchers("/seller/**").hasAnyRole("SELLER", "ADMIN")

				// SHIPPER and ADMIN access
				.requestMatchers("/shipper/**").hasAnyRole("SHIPPER", "ADMIN")

				// Authenticated marketplace users
				.requestMatchers("/buyer/**").hasAnyRole("BUYER", "SELLER", "MANAGER", "SHIPPER", "ADMIN")

				// All other endpoints require authentication
				.anyRequest().authenticated())

				// Form login
				.formLogin(form -> form.loginPage("/auth/login").loginProcessingUrl("/auth/login")
						.usernameParameter("email").passwordParameter("password").defaultSuccessUrl("/", true)
						.failureUrl("/auth/login?error").permitAll())

				// Logout
				.logout(logout -> logout.logoutUrl("/auth/logout").logoutSuccessUrl("/auth/login?logout")
						.invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID").permitAll());

		// Keep CSRF protection enabled for form submissions.
		return http.build();
	}
}
