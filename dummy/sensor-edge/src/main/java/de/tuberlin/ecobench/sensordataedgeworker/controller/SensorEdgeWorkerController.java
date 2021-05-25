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
	@PostMapping(path = "/sensorData", produces = "applicatin/json")
	public ResponseEntity<SensorData> postSensorData(@RequestBody SensorData sd) {
		//logger.info("Sensordaten empfangen: "+sd.getMeasurement());
		sensorService.addSensorData(sd);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * Temperatur Abfragen
	 * 
	 * @param sd Sensordata
	 * @return -1
	 */
	@GetMapping(path = "/alert",consumes = "application/json")
	public ResponseEntity<Integer> getTemperature() {
		logger.info("Alert empfangen.");
		return new ResponseEntity<>(-1, HttpStatus.OK);
	}

}
