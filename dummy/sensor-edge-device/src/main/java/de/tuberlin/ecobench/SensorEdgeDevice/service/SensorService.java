package de.tuberlin.ecobench.SensorEdgeDevice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import de.tuberlin.ecobench.SensorEdgeDevice.model.SensorData;


@Service
@EnableScheduling
public class SensorService {

	
   private static final Logger log = LoggerFactory.getLogger(SensorService.class);

	
   @Value("${WORKER_ENDPOINT}")
   private String WORKER_ENDPOINT;
   
   @Value("${WORKER_PORT}")
   private String WORKER_PORT;
   
   @Value("${WORKER_IP}")
   private String WORKER_IP;
   
   @Value("${PING_INTERVAL}")
   private String PING_INTERVAL;
	
	@Scheduled(fixedDelay = 50)
   	public void postSensorData() {
	
    SensorData sd = new SensorData();
	String jsonSensorData = new Gson().toJson(sd);
        //log.info("WORKER URL: http://"+WORKER_IP+":"+WORKER_PORT+"/"+WORKER_ENDPOINT);
 	try {
 		HttpResponse<JsonNode> jsonResponse = Unirest.post(WORKER_IP+":"+WORKER_PORT+"/"+WORKER_ENDPOINT)
				.header("accept", "application/json").header("content-type", "application/json")
				.body(jsonSensorData)  
				.asJson();
		log.info("Sending sensor data :"+sd.getMeasurement());
	} catch (UnirestException e) {
	    log.error("Error while Sending data:");
	    log.error(e.getMessage());
	    try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
 
		}
	}
	//Parametrisierter Intervall
	try {
		Thread.sleep(Integer.valueOf(PING_INTERVAL));
	} catch (InterruptedException e) {
 		e.printStackTrace();
	}
 	}
	
}
