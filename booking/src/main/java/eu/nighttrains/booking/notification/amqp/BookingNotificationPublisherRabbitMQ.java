package eu.nighttrains.booking.notification.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.nighttrains.booking.model.Booking;
import eu.nighttrains.booking.model.BookingStatus;
import eu.nighttrains.booking.notification.BookingNotificationPublisher;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BookingNotificationPublisherRabbitMQ implements BookingNotificationPublisher {
	private final RabbitTemplate rabbitTemplate;

	private final String exchangeName;
	private final String routingKeyConfirmed;
	private final String routingKeyReserved;
	private final String routingKeyRejected;

	private final Jackson2JsonMessageConverter amqpMessageConverter;

	public BookingNotificationPublisherRabbitMQ(
			@Autowired RabbitTemplate rabbitTemplate,
			@Value("${bookingstatusupdates.exchange-name}") String exchangeName,
			@Value("${bookingstatusupdates.routingkey.confirmed}") String routingKeyConfirmed,
			@Value("${bookingstatusupdates.routingkey.reserved}") String routingKeyReserved,
			@Value("${bookingstatusupdates.routingkey.rejected}") String routingKeyRejected
	) {

		this.rabbitTemplate = rabbitTemplate;
		this.exchangeName = exchangeName;
		this.routingKeyConfirmed = routingKeyConfirmed;
		this.routingKeyReserved = routingKeyReserved;
		this.routingKeyRejected = routingKeyRejected;

		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.registerModule(new JavaTimeModule());
		this.amqpMessageConverter = new Jackson2JsonMessageConverter(jsonMapper);
	}

	@Override
	public void publishBookingStatusUpdate(Booking booking) {
		this.rabbitTemplate.convertAndSend(
				this.exchangeName,
				this.lookupRoutingKeyForStatus(booking.getStatus()),
				this.serializeBooking(booking)
		);
	}

	private Message serializeBooking(Booking booking) {
		return this.amqpMessageConverter.toMessage(
				booking,
				new MessageProperties()
		);
	}

	private String lookupRoutingKeyForStatus(BookingStatus status) {
		switch (status) {
			case CONFIRMED:
				return this.routingKeyConfirmed;
			case RESERVED:
				return this.routingKeyReserved;
			case REJECTED:
				return this.routingKeyRejected;
			default:
				throw new IllegalArgumentException("Could not lookup routing key for booking status " + status);
		}
	}
}
