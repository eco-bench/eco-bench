package de.tuberlin.ecobench.sensordataedgeworker.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.tuberlin.ecobench.sensordataedgeworker.model.BenchmarkData;
import de.tuberlin.ecobench.sensordataedgeworker.model.SensorData;
import de.tuberlin.ecobench.sensordataedgeworker.model.SensorWorkerConfig;


@Service
public class SensorService {

	private static final Logger logger = LoggerFactory.getLogger(SensorService.class);

	private static final String workerID = "Sensor-Processing-Worker-"+(int)(Math.random()*1000);
	
	// Puffer für SensorDaten
	private static List<SensorData> sensorDataList = new ArrayList<>();
	// Benachbarte Nodes aus properties als Kommaseparierte, vollständige hostnames
	private static List<String> hostnames = new ArrayList<>();
	
	// Buffer für Benchmark-Messungen
	private static List<BenchmarkData> benchDataList = new ArrayList<>();

	private static SensorWorkerConfig config = new SensorWorkerConfig();
	private static File benchMarkFile = null;
 
	/**
	 * Messung aus dem Post-Request zwischenspeichern. <br>
	 * Falls ein Kritischer wert vorliegt, werden Messdaten benachbarter Plantagen
	 * abgefragt, und die Bewässerung ggf. angepasst <br>
	 * Falls anzahl der Listeneinträge > übergebenes Intervall, werden die Daten zum
	 * Median aggregiert und in die Cloud gesendet
	 * 
	 * @param sd
	 */
	public void processSensorData(SensorData sd) {
		// Speichern
 		SensorData newSd = SensorData.initSensorObject();
		newSd.setMeasurement(sd.getMeasurement());
		sensorDataList.add(newSd);
        
//		if(benchDataList.size()>=config.getlogStorageLimit()) {
 
//		Logs over HTTP
			
			//if(SensorWorkerConfig.benchEndpointSpecified()) {
//				try {
// 					sendLogsToEndpoint();
//				} catch (UnirestException e) {
//					 
//				}
//			}
	//	}
		// Bei kritischen Messwert Daten benachbarter Plantagen abfragen, bewässerung anpasssen und an das Bewässerungssystem (irrigation System) übermitteln
		if (this.isCriticalValue(newSd.getSensorType(), newSd.getMeasurement())) {
			// Daten von benachbarten Plantagen abfragen
			List<SensorData> nearPlantData = getWeatherDataFromNeighbourPlantations();
			// SOLL-Wasserdruck
			int waterPressureBar = 10;
			if (nearPlantData == null || nearPlantData.isEmpty()) {
				this.adjustIrrigation(waterPressureBar);
			} else {

				// Empfangene SensorTypen extrahieren
				List<String> uniqueSensorTypes = getUniqueTypesFromList(nearPlantData);
				
				long startTime = System.nanoTime();
 		  
				for (String sensorType : uniqueSensorTypes) {
					double avgMeasurement = extractAVGValue(nearPlantData, sensorType);
					if (this.isCriticalValue(sensorType, avgMeasurement)) {
						// für jeden kritischen Messwert den Wasserdruck um 10 anpassen
						waterPressureBar += 10;
					}

				}
				long endTime = System.nanoTime();
				long timeDelta = endTime-startTime;
				addBenchmarkData(startTime, timeDelta, 3);
				
				this.adjustIrrigation(waterPressureBar);

			}
		}

		// Aggregationsintervall erreicht
		if (sensorDataList.size() >= Integer.valueOf(SensorWorkerConfig.getIntervall()).intValue()) {
			// Daten aggregieren und in die Cloud senden, falls Anzahl der
			// Sensordaten>Übergebenes Intervall
			aggregateDataAndSendToCloud();
		}

	}

	/**
	 * 
	 * Median für jede SensorID aus der Liste berechnen und nach SensorID gruppieren
	 * 
	 * @param dataList
	 * @return
	 */
	private Map<String, Double> calculateMedianBySensorGroup(List<SensorData> dataList) {
		// Sensornamen extrahieren
		
		long startTime = System.nanoTime();
	 
 
 		Map<String, Double> sensorMap = new HashMap<>();
		List<String> uniqueSensorNames = getUniqueNamesFromList(dataList);
		for (String sensorID : uniqueSensorNames) {
			double median = getMedian(sensorDataList.stream().filter(s -> s.getSensorID().equalsIgnoreCase(sensorID))
					.collect(Collectors.toList()));
			sensorMap.put(sensorID, Double.valueOf(median));
		}
		long endTime = System.nanoTime();
		long timeDelta = endTime-startTime;
		addBenchmarkData(startTime, timeDelta, 3);
		return sensorMap;

	}

