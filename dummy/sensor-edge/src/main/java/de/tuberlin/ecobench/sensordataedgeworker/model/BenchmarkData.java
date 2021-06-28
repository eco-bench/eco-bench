package de.tuberlin.ecobench.sensordataedgeworker.model;

import org.springframework.stereotype.Component;

@Component
public class BenchmarkData {

	
	
	/**
	 * Typ der Messung 
	 * 0 - Edge2Edge Irrigation
	 * 1 - Edge2Edge Abfrage
	 * 2 -  Edge2Cloud
	 * 3 -  Data-Processing
	 */
	private int type;
	
	
	/**
	 * Startzeitpunkt der Messung
	 */
	private long timestamp;
	
	/**
	 * 
	 * Zeitdifferenz zwischen Absenden der Bewässerungskonfiguration von dem Sensor-Edge-Worker und dem Empfang der Nachricht
	 *  auf dem IrrigationController  
	 */
	private long timeDelta;
	
	/**
	 * Id des Workers, der die Nachticht übermittelt hat
	 */
	private String workerID;

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimeDelta() {
		return timeDelta;
	}

	public void setTimeDelta(long timeDelta) {
		this.timeDelta = timeDelta;
	}

	public String getWorkerID() {
		return workerID;
	}

	public void setWorkerID(String workerID) {
		this.workerID = workerID;
	}
 

 
	
	
}
