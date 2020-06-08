package eu.nighttrains.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Value("${apigateway.gateway.host}")
	private String gatewayHost;

	@Value("${apigateway.path.timetable}")
	private String timetablePath;

	@Value("${apigateway.path.timetable.prefixlength}")
	private int timetablePathPrefixLength = 1;

	@Value("${apigateway.path.booking}")
	private String bookingPath;

	@Value("${apigateway.path.booking.prefixlength}")
	private int bookingPathPrefixLength = 1;

	@Value("${apigateway.uri.timetable}")
	private String timetableUri;

	@Value("${apigateway.uri.booking}")
	private String bookingUri;

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route(
						"timetable",
						route -> route.host(this.gatewayHost)
								.and()
								.path(this.timetablePath)
								.filters(filter -> filter
									.stripPrefix(this.timetablePathPrefixLength)
									.circuitBreaker(circuitBreaker -> circuitBreaker.setName("timetableCircuitBreaker"))
								)
								.uri(this.timetableUri)
				)
				.route(
						"booking",
						route -> route.host(this.gatewayHost)
								.and()
								.path(this.bookingPath)
								.filters(filter -> filter
										.stripPrefix(this.bookingPathPrefixLength)
										.circuitBreaker(circuitBreaker -> circuitBreaker.setName("bookingCircuitBreaker"))
								)
								.uri(this.bookingUri)
				)
				.build();
	}
}
