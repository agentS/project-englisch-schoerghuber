package eu.nighttrains.timetable.health;

import eu.nighttrains.timetable.businesslogic.NoRouteException;
import eu.nighttrains.timetable.businesslogic.RouteManager;
import eu.nighttrains.timetable.dto.RailwayStationConnectionDto;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
@Liveness
public class ConnectionLookupHealthCheck implements HealthCheck {
	private static final long RAILWAY_STATION_ID_VIENNA_CENTRAL_STATION = 0L;
	private static final long RAILWAY_STATION_ID_ZURICH_CENTRAL_STATION = 14L;

	private static final String HEALTH_CHECK_NAME = "Connection lookup health check";

	private final RouteManager routeManager;

	@Inject
	public ConnectionLookupHealthCheck(RouteManager routeManager) {
		this.routeManager = routeManager;
	}

	@Override
	public HealthCheckResponse call() {
		try {
			List<RailwayStationConnectionDto> connections = this.routeManager.findAllStopsBetween(
					RAILWAY_STATION_ID_VIENNA_CENTRAL_STATION,
					RAILWAY_STATION_ID_ZURICH_CENTRAL_STATION
			);
			if (connections.size() > 0) {
				return HealthCheckResponse.up(HEALTH_CHECK_NAME);
			}
		} catch (NoRouteException exception) {
			exception.printStackTrace();
		}
		return HealthCheckResponse.down(HEALTH_CHECK_NAME);
	}
}
