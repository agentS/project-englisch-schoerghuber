package eu.nighttrains.booking.service.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;

import java.time.Duration;

@TestConfiguration
public class CircuitBreakerTestConfig {
	public static final int CIRCUIT_BREAKER_TIMEOUT = 100; // milliseconds
	public static final int NON_CRITICAL_DELAY = CIRCUIT_BREAKER_TIMEOUT / 2; // milliseconds
	public static final int CRITICAL_DELAY = CIRCUIT_BREAKER_TIMEOUT * 5; // milliseconds

	@Bean
	public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
		return factory -> factory.configureDefault(identifier -> new Resilience4JConfigBuilder(identifier)
				.timeLimiterConfig(TimeLimiterConfig.custom()
						.timeoutDuration(Duration.ofMillis(CIRCUIT_BREAKER_TIMEOUT))
						.build()
				)
				.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
				.build()
		);
	}
}
