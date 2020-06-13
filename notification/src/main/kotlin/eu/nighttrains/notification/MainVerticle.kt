package eu.nighttrains.notification

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.kotlin.config.ConfigRetrieverOptions
import io.vertx.kotlin.config.ConfigStoreOptions
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.vertx.config.ConfigRetriever
import io.vertx.kotlin.core.DeploymentOptions

class MainVerticle : AbstractVerticle() {

	override fun start(startPromise: Promise<Void>) {

		val fileConfigurationStore = ConfigStoreOptions(
			type = "file",
			config = json {
				obj("path" to "configuration.json")
			}
		)
		val configurationRetrieverOptions = ConfigRetrieverOptions(
			stores = listOf(fileConfigurationStore)
		)
		val configurationRetriever = ConfigRetriever.create(this.vertx, configurationRetrieverOptions)
		configurationRetriever.getConfig { configurationRetrieverResult ->
			if (configurationRetrieverResult.failed()) {
				startPromise.fail(configurationRetrieverResult.cause())
			} else {
				val configuration = configurationRetrieverResult.result()

				val amqpListenerVerticle = AmqpListenerVerticle()
				val amqpListenerOptions = DeploymentOptions(
					config = configuration.getJsonObject("amqp")
				)
				this.vertx.deployVerticle(amqpListenerVerticle, amqpListenerOptions) { amqpListenerDeplyomentResult ->
					if (amqpListenerDeplyomentResult.failed()) {
						startPromise.fail(amqpListenerDeplyomentResult.cause())
					} else {
						startPromise.complete()
					}
				}
			}
		}
	}
}