	/**
	 * Liste mit Sensordaten sensorDataList speichert Sensordaten unterschiedlicher
	 * Sensortypen. In der Funktion werden zuerst unterschiedliche SensorNamen
	 * extrahiert und für jeden Sensor Median seiner Messwerte ermittelt. Die
	 * ermittelten Durchschnittswerte werden einzeln in die Cloud gesendet.
	 */
	private void aggregateDataAndSendToCloud() {
 		// Für jede sensorID Median ermitteln und zum Cloud-Node senden
		Map<String, Double> sensorMap = this.calculateMedianBySensorGroup(sensorDataList);
		for (String key : sensorMap.keySet()) {
			// key = sensorID
			try {
				this.sendProcessedDataToCloudNode(key, sensorMap.get(key).doubleValue());
			} catch (UnirestException e) {
				logger.error("Fehler beim Senden des berechneten Medians.");
			}

			// Liste Leeren, falls Übermitteln der Daten erfolgreich war
			sensorDataList = new ArrayList<>();
		}

	}

	/**
	 * Liste mit Daten zu einem JSON-Array konvertieren
	 * 
	 * @return
	 */
	public static String getSensorDataList() {
		String jsonSensorData = new Gson().toJson(sensorDataList);
		return jsonSensorData;
	}

	/**
	 * Namen der Sensoren ohne Duplikate aus der Liste extrahieren
	 * 
	 * @param dataList
	 * @return
	 */
	public List<String> getUniqueNamesFromList(List<SensorData> dataList) {
		Set<String> uniqueNames = new HashSet<String>(
				dataList.stream().map(SensorData::getSensorID).collect(Collectors.toList()));
		List<String> result = new ArrayList<>();
		result.addAll(uniqueNames);
		return result;
	}

	/**
	 * Typen der Sensoren ohne Duplikate aus der Liste extrahieren
	 * 
	 * @param dataList
	 * @return
	 */
	public List<String> getUniqueTypesFromList(List<SensorData> dataList) {
		Set<String> uniqueTypes = new HashSet<String>(
				dataList.stream().map(SensorData::getSensorType).collect(Collectors.toList()));
		List<String> result = new ArrayList<>();
		result.addAll(uniqueTypes);
		return result;
	}

