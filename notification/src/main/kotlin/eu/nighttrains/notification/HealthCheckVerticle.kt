package eu.nighttrains.notification

import io.vertx.core.AbstractVerticle
import io.vertx.core.Promise
import io.vertx.ext.healthchecks.HealthCheckHandler
import io.vertx.ext.web.Router

class HealthCheckVerticle : AbstractVerticle() {
	override fun start(startPromise: Promise<Void>) {
		val server = this.vertx.createHttpServer()
		val navigator = Router.router(this.vertx)
		val healthCheckHandler = HealthCheckHandler.create(this.vertx)
		healthCheckHandler.register("readiness") { future ->
			if (this.vertx.deploymentIDs().size == NUMBER_OF_VERTICLES) {
				future.complete()
			} else {
				future.fail("Deployment not yet finished")
			}
		}
		healthCheckHandler.register("liveness") { future ->
			future.complete()
		}
		navigator.get("/health*").handler(healthCheckHandler)
		server
			.requestHandler(navigator)
			.listen(this.config().getInteger("port"))

		startPromise.complete()
	}
}
