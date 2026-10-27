
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

		http.authorizeHttpRequests(auth -> auth.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR)
				.permitAll()

				.requestMatchers("/WEB-INF/views/**").permitAll()

				.requestMatchers("/", "/books", "/books/**", "/shops", "/shops/**", "/auth/**", "/css/**", "/js/**",
						"/images/**", "/assets/**", "/webjars/**", "/favicon.ico", "/error")
				.permitAll()

				.requestMatchers("/admin/**").hasRole("ADMIN")

				.requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")

				.requestMatchers("/seller/**").hasAnyRole("SELLER", "ADMIN")

				.requestMatchers("/shipper/**").hasAnyRole("SHIPPER", "ADMIN")

				.requestMatchers("/buyer/**").hasAnyRole("BUYER", "SELLER", "MANAGER", "SHIPPER", "ADMIN")

				.anyRequest().authenticated());

		http.formLogin(form -> form.loginPage("/auth/login").loginProcessingUrl("/auth/login")
				.usernameParameter("email").passwordParameter("password").defaultSuccessUrl("/", true)
				.failureUrl("/auth/login?error").permitAll());

		http.logout(logout -> logout.logoutUrl("/auth/logout").logoutSuccessUrl("/auth/login?logout")
				.invalidateHttpSession(true).clearAuthentication(true).deleteCookies("JSESSIONID").permitAll());

		return http.build();
	}
}
