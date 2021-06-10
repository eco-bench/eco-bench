package de.tuberlin.ecobench.edgeirrigationsystem.model;

 
public class IrrigationConfig {

	/**
	 * Wasserdruck in Bar
	 */
	int waterPressure;
	
	/**
	 * 
	 * Zeitdifferenz zwischen Absenden der Bewässerungskonfiguration von dem Sensor-Edge-Worker und dem Empfang der Nachricht
	 *  auf dem IrrigationController  
	 */
	long timeDelta;
	
	/**
	 * Id des Workers, der die Nachticht übermittelt hat
	 */
	String workerID;
	
	public int getWaterPressure() {
		return this.waterPressure;
	}
	
	public void setWaterPressure(int waterPressure) {
		this.waterPressure = waterPressure;
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
