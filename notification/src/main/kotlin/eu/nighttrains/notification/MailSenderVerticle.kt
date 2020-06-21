package eu.nighttrains.notification

import eu.nighttrains.notification.dto.EmailRequest
import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.core.eventbus.Message
import io.vertx.core.json.JsonObject
import io.vertx.ext.mail.MailClient
import io.vertx.ext.mail.MailConfig
import io.vertx.ext.mail.MailMessage

class MailSenderVerticle : AbstractVerticle() {
	private val MAIL_CLIENT_POOL_NAME = "notificationMailer"
	private var mailClient: MailClient? = null

	override fun start(startFuture: Promise<Void>) {
		val mailClientConfiguration = MailConfig()
		mailClientConfiguration.hostname = this.config().getString("host")
		mailClientConfiguration.port = this.config().getInteger("port")
		mailClientConfiguration.username = this.config().getString("username")
		mailClientConfiguration.password = this.config().getString("password")
		this.mailClient = MailClient.createShared(
			this.vertx,
			mailClientConfiguration,
			MAIL_CLIENT_POOL_NAME
		)

		val eventBus = this.vertx.eventBus()
		val emailRequestConsumer = eventBus.consumer<JsonObject>(EVENT_BUS_MESSAGE_NAME_EMAIL_REQUEST)
		emailRequestConsumer.handler(this::handleEmailRequest)
		emailRequestConsumer.completionHandler { emailRequestConsumerRegistrationResult ->
			if (emailRequestConsumerRegistrationResult.failed()) {
				startFuture.fail(emailRequestConsumerRegistrationResult.cause())
			} else {
				startFuture.complete()
			}
		}
	}

	private fun handleEmailRequest(emailRequestMessage: Message<JsonObject>) {
		val message = MailMessage()
		val emailRequest = emailRequestMessage.body()
		message.from = this.config().getString("fromAddress")
		message.to = listOf(emailRequest.getString("recipientAddress"))
		message.subject = emailRequest.getString("subject")
		message.html = emailRequest.getString("text")
		this.mailClient?.sendMail(message) { mailTransmissionResult ->
			if (mailTransmissionResult.succeeded()) {
				emailRequestMessage.reply(RESPONSE_CODE_EMAIL_REQUEST_SENT)
			} else {
				println(mailTransmissionResult.cause())
				emailRequestMessage.fail(
					ERROR_CODE_EMAIL_REQUEST_FAILED,
					mailTransmissionResult.cause().message
				)
			}
		}
	}
}
