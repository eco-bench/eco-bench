package de.tuberlin.ecobench.edgeirrigationsystem.controller;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tuberlin.ecobench.edgeirrigationsystem.model.IrrigationConfig;
import de.tuberlin.ecobench.edgeirrigationsystem.service.IrrigationService;

@RestController
@RequestMapping("/")
public class IrrigationController {
    private static final Logger logger = LoggerFactory.getLogger(IrrigationController.class);

    
	/**
	 * Sensor-Messung hinzufügen
	 * 
	 * @param sd Sensordata
	 * @return
	 */
	@PostMapping(path = "water", consumes = "application/json", produces = "application/json")
	public ResponseEntity<IrrigationConfig> postIrrigationConfig 
	(@RequestBody IrrigationConfig config) {
 		IrrigationService.addConfig(config);
		return new ResponseEntity<>(HttpStatus.OK);
	}
 
	/**
	 * Testzwecke
	 * Gespeicherte Logs abfragen
	 * 
	 * 
	 * 
	 */
	@GetMapping(path = "water",produces="application/json", consumes = "application/json")
	public ResponseEntity<String> getIrrigationConfig() { 
		String resp = IrrigationService.getConfigList();
 		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
}
