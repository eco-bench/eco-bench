package de.tuberlin.ecobench.SensorEdgeDevice.model;

import org.springframework.stereotype.Component;

@Component
public class SensorData {

	private double measurement = Math.random()*100;

	public double getMeasurement() {
		return measurement;
	}

 
	
}
