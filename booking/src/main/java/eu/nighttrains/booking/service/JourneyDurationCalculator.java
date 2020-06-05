package eu.nighttrains.booking.service;

import eu.nighttrains.timetable.dto.RailwayStationConnectionDto;

import java.util.List;

public final class JourneyDurationCalculator {
	private JourneyDurationCalculator() {}

	public static int calculateJourneyDurationInDays(List<RailwayStationConnectionDto> stops) {
		int durationInDays = 0;

		for (RailwayStationConnectionDto stop : stops) {
			if (stop.getDepartureTime().isAfter(stop.getArrivalTime())) {
				++durationInDays;
			}
		}
		return durationInDays;
	}
}
