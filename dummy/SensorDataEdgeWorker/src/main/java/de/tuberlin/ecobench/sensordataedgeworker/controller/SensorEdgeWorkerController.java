package de.tuberlin.ecobench.sensordataedgeworker.controller;

import java.util.List;

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
	@PostMapping(path = "/sensorData")
	public ResponseEntity<SensorData> postSensorData(@RequestBody SensorData sd) {
		sensorService.addSensorData(sd);
		return new ResponseEntity<>(HttpStatus.OK);

	}

	/**
	 * Temperatur Abfragen
	 * 
	 * @param sd Sensordata
	 * @return -1
	 */
	@GetMapping(path = "/alert")
	public ResponseEntity<Integer> getTemperature() {
		return new ResponseEntity<>(-1, HttpStatus.OK);
	}

}
