package eu.nighttrains.booking.service.config;

import com.github.fridujo.rabbitmq.mock.MockConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class RabbitMqMockConfig {
	@Bean
	public ConnectionFactory connectionFactory() {
		return new CachingConnectionFactory(new MockConnectionFactory());
	}
}
