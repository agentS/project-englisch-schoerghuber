package eu.nighttrains.notification

import io.vertx.core.*
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
					this.config().getString("confirmationQueueName")
				) { confirmationQueueStartResult ->
					if (confirmationQueueStartResult.failed()) {
						startPromise.fail(confirmationQueueStartResult.cause())
					} else {
						startListeningOnQueue(
							client,
							this.config().getString("reservationQueueName")
						) { reservationQueueStartResult ->
							if (reservationQueueStartResult.failed()) {
								startPromise.fail(reservationQueueStartResult.cause())
							} else {
								startListeningOnQueue(
									client,
									this.config().getString("rejectionQueueName")
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

	fun startListeningOnQueue(
		client: RabbitMQClient,
		queueName: String,
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
							println(message.body().toJsonObject())
						}
						handler(Future.succeededFuture())
					}
				}
			}
		}
	}
}