	/**
	 * Messwerte eines SensorTyps in der Liste Finden und Durchschnitt berechnen
	 * 
	 * @param dataList List of SensorData
	 * @return
	 */
	public static int extractAVGValue(List<SensorData> dataList, String sensorType) {
		long startTime = System.nanoTime();
		int avg = dataList.stream().filter(sd -> sd.getSensorType().toUpperCase().equals("sensorType"))
				.map(SensorData::getMeasurement).reduce(0.0, (x, y) -> {
					return (x * (dataList.size() - 1) + y) / dataList.size();
				}).intValue();
		long endTime = System.nanoTime();
		long timeDelta = endTime-startTime;
		addBenchmarkData(startTime, timeDelta, 3);
		logger.info("Average of " + sensorType + " = " + avg);
		return avg;
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
	 * Ermitteln, ober der erfasste Messwert einen kritischen Wert überschreitet
	 * 
	 * @param sensorType
	 * @param value
	 * @return
	 */
	public boolean isCriticalValue(String sensorType, double value) {
		boolean result = false;
		
		switch (sensorType.toUpperCase()) {
		case "TEMPERATURE":
			result = ((int) value > (int) SensorWorkerConfig.getCriticalTemp()) ? true : false;
			break;
		case "HUMIDITY":
			result = ((int) value < (int) SensorWorkerConfig.getCriticalHumidity()) ? true : false;
			break;
		case "SOILMOISTURE":
			result = ((int) value < (int) SensorWorkerConfig.getCriticalsoilMoisture()) ? true : false;
			break;
		case "PRECIPITATION":
			result = ((int) value < (int) SensorWorkerConfig.getCriticalPrecipitation()) ? true : false;
			break;
		default:
			logger.error("Unknown Sensor Type: " + sensorType);
		}
		return result;
	}

	/**
	 * Wetterdaten von Benachbarten Plantagen abfragen
	 */
	private List<SensorData> getWeatherDataFromNeighbourPlantations() {
		List<SensorData> neighbourPlantSensorData = new ArrayList<>();
		
		if(hostnames==null || hostnames.isEmpty()) {
			logger.error("No hostnames for neighbour plantings specified.");
			return neighbourPlantSensorData;
		}
		for (String hostname : hostnames) {

			HttpResponse<String> response;
			try {
				logger.info("Requesting Weather Data from Neighbor Plantations.");
				long startTime = System.nanoTime();
				response = Unirest.get(hostname).header("accept", "application/json")
						.header("content-type", "application/json").asString();
				long endTime = System.nanoTime();
				long timeDelta = endTime - startTime;
				addBenchmarkData(startTime, timeDelta, 1);
				String resp = response.getBody();
				// Json Array aus dem GET-Request parsen
				JSONArray jsonarray = new JSONArray(resp);
				logger.info("got json array: ");
				logger.info("" + jsonarray);
				for (int i = 0; i < jsonarray.length(); i++) {
					JSONObject jsonobject = jsonarray.getJSONObject(i);
					SensorData sd = new SensorData();
					sd.setSensorID(jsonobject.getString("sensorID"));
					sd.setSensorType(jsonobject.getString("sensorType"));
					sd.setMeasurement(jsonobject.getDouble("measurement"));
					neighbourPlantSensorData.add(sd);
				}
			} catch (UnirestException e) {
				logger.error("Error requesting neighbour Edge-Nodes.");
			}

		}
		return neighbourPlantSensorData;
	}

	/**
	 * 
	 * Übermittelt das berechnete median an einen entfernten cloud Node
	 * HTTP-POST-Request Cloud Node wird über host,port und url spezifiziert
	 * 
	 * 
	 * @param sensorId
	 * @param median
	 * @throws UnirestException
	 */
	public void sendProcessedDataToCloudNode(String sensorId, double median) throws UnirestException {
		String targetHost = SensorWorkerConfig.getTargetHost();
		String targetPort = SensorWorkerConfig.getTargetPort();
		String url = SensorWorkerConfig.getUrl();
		
		if(targetHost==null || targetHost.isEmpty() || targetPort == null ||targetPort.isEmpty()) {
			logger.error("No Cloud-Host specified.");
 		}else {
		logger.info("Sending Data to Cloud.");
		logger.info("Median:"+ median);
		long startTime = System.nanoTime();
		HttpResponse<JsonNode> jsonResponse = Unirest.post("http://" + targetHost + ":" + targetPort + url)
				.header("accept", "application/json").header("content-type", "application/json")
				.body("{" + "	\"sensorID\":\"" + sensorId + "\"," + " \"value\":" + median + "}").asJson();
        long endTime = System.nanoTime();
        long timeDelta = endTime - startTime;
        addBenchmarkData(startTime, timeDelta, 2);
 		}
	}

	/**
	 * 
	 * Bewässerung anpassen HTTP-POST-Request Bewässerung-Worker wird über host,port
	 * und url spezifiziert
	 * 
	 * 
	 * @param watrerPressure
	 * 
	 * @throws UnirestException
	 */
	public void adjustIrrigation(int waterPressure)  {
		
		String irrigationHost = SensorWorkerConfig.getIrrigationHost();
		String irrigationPort = SensorWorkerConfig.getIrrigationPort();
		
		if(irrigationHost==null || irrigationHost.isEmpty() || irrigationPort==null || irrigationPort.isEmpty()) {
			logger.error("No Irrigation Endpoint specified");
		}else {
		logger.info("Wasserdruck wird auf "+waterPressure+" angepasst.");
 		//neben waterPressure werden timestamp und Worker-ID für spätere Benchmark-Berechnungen mit gesendet
		try {
			long startTime = System.nanoTime();
			HttpResponse<JsonNode> jsonResponse = Unirest.post("http://" + irrigationHost + ":" + irrigationPort + "/water")
					.header("accept", "application/json").header("content-type", "application/json")
					.body("{" + "	\"workerID\":\"" + SensorService.workerID + "\","
					+ " \"waterPressure\":" + waterPressure + ","
					+ " \"timeDelta\":" + System.currentTimeMillis()+
							"}").asJson();
			long endTime = System.nanoTime();
			long timeDelta = endTime - startTime;
			addBenchmarkData(startTime, timeDelta, 0);
		} catch (UnirestException e) {
 	        logger.error("Keine Bewässerungsanlage spezifiziert.");
		}
		}
	}

	/*
	 * 
	 * 
	 * Parameter setzen
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * Konfiguration zur Runtime aus dem HTTP-Request auslesen und anpassen
	 * 
	 * @param key
	 * @param param
	 */
	public static void changeConfig(SensorWorkerConfig props) {
		
		logger.info("Passe Konfiguration an.");
 		try {
			Thread.currentThread().sleep(1000);
 		} catch (InterruptedException e) {
 		}
 		
		if(props != null) {
			if(props.getIntervall()!=null && !props.getIntervall().isEmpty()) {
			config.setintervall(props.getIntervall());		
			}
		    config.setCriticalHumidity(props.getCriticalHumidity());
		    config.setCriticalPrecipitation(props.getCriticalPrecipitation());
		    config.setCriticalsoilMoisture(props.getCriticalsoilMoisture());
		    config.setCriticalTemp(props.getCriticalTemp());
		    config.setintervall(props.getIntervall());
		}
	 
		logger.info("Konfiguration angepasst.");
		try {
			Thread.currentThread().sleep(1000);
 		} catch (InterruptedException e) {
 		}
 		
	}
	/**
	 * Logs an den Benchmarking Endpoint über HTTP zur Auswertung senden
	 * 
	 * @param ti
	 * @param median
	 * @throws UnirestException
	 */
//	private static void sendLogsToEndpoint() throws UnirestException {
// 
//		if (SensorWorkerConfig.getBenchmarkEndPointHost() != null
//				&& !SensorWorkerConfig.getBenchmarkEndPointHost().isEmpty()
//				&& SensorWorkerConfig.getBenchmarkEndpointPort() != null
//				&& !SensorWorkerConfig.getBenchmarkEndpointPort().isEmpty()) {
//			logger.info("Sending Logs");
//			String jsonLogs = new Gson().toJson(benchDataList);
//			HttpResponse<JsonNode> jsonResponse = Unirest
//					.post("http://" + SensorWorkerConfig.getBenchmarkEndPointHost() + ":"
//							+ SensorWorkerConfig.getBenchmarkEndPointHost()
//							+ SensorWorkerConfig.getBenchmarkEndpointURL())
//					.header("accept", "application/json").header("content-type", "application/json").body(jsonLogs)
//					.asJson();
//			// Speicher freigeben
//			benchDataList = new ArrayList<>();
//		} else {
//			logger.info("Kein Bancharking Endpoint spezifiziert");
//		}
//	}
	
	/**
	 * Benchmarkdaten in einem Log-File als JSON abspeichern
	 * Log-File path : benchmarkLogsPath ENV-Variable
	 */
//	public static void storeBenchmarkDataToFile(BenchmarkData bd) {
//		String benchFilePath = "";
//		if(benchMarkFile==null) {
//		String time = ""+System.currentTimeMillis();
//		benchFilePath = config.getBenchmarkLogsPath()+"/BenchmarkData_"+time;
//		benchMarkFile = new File(benchFilePath);
//		logger.info("Benchmarkfile created: "+benchFilePath);
//		   
//		}
//		String benchData = new Gson().toJson(bd);
//		try {
//			FileWriter fw = new FileWriter(benchMarkFile,true);
//			PrintWriter out = new PrintWriter(new BufferedWriter(fw));
//			
//			logger.info("Storing logs to File "+benchMarkFile.getAbsolutePath());
//			out.println(benchData.trim());
//			out.flush();
//			out.close();
//			benchDataList.clear();
// 		}  catch (IOException e) {
//			logger.error("Cant write to Log-File: "+benchMarkFile.getAbsolutePath());
//		    logger.error(e.getMessage());
//		}
//		
//	}
	
	
	public static void addBenchmarkData(long timestamp, long timedelta, int type) {
		BenchmarkData bd = new BenchmarkData();
	    bd.setWorkerID(workerID);
	    bd.setTimestamp(timestamp);
	    bd.setType(type);
	    bd.setTimeDelta(timedelta);
		SensorService.benchDataList.add(bd);
	//	storeBenchmarkDataToFile(bd);
		String benchData = new Gson().toJson(bd);
        System.out.println("latency: "+benchData.trim());
	}
	
	/**
	 * Lokale Konfiguration abfragen
	 * @return
	 */
	public static String getConfig() {
     	JSONObject obj = new JSONObject();
		obj.put("intervall", SensorWorkerConfig.getIntervall());
		obj.put("criticalTemp", SensorWorkerConfig.getCriticalTemp());
		obj.put("criticalHumidity", SensorWorkerConfig.getCriticalHumidity());
		obj.put("criticalsoilMoisture", SensorWorkerConfig.getCriticalsoilMoisture());
		obj.put("criticalPrecipitation", SensorWorkerConfig.getCriticalPrecipitation());
		return obj.toString();
 }
 
 
  

	public static String getWorkerid() {
		return workerID;
	}

 

	public List<SensorData> getSensorData() {
		return sensorDataList;
	}

}
