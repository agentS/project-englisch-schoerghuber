package eu.nighttrains.booking.service.rest;

import eu.nighttrains.booking.service.NoConnectionsAvailableException;
import feign.FeignException;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeTableRestClientFeignAdvice {
	@AfterThrowing(pointcut = "execution(public * eu.nighttrains.booking.service.rest.TimeTableRestClientFeign.getRailwayConnections(..))", throwing = "exception")
	public void translateNotFoundExceptionRailwayConnectionLookup(FeignException.NotFound exception) throws Throwable {
		throw new NoConnectionsAvailableException();
	}
}
