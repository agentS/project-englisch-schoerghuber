package eu.nighttrains.notification

import eu.nighttrains.notification.dto.EmailRequest
import io.vertx.core.*
import io.vertx.core.json.JsonObject
import io.vertx.rabbitmq.RabbitMQClient
import io.vertx.rabbitmq.RabbitMQOptions

class AmqpListenerVerticle : AbstractVerticle() {
	override fun start(startPromise: Promise<Void>) {
		val amqpConfiguration = RabbitMQOptions()
		amqpConfiguration.host = this.config().getString("host")
		amqpConfiguration.port = this.config().getInteger("port")
		amqpConfiguration.user = this.config().getString("username")
		amqpConfiguration.password = this.config().getString("password")

		val client = RabbitMQClient.create(this.vertx, amqpConfiguration)
		client.start { clientStartResult ->
			if (clientStartResult.failed()) {
				startPromise.fail(clientStartResult.cause())
			} else {
				startListeningOnQueue(
					client,
					this.config().getString("confirmationQueueName"),
					this::convertConfirmationMessageToEmailRequest
				) { confirmationQueueStartResult ->
					if (confirmationQueueStartResult.failed()) {
						startPromise.fail(confirmationQueueStartResult.cause())
					} else {
						startListeningOnQueue(
							client,
							this.config().getString("reservationQueueName"),
							this::convertReservationMessageToEmailRequest
						) { reservationQueueStartResult ->
							if (reservationQueueStartResult.failed()) {
								startPromise.fail(reservationQueueStartResult.cause())
							} else {
								startListeningOnQueue(
									client,
									this.config().getString("rejectionQueueName"),
									this::convertRejectionMessageToEmailRequest
								) { rejectionQueueStartResult ->
									if (rejectionQueueStartResult.failed()) {
										startPromise.fail(rejectionQueueStartResult.cause())
									} else {
										startPromise.complete()
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private fun startListeningOnQueue(
		client: RabbitMQClient,
		queueName: String,
		convertAmqpMessageToEmailRequest: (JsonObject) -> EmailRequest,
		handler: (AsyncResult<Void>) -> Unit
	) {
		client.queueDeclare(
			queueName,
			true, false, false
		) { queueDeclarationResult ->
			if (queueDeclarationResult.failed()) {
				handler(Future.failedFuture(queueDeclarationResult.cause()))
			} else {
				client.basicConsumer(queueName) { confirmationMessageConsumerStartResult ->
					if (confirmationMessageConsumerStartResult.failed()) {
						handler(Future.failedFuture(confirmationMessageConsumerStartResult.cause()))
					} else {
						val confirmationMessageConsumer = confirmationMessageConsumerStartResult.result()
						confirmationMessageConsumer.handler { message ->
							val eventBus = this.vertx.eventBus()
							val jsonMessage = message.body().toJsonObject()
							eventBus.send(
								EVENT_BUS_MESSAGE_NAME_EMAIL_REQUEST,
								JsonObject.mapFrom(convertAmqpMessageToEmailRequest(jsonMessage))
							)
						}
						handler(Future.succeededFuture())
					}
				}
			}
		}
	}

	private fun convertConfirmationMessageToEmailRequest(amqpMessage: JsonObject): EmailRequest =
		EmailRequest(
			amqpMessage.getString("emailAddress"),
			"Confirmation of your booking ${amqpMessage.getString("id")}",
			"Your booking from ${amqpMessage.getString("id")} has been confirmed."
		)

	private fun convertReservationMessageToEmailRequest(amqpMessage: JsonObject): EmailRequest =
		EmailRequest(
			amqpMessage.getString("emailAddress"),
			"Reservation of your booking ${amqpMessage.getString("id")} pending",
			"Unfortunately we are having troubles on our side processing your request and therefore your booking ${amqpMessage.getString("id")} can not be confirmed yet. We will notify you as soon as we can confirm the booking."
		)

	private fun convertRejectionMessageToEmailRequest(amqpMessage: JsonObject): EmailRequest =
		EmailRequest(
			amqpMessage.getString("emailAddress"),
			"Rejection of your booking ${amqpMessage.getString("id")}",
			"Unfortunately one ore more trains for your reservation ${amqpMessage.getString("id")} are overbooked. Thus we have to reject your booking request. We apologize for the inconvenience."
		)
}
