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

import de.tuberlin.ecobench.sensordataedgeworker.controller.SensorEdgeWorkerController;
import de.tuberlin.ecobench.sensordataedgeworker.model.SensorData;

@Service
public class SensorService {

	
	private static List<SensorData> sensorDataList;
	
 	@Value("${sensorEdgeWorker.aggregationintervall}")
	private static int intervall;
 	
	public List<SensorData> getSensorData() {
  		return sensorDataList;
	}
 
	public void addSensorData(SensorData sd) {
		if(sensorDataList.size()>=intervall) {
			aggregateDataAndSendToCloud();
		}
		initSensorData();
 		SensorData newSd = new SensorData();
		newSd.setMeasurement(sd.getMeasurement());
		newSd.setSensorID("Thermometer-1");
 		sensorDataList.add(newSd);
	}

	private void aggregateDataAndSendToCloud() {
		double median = getMedian(sensorDataList);
		try {
			SensorEdgeWorkerController.sendProcessedDataToCloudNode(median);
		} catch (UnirestException e) {
 			e.printStackTrace();
		}
 		
	}

	private Double getMedian(List<SensorData>sublist) {
 		List<Double>measurements = sublist.stream().map(SensorData::getMeasurement).collect(Collectors.toList());
		Collections.sort(measurements);

		double median;
		if (measurements.size() % 2 == 0)
		    median = ((double)measurements.get(measurements.size()/2) + (double)measurements.get(measurements.size()/2)-1)/2;
		else
		    median = (double) measurements.get(measurements.size()/2);
		return median;
	}
	
	private void initSensorData() {
		if(sensorDataList == null) {
			
		 	sensorDataList = new ArrayList<>();

		}
 		 
 	}
	
	
//	@EventListener(ApplicationReadyEvent.class)
//	public void test() {
//		System.out.println("running");
//	}

}
