package eu.nighttrains.gateway.configuration;

import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SwaggerUiConfigProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfiguration {
	@Value("${apigateway.path.timetable}")
	private String timetablePath;

	@Value("${apigateway.path.booking}")
	private String bookingPath;

	@Bean
	public List<GroupedOpenApi> apis(SwaggerUiConfigProperties swaggerUiConfigProperties, RouteDefinitionLocator routeDefinitionLocator) {
		List<GroupedOpenApi> groups = new ArrayList<>();
		List<RouteDefinition> definitions = routeDefinitionLocator
				.getRouteDefinitions()
				.collectList()
				.block();
		definitions.stream()
				.filter(routeDefinition ->
						routeDefinition.getId().equals("booking")
						|| routeDefinition.getId().equals("timetable")
				)
				.forEach(routeDefinition -> {
					swaggerUiConfigProperties.addGroup(routeDefinition.getId());
					groups.add(GroupedOpenApi.builder()
						.pathsToMatch("/" + routeDefinition.getId() + "/**")
						.group(routeDefinition.getId())
						.build()
					);
				});
		return groups;
	}
}
