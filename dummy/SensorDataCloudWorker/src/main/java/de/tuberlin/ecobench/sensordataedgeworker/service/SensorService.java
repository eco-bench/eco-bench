package de.tuberlin.ecobench.sensordataedgeworker.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.exceptions.UnirestException;

import de.tuberlin.ecobench.sensordataedgeworker.controller.SensorCloudWorkerController;
import de.tuberlin.ecobench.sensordataedgeworker.model.SensorData;

@Service
public class SensorService {

	
	private static List<SensorData> sensorDataList;
 
 	
 	
	public List<SensorData> getSensorData() {
  		return sensorDataList;
	}
	
	public void addSensorData(SensorData sd) {
 
		initSensorData();
 		SensorData newSd = new SensorData();
		newSd.setMeasurement(sd.getMeasurement());
		newSd.setSensorID("Thermometer-1");
 		sensorDataList.add(newSd);
	}

 
	
	private void initSensorData() {
		if(sensorDataList == null) {
			
		 	sensorDataList = new ArrayList<>();

		}
 		 
 	}
	
 
}
