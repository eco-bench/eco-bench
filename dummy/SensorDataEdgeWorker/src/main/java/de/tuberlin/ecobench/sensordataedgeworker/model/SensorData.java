package de.tuberlin.ecobench.sensordataedgeworker.model;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class SensorData {

	
  	private String sensorID;
	private Double measurement;
	
	
}
