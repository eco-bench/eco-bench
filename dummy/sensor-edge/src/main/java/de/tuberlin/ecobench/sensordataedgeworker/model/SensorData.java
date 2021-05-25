package de.tuberlin.ecobench.sensordataedgeworker.model;


import lombok.Data;

@Data
public class SensorData {

	
	/**
	 *  sensorID automatisch generieren
	 */
  	private static String sensorID = "temperature-"+(int)(Math.random()*100);
	private Double measurement;
	
	/**
	 * für testZwecke
	 * @return
	 */
	public static String getSensorID() {
		return sensorID;
	}
}
