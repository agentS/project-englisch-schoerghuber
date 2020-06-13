package eu.nighttrains.gateway.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http.csrf().disable();

		http
				.authorizeExchange()
						.pathMatchers("/booking/booking/**").authenticated()
						.and()
				.authorizeExchange()
						.anyExchange().permitAll()
						.and()
				.oauth2Login()
						.and()
				.oauth2ResourceServer()
						.jwt();
		return http.build();
	}
}
