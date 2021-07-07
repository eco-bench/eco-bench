package de.tuberlin.ecobench.sensordataedgeworker.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tuberlin.ecobench.sensordataedgeworker.model.SensorData;
import de.tuberlin.ecobench.sensordataedgeworker.model.SensorWorkerConfig;
import de.tuberlin.ecobench.sensordataedgeworker.service.SensorService;
 
@RestController
@RequestMapping("/")
public class SensorEdgeWorkerController {

	private final SensorService sensorService = new SensorService();
    private static final Logger logger = LoggerFactory.getLogger(SensorEdgeWorkerController.class);

	/**
	 * für Testzwecke
	 * 
	 * @return
	 */
	@GetMapping
	public ResponseEntity<List<SensorData>> getSensorData() {

		List<SensorData> sensorData = sensorService.getSensorData();
		return new ResponseEntity<>(sensorData, HttpStatus.OK);
	}

	/**
	 * Sensor-Messung hinzufügen
	 * 
	 * @param sd Sensordata
	 * @return
	 */
	@PostMapping(path = "sensorData", consumes = {"application/json","text/plain","*/*"}, produces = {"application/json","text/plain","*/*"} )
	public ResponseEntity<SensorData> postSensorData(@RequestBody SensorData sd) {
  		sensorService.processSensorData(sd);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Temperatur Abfragen
	 * 
	 * @param sd Sensordata
	 * @return -1
	 */
	@GetMapping(path = "sensorData",consumes = "application/json",produces = "applicatin/json")
	public ResponseEntity<String> getWeather() {
		logger.info("Processing Weather Data Request from other Plantation.");
	    String resp = SensorService.getSensorDataList();
 		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
	
	/**
	 * Properties abfragen
	 * @return
	 */
	@GetMapping(path = "config",produces="application/json", consumes = "application/json")
	public ResponseEntity<String> getConfig() { 
		String resp = SensorService.getConfig();
 		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
	
	/**
	 * Properties anpassen
	 * @param props
	 * @return
	 */
	@PostMapping(path = "config", consumes = "application/json")
	public ResponseEntity<SensorWorkerConfig> postAppProperties(@RequestBody SensorWorkerConfig config) { 
	     SensorService.changeConfig(config);
 		return new ResponseEntity<>(HttpStatus.OK);
	}
}
