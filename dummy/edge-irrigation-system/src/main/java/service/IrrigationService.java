package service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import model.IrrigationConfig;
 
public class IrrigationService {

	
	private static final Logger log = LoggerFactory.getLogger(IrrigationService.class);

	private static List<IrrigationConfig> configList = new ArrayList<>();
	
	private static int logStorageLimit;
	private static String benchmarkEndPointHost;
	private static String benchmarkEndpointPort;
	private static String benchmarkEndpointURL;
	/**
	 * Logs an den Benchmarking Endpoint zur Auswertung senden
	 * 
	 * @param ti
	 * @param median
	 * @throws UnirestException
	 */
 	private static void sendLogsToEndpoint() throws UnirestException {
 		log.info("Sending Logs");
 		String jsonLogs = new Gson().toJson(configList);
   	    HttpResponse<JsonNode> jsonResponse 
 	      = Unirest.post("http://"+benchmarkEndPointHost+":"+benchmarkEndpointPort+benchmarkEndpointURL)
  	    	      .header("accept", "application/json")
 	    	      .header("content-type", "application/json")
 	    	      .body(jsonLogs)
 	               .asJson();
  	}
 	
 	public static String getConfigList() {
 		String jsonLogs = new Gson().toJson(configList);
 		return jsonLogs;

 	}
 	
 	public static void addConfig(IrrigationConfig config) {
 		log.info("Water Pressure Adjusted to: "+config.getWaterPressure()+" bar.");
 		IrrigationConfig newConfig = new IrrigationConfig();
 		newConfig.setWorkerID(config.getWorkerID());
        newConfig.setWaterPressure(config.getWaterPressure());
        //Calculate Time Delta: Worker timestamp - Irrigation Timestamp (Transmission Latency)
        newConfig.setTimeDelta(System.currentTimeMillis()-config.getTimeDelta());
 		configList.add(config);
 		if(configList.size()>=logStorageLimit) {
 			try {
				sendLogsToEndpoint();
			} catch (UnirestException e) {
 			    log.error("Error sending Logs to Benchmarking Endpoint");
			}
 		}
 	}
 	
	@Value("${logStorageLimit}")
	public void setlogStorageLimit(int logStorageLimit) {
		IrrigationService.logStorageLimit = logStorageLimit;
	}
 
	@Value("${benchmarkEndPointHost}")
	public static void setBenchmarkEndPointHost(String benchmarkEndPointHost) {
		IrrigationService.benchmarkEndPointHost = benchmarkEndPointHost;
	}

	@Value("${benchmarkEndpointPort}")
	public static void setBenchmarkEndpointPort(String benchmarkEndpointPort) {
		IrrigationService.benchmarkEndpointPort = benchmarkEndpointPort;
	}

	@Value("${benchmarkEndpointURL}")
	public static void setBenchmarkEndpointURL(String benchmarkEndpointURL) {
		IrrigationService.benchmarkEndpointURL = benchmarkEndpointURL;
	}
	
}
