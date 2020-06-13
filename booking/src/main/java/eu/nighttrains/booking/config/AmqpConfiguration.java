package eu.nighttrains.booking.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AmqpConfiguration {
	private final String exchangeName;
	private final String confirmationQueueName;
	private final String reservationQueueName;
	private final String rejectionQueueName;
	private final String routingKeyConfirmed;
	private final String routingKeyReserved;
	private final String routingKeyRejected;

	public AmqpConfiguration(
			@Value("${bookingstatusupdates.exchange-name}") String exchangeName,
			@Value("${bookingstatusupdates.queue.confirmation}") String confirmationQueueName,
			@Value("${bookingstatusupdates.queue.reservation}") String reservationQueueName,
			@Value("${bookingstatusupdates.queue.rejection}") String rejectionQueueName,
			@Value("${bookingstatusupdates.routingkey.confirmed}") String routingKeyConfirmed,
			@Value("${bookingstatusupdates.routingkey.reserved}") String routingKeyReserved,
			@Value("${bookingstatusupdates.routingkey.rejected}") String routingKeyRejected
	) {
		this.exchangeName = exchangeName;
		this.confirmationQueueName = confirmationQueueName;
		this.reservationQueueName = reservationQueueName;
		this.rejectionQueueName = rejectionQueueName;
		this.routingKeyConfirmed = routingKeyConfirmed;
		this.routingKeyReserved = routingKeyReserved;
		this.routingKeyRejected = routingKeyRejected;
	}

	@Bean
	public Queue confirmationQueue() {
		return new Queue(this.confirmationQueueName, true, false, false);
	}

	@Bean
	public Queue reservationQueue() {
		return new Queue(this.reservationQueueName, true, false, false);
	}

	@Bean
	public Queue rejectionQueue() {
		return new Queue(this.rejectionQueueName, true, false, false);
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(this.exchangeName, true, false);
	}

	@Bean
	public Binding bindingConfirmation(Queue confirmationQueue, DirectExchange exchange) {
		return BindingBuilder.bind(confirmationQueue).to(exchange).with(this.routingKeyConfirmed);
	}

	@Bean
	public Binding bindingReservation(Queue reservationQueue, DirectExchange exchange) {
		return BindingBuilder.bind(reservationQueue).to(exchange).with(this.routingKeyReserved);
	}

	@Bean
	public Binding bindingRejection(Queue rejectionQueue, DirectExchange exchange) {
		return BindingBuilder.bind(rejectionQueue).to(exchange).with(this.routingKeyRejected);
	}
}
