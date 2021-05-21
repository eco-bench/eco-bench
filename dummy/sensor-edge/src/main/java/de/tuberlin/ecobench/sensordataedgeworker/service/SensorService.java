package de.tuberlin.ecobench.sensordataedgeworker.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.tuberlin.ecobench.sensordataedgeworker.SensordataedgeworkerApplication;
import de.tuberlin.ecobench.sensordataedgeworker.model.SensorData;
import jdk.internal.net.http.common.Log;

@Service
@EnableScheduling
public class SensorService {
	
    private static final Logger logger = LoggerFactory.getLogger(SensorService.class);

	private static List<SensorData> sensorDataList = new ArrayList<>();
    private static List<String> hostnames = new ArrayList<>();
	private static String intervall;
	private static String targetHost;
	private static String targetPort;
	private static String url;
	
	
  
	/**
	 * Messung aus dem Post-Request zwischenspeichern. <br>Falls anzahl der
	 * Listeneinträge > übergebenes Intervall, werden die Daten zum Median
	 * aggregiert und in die Cloud gesendet
	 * 
	 * @param sd
	 */
	public void addSensorData(SensorData sd) {
		
		if (sensorDataList.size() >= Integer.valueOf(intervall).intValue()) {
			// Daten aggregieren und in die Cloud senden, falls Anzahl der Sensordaten>Übergebenes Intervall
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
        logger.info("Meidan berechnen: "+sensorDataList);
 		try {
			this.sendProcessedDataToCloudNode(SensorData.getSensorID(), median);
			//Liste Leeren, falls Übermitteln der Daten erfolgreich war
			sensorDataList = new ArrayList<>();
 		} catch (UnirestException e) {
 		    logger.error("Fehler beim Senden des berechneten Medians.");
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
	 * Temperatur von allen anderen Edge Worker Nodes abfragen <br>
	 * alle 1000 ms
	 * @param sublist
	 * @return
	 * @throws UnirestException 
	 */
	@Scheduled(fixedRateString = "10000")
	private void getTemperature() {
 
		for(String hostname:hostnames) {
 
	     HttpResponse<String> response;
		try {
		    logger.info("Frage Temoperaturdaten ab.");
			response = Unirest.get(hostname).asString();
		    String resp = response.getBody();
               //DO nothing
		} catch (UnirestException e) {
 		    logger.error("Fehler bei Datenabfrage von anderen Edge-Nodes.");
  		}
   	  
		}
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
 		logger.info("Sending Data to Cloud.");
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
	@Value("#{'${edge.endpoints}'.split(',')}") 
	public void sethostnames(List<String> hostnames) {
		  SensorService.hostnames = hostnames;
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
