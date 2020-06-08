package eu.nighttrains.gateway.configuration;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {
	@Value("${apigateway.path.timetable}")
	private String timetablePath;

	@Value("${apigateway.path.booking}")
	private String bookingPath;

	@Bean
	public GroupedOpenApi timetableApi() {
		return GroupedOpenApi.builder()
				.pathsToMatch(this.timetablePath)
				.group("timetable")
				.build();
	}

	@Bean
	public GroupedOpenApi bookingApi() {
		return GroupedOpenApi.builder()
				.pathsToMatch(this.bookingPath)
				.group("booking")
				.build();
	}
}
