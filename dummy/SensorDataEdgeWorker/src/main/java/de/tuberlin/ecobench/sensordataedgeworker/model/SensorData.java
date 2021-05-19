package de.tuberlin.ecobench.sensordataedgeworker.model;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class SensorData {

	
	/**
	 *  sensorID automatisch generieren
	 */
  	private static String sensorID = "temperature-"+(int)(Math.random()*100);
	private Double measurement;
	
	
	public static String getSensorID() {
		return sensorID;
	}
}
