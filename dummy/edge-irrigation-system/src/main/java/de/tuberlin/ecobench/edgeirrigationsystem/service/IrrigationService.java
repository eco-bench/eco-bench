package de.tuberlin.ecobench.edgeirrigationsystem.service;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.tuberlin.ecobench.edgeirrigationsystem.model.IrrigationConfig;
import de.tuberlin.ecobench.edgeirrigationsystem.model.IrrigationProperties;

@Service
public class IrrigationService {

	private static final Logger log = LoggerFactory.getLogger(IrrigationService.class);

	private static List<IrrigationConfig> configList = new ArrayList<>();

	private static IrrigationProperties props = new IrrigationProperties();

	/**
	 * 
	 * Ausgelagert in SensorEdgeWorker 
	 * 
	 * Logs an den Benchmarking Endpoint zur Auswertung senden
	 * 
	 * @param ti
	 * @param median
	 * @throws UnirestException
	 */
//	private static void sendLogsToEndpoint() throws UnirestException {
//		if (IrrigationProperties.getBenchmarkEndPointHost() != null
//				&& !IrrigationProperties.getBenchmarkEndPointHost().isEmpty()
//				&& IrrigationProperties.getBenchmarkEndpointPort() != null
//				&& !IrrigationProperties.getBenchmarkEndpointPort().isEmpty()) {
//			log.info("Sending Logs");
//			String jsonLogs = new Gson().toJson(configList);
//			HttpResponse<JsonNode> jsonResponse = Unirest
//					.post("http://" + IrrigationProperties.getBenchmarkEndPointHost() + ":"
//							+ IrrigationProperties.getBenchmarkEndPointHost()
//							+ IrrigationProperties.getBenchmarkEndpointURL())
//					.header("accept", "application/json").header("content-type", "application/json").body(jsonLogs)
//					.asJson();
//			// Speicher freigeben
//			configList = new ArrayList<>();
//		} else {
//			log.info("Kein Bancharking Endpoint spezifiziert");
//		}
//	}

	/**
	 * für test
	 * 
	 * @return
	 */
	public static String getConfigList() {
		String jsonLogs = new Gson().toJson(configList);
		return jsonLogs;

	}

	public static void addConfig(IrrigationConfig config) {
		log.info("Water Pressure Adjusted to: " + config.getWaterPressure() + " bar.");
		IrrigationConfig newConfig = new IrrigationConfig();
		newConfig.setWorkerID(config.getWorkerID());
		newConfig.setWaterPressure(config.getWaterPressure());
		// Calculate Time Delta: Worker timestamp - Irrigation Timestamp (Transmission
		// Latency)
		newConfig.setTimeDelta(System.currentTimeMillis() - config.getTimeDelta());
		configList.add(newConfig);
		
		
 		if (configList.size() >= IrrigationProperties.getlogStorageLimit()) {
			//Logübermittlung ausgelagert in den SensorEdgeWorker

 			configList.clear();
			
			//ausgelagert in den SensorEdgeWorker

//			try {
//				sendLogsToEndpoint();
//			} catch (UnirestException e) {
//				log.error("Error sending Logs to Benchmarking Endpoint");
//			}
		}
	}

	/**
	 * properties abfragen
	 * 
	 * @return
	 */
	public static String getAppProperties() {
		JSONObject obj = new JSONObject();
		obj.put("benchmarkEndPointHost", IrrigationProperties.getBenchmarkEndPointHost());
		obj.put("benchmarkEndpointPort", IrrigationProperties.getBenchmarkEndpointPort());
		obj.put("benchmarkEndpointURL", IrrigationProperties.getBenchmarkEndpointURL());
		obj.put("logStorageLimit", IrrigationProperties.getlogStorageLimit());
		return obj.toString();
	}

	/**
	 * Properties Anpassen
	 * 
	 * @param props
	 */
	public static void changeProperties(IrrigationProperties props) {
		if (props.getBenchmarkEndPointHost() != null && !props.getBenchmarkEndPointHost().isEmpty()) {
			props.setBenchmarkEndPointHost(props.getBenchmarkEndPointHost());
		}
		if (props.getBenchmarkEndpointPort() != null && !props.getBenchmarkEndpointPort().isEmpty()) {

			props.setBenchmarkEndpointPort(props.getBenchmarkEndpointPort());
		}
		if (props.getBenchmarkEndpointURL() != null && !props.getBenchmarkEndpointURL().isEmpty()) {

			props.setBenchmarkEndpointURL(props.getBenchmarkEndpointURL());
		}
			props.setlogStorageLimit(props.getlogStorageLimit());
		 
	}

}
