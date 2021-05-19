package de.tuberlin.ecobench.sensordataedgeworker.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.tuberlin.ecobench.sensordataedgeworker.model.SensorData;

@Service
public class SensorService {

	private static List<SensorData> sensorDataList = new ArrayList<>();

	private static String intervall;
	private static String targetHost;
	private static String targetPort;
	private static String url;

	/**
	 * Messung aus dem Post-Request zwischenspeichern Falls anzahl der
	 * Listeneinträge > übergebenes Intervall, werden die Daten zum Median
	 * aggregiert und in die Cloud gesendet
	 * 
	 * @param sd
	 */
	public void addSensorData(SensorData sd) {
		if (sensorDataList.size() >= Integer.valueOf(intervall).intValue()) {
			// Daten aggregieren und in die Cloud senden
			aggregateDataAndSendToCloud();
		}
		SensorData newSd = new SensorData();
		newSd.setMeasurement(sd.getMeasurement());
		sensorDataList.add(newSd);
	}

	/**
	 * Median berechnen und in die Cloud Senden
	 */
	private void aggregateDataAndSendToCloud() {
		double median = getMedian(sensorDataList);
		System.out.println("median " + median);
		try {
			this.sendProcessedDataToCloudNode(SensorData.getSensorID(), median);
		} catch (UnirestException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Berechnet Median
	 * 
	 * @param sublist
	 * @return
	 */
	private Double getMedian(List<SensorData> sublist) {
		List<Double> measurements = sublist.stream().map(SensorData::getMeasurement).collect(Collectors.toList());
		Collections.sort(measurements);

		double median;
		if (measurements.size() % 2 == 0)
			median = ((double) measurements.get(measurements.size() / 2)
					+ (double) measurements.get(measurements.size() / 2) - 1) / 2;
		else
			median = (double) measurements.get(measurements.size() / 2);
		return median;
	}

	/**
 	 * 
 	 * Übermittelt das berechnete median an einen entfernten cloud Node
 	 * HTTP-POST-Request
 	 * Cloud Node wird über host,port und url spezifiziert
 	 * 
 	 * 
 	 * @param sensorId
 	 * @param median
 	 * @throws UnirestException
 	 */
 	public void sendProcessedDataToCloudNode(String sensorId, double median) throws UnirestException {
 	    HttpResponse<JsonNode> jsonResponse 
 	      = Unirest.post("http://"+targetHost+":"+targetPort+url)
   	    	      .header("accept", "application/json")
 	    	       .field("sensorID", sensorId)
 	    	       .field("value",median)
 	               .asJson();
  	}

	@Value("${sensorEdgeWorker.intervall}")
	public void setintervall(String intervall) {
		SensorService.intervall = intervall;
	}

	@Value("${targetNode.host}")
	public void settargetHost(String targetHost) {
		SensorService.targetHost = targetHost;
	}

	@Value("${targetNode.port}")
	public void settargetPort(String targetPort) {
		SensorService.targetPort = targetPort;
	}

	@Value("${targetNode.url}")
	public void setUrl(String url) {
		SensorService.url = url;
	}

	/**
	 * Für Testzwecke
	 * 
	 * @return
	 */
	public List<SensorData> getSensorData() {
		return sensorDataList;
	}

}
