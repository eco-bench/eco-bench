package de.tuberlin.ecobench.sensordataedgeworker.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.tuberlin.ecobench.sensordataedgeworker.model.SensorData;
import de.tuberlin.ecobench.sensordataedgeworker.service.SensorService;

@RestController
@RequestMapping("/")
public class SensorEdgeWorkerController {
	
 	private final SensorService sensorService = new SensorService();
 	
 	@Value("${sensordata.sensorid}")
	private String sensorid;
 	@Value("${targetNode.host}")
 	private String targetHost;
 	@Value("${targetNode.port}")
 	private String targetPort;
 	@Value("${sensorEdgeWorker.aggregationintervall}")
 	private String intervall;
 	
 	
 	@GetMapping
	public ResponseEntity<List<SensorData>> getSensorData() {
		
 		List<SensorData> sensorData = sensorService.getSensorData();
 		
		return new ResponseEntity<>(sensorData, HttpStatus.OK);
		
	}
 	
 	@PostMapping(path="/sensorData")
	public ResponseEntity<SensorData> postSensorData( @RequestBody SensorData sd) {
		
 		sensorService.addSensorData(sd);
   		return new ResponseEntity<>(HttpStatus.OK);
 
	}
 	
    /**
     * Aggregierte Daten zum Cloudnode schicken
     * @param median 
     * TODO: Beenden
     */
 	public static void sendProcessedDataToCloudNode(double median) throws UnirestException {

 	    HttpResponse<JsonNode> jsonResponse 
 	      = Unirest.post("http://www.mocky.io/v2/5a9ce7663100006800ab515d")
 	      .body("{\"name\":\"Sam Baeldung\", \"city\":\"viena\"}")
 	      .asJson();
 	 
  	}

}
