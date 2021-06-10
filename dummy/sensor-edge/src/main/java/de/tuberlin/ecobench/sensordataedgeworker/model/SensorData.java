package de.tuberlin.ecobench.sensordataedgeworker.model;

import java.util.concurrent.ThreadLocalRandom;

import jdk.internal.org.jline.utils.Log;

public class SensorData {

	public enum SensorType {
		TEMPERATURE, HUMIDITY, SOILMOISTURE, PRECIPITATION
	}

	/**
	 * sensorID automatisch generieren
	 */
	private String sensorID;

	/**
	 * Messwert
	 */
	private Double measurement;
	/**
	 * Sensor Typ aus TEMPERATURE, HUMIDITY, SOILMOISTURE, PRECIPITATION (Enums bei
	 * Spring problematisch)
	 */
	private String sensorType;

	public String getSensorID() {
		return this.sensorID;
	}

	public void setSensorID(String sensorID) {
		this.sensorID = sensorID;
	}

	public double getMeasurement() {
		return measurement;
	}

	public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public void setMeasurement(Double measurement) {
		this.measurement = measurement;
	}

	/**
	 * SensorID generieren
	 * 
	 * @return
	 */
	public static String generateSensorID() {
		String[] sensorNames = { "TEMPERATURE", "HUMIDITY", "SOILMOISTURE", "PRECIPITATION" };
		int randomNum = ThreadLocalRandom.current().nextInt(0, 4);
		String id = sensorNames[randomNum] + "-" + (int) (Math.random() * 100);
		return id;
	}

	public void setGeneratedSensorID() {
		this.sensorID = generateSensorID();
	}
	public void setSensorTypeByID() {
		if(this.getSensorID()!=null) {
		this.sensorType = getSensorTypeByID(this.getSensorID());
		}else {
			System.err.println("Error: Sensor id is null");
 		}
	}

	/**
	 * Für den Objekt SensorID und SensorTyp generieren
	 */
	public void initSensorObject() {
		this.setSensorID(generateSensorID());
		this.setSensorType(this.getSensorTypeByID(this.sensorID));
		
	}
	public String getSensorTypeByID(String sensorID) {
		if (sensorID.toUpperCase().contains("TEMPERATURE")) {
			return "TEMPERATURE";
		} else if (sensorID.toUpperCase().contains("HUMIDITY")) {
			return "HUMIDITY";
		} else if (sensorID.toUpperCase().contains("SOILMOISTURE")) {
			return "SOILMOISTURE";
		} else if (sensorID.toUpperCase().contains("PRECIPITATION")) {
			return "PRECIPITATION";
		} else {
			return "TEMPERATURE";
		}
	}
}
