package controller;

 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import model.IrrigationConfig;
import service.IrrigationService;

 
public class IrrigationController {
    private static final Logger logger = LoggerFactory.getLogger(IrrigationController.class);

    
	/**
	 * Sensor-Messung hinzufügen
	 * 
	 * @param sd Sensordata
	 * @return
	 */
	@PostMapping(path = "/water", consumes = "applicatin/json", produces = "applicatin/json")
	public ResponseEntity<IrrigationConfig> postIrrigationConfig 
	(@RequestBody IrrigationConfig config) {
		System.out.println("ll");
		logger.info("Daten empfangen: "+config.toString());
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
	@GetMapping(path = "/water", produces="application/json", consumes = "application/json")
	public ResponseEntity<String> getIrrigationConfig() {
		logger.info("GET");
		System.out.println("get");

		String resp = IrrigationService.getConfigList();
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}
}
