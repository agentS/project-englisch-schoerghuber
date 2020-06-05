package eu.nighttrains.booking.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import eu.nighttrains.timetable.dto.RailwayStationConnectionDto;
import eu.nighttrains.timetable.dto.TrainCarDto;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public final class TimeTableMockJsonDataMapper {
	private static final String MOCK_DATA_ROOT_PATH = "/test/mockData/";

	private TimeTableMockJsonDataMapper() {}

	public static List<RailwayStationConnectionDto> readRailwayStationConnections(
			final long departureStationId,
			final long arrivalStationId
	) {
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.registerModule(new JavaTimeModule());
		try {
			File mockDataFile = new File(
					TimeTableMockJsonDataMapper.class.getResource(String.format("%s/connections/connectionsFrom%dTo%d.json", MOCK_DATA_ROOT_PATH, departureStationId, arrivalStationId)).getFile()
			);
			var railwayStationConnections = jsonMapper.readValue(
					mockDataFile,
					RailwayStationConnectionDto[].class
			);
			return Arrays.asList(railwayStationConnections);
		} catch (JsonProcessingException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public static List<TrainCarDto> readTrainCarsForConnection(long trainConnectionId) {
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.registerModule(new JavaTimeModule());
		try {
			File mockDataFile = new File(
					TimeTableMockJsonDataMapper.class.getResource(String.format("%s/trainCars/trainCarsOfConnection%d.json", MOCK_DATA_ROOT_PATH, trainConnectionId)).getFile()
			);
			var railwayStationConnections = jsonMapper.readValue(
					mockDataFile,
					TrainCarDto[].class
			);
			return Arrays.asList(railwayStationConnections);
		} catch (JsonProcessingException exception) {
			exception.printStackTrace();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
		return null;
	}
}
