package de.tuberlin.ecobench.sensordataedgeworker.model;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

 
@Component
public class SensorWorkerConfig {
	
	private static final Logger logger = LoggerFactory.getLogger(SensorWorkerConfig.class);

	// Benachbarte Nodes aus properties als Kommaseparierte, vollständige hostnames
	private static List<String> hostnames = new ArrayList<>();
	// Cloud-Endpoint aus properties
	private static String intervall;
	private static String targetHost;
	private static String targetPort;
	private static String url;
	//Bewässerungsendpoint
	private static String irrigationHost;
	private static String irrigationPort;
 	// Kritische Messwerte aus properties
	private static int criticalTemp;
	private static int criticalHumidity;
	private static int criticalsoilMoisture;
	private static int criticalPrecipitation;
	private static int logStorageLimit;
	//Benchmarking spec
	private static String benchmarkEndPointHost;
	private static String benchmarkEndpointPort;
	private static String benchmarkEndpointURL;
	private static String benchmarkLogsPath;
 
	/**
	 * Prüft ob Benchmarking-Endpoint angegeben ist, um Fehlermeldungen zu vermeiden
	 * @return
	 */
	public static boolean benchEndpointSpecified() {
		if(getBenchmarkEndPointHost()!=null && !getBenchmarkEndPointHost().isEmpty()
				&& getBenchmarkEndpointURL()!=null && getBenchmarkEndpointURL().isEmpty()
				&& getBenchmarkEndpointPort()!=null && !getBenchmarkEndpointPort().isEmpty()) {
			return true;
		}
		logger.info("Benchmark-Endpoint not specified.");
		return false;
	}
 
	
	
	
	@Value("${benchmarkLogsPath}")
	public String getBenchmarkLogsPath() {
		return benchmarkLogsPath;
	}


	@Value("${benchmarkLogsPath}")
	public void setBenchmarkLogsPath(String benchmarkLogsPath) {
		logger.info("benchmarkLogsPath adjusted to " + benchmarkLogsPath); 
		SensorWorkerConfig.benchmarkLogsPath = benchmarkLogsPath;
	}





	@Value("${logStorageLimit}")
	public void setlogStorageLimit(int logStorageLimit) {
		logger.info("LOGSOTRAGELIMIT adjusted to " + logStorageLimit);
		SensorWorkerConfig.logStorageLimit = logStorageLimit;
	}

	@Value("${benchmarkEndPointHost}")
	public void setBenchmarkEndPointHost(String benchmarkEndPointHost) {
		logger.info("benchmarkEndPointHost adjusted to " + benchmarkEndPointHost); 
		SensorWorkerConfig.benchmarkEndPointHost = benchmarkEndPointHost;
	}

	@Value("${benchmarkEndpointPort}")
	public void setBenchmarkEndpointPort(String benchmarkEndpointPort) {
		logger.info("benchmarkEndpointPort adjusted to " + benchmarkEndpointPort);
		SensorWorkerConfig.benchmarkEndpointPort = benchmarkEndpointPort;
	}

	@Value("${benchmarkEndpointURL}")
	public void setBenchmarkEndpointURL(String benchmarkEndpointURL) {
		logger.info("benchmarkEndpointURL adjusted to " + benchmarkEndpointURL);
		SensorWorkerConfig.benchmarkEndpointURL = benchmarkEndpointURL;
	}

	@Value("${logStorageLimit}")
	public static int getlogStorageLimit() {
		return logStorageLimit;
	}

	@Value("${benchmarkEndPointHost}")
	public static String getBenchmarkEndPointHost() {
 		return benchmarkEndPointHost;
	}

	@Value("${benchmarkEndpointPort}")
	public static String getBenchmarkEndpointPort() {
		return benchmarkEndpointPort;
	}

	@Value("${benchmarkEndpointURL}")
	public static String getBenchmarkEndpointURL() {
 		return benchmarkEndpointURL;
	}

	@Value("#{'${edge.endpoints}'.split(',')}")
	public static List<String> getHostnames() {
		return hostnames;
	}
	
	@Value("${intervall}")
	public static String getIntervall() {
		return intervall;
	}
	
	@Value("${targetNode.host}")
	public static String getTargetHost() {
		return targetHost;
	}
	
	@Value("${targetNode.port}")
	public static String getTargetPort() {
		return targetPort;
	}
	
	@Value("${targetNode.url}")
	public static String getUrl() {
		return url;
	}
	
	@Value("${irrigation.host}")
	public static String getIrrigationHost() {
		return irrigationHost;
	}
	
	@Value("${irrigation.port}")
	public static String getIrrigationPort() {
		return irrigationPort;
	}
	
	@Value("${criticalTemp}")
	public static int getCriticalTemp() {
		return criticalTemp;
	}
	
	@Value("${criticalHumidity}")
	public static int getCriticalHumidity() {
		return criticalHumidity;
	}
	
	@Value("${criticalsoilMoisture}")
	public static int getCriticalsoilMoisture() {
		return criticalsoilMoisture;
	}
	
	@Value("${criticalPrecipitation}")
	public static int getCriticalPrecipitation() {
		return criticalPrecipitation;
	}

 
	@Value("${intervall}")
	public void setintervall(String intervall) {
		logger.info("invervall set to: "+intervall);
		SensorWorkerConfig.intervall = intervall;
	}

	@Value("${targetNode.host}")
	public void settargetHost(String targetHost) {
		SensorWorkerConfig.targetHost = targetHost;
	}

	@Value("${targetNode.port}")
	public void settargetPort(String targetPort) {
		SensorWorkerConfig.targetPort = targetPort;
	}

	@Value("${targetNode.url}")
	public void setUrl(String url) {
		SensorWorkerConfig.url = url;
	}

	@Value("#{'${edge.endpoints}'.split(',')}")
	public void sethostnames(List<String> hostnames) {
		SensorWorkerConfig.hostnames = hostnames;
	}

	@Value("${criticalTemp}")
	public void setCriticalTemp(int criticalTemp) {
  		logger.info("Ciritcal Temp set to: " + criticalTemp);
		SensorWorkerConfig.criticalTemp = criticalTemp;
	}

	@Value("${criticalHumidity}")
	public void setCriticalHumidity(int criticalHumidity) {
		logger.info("Ciritcal Humidity set to: " + criticalHumidity);
		SensorWorkerConfig.criticalHumidity = criticalHumidity;
	}

	@Value("${criticalsoilMoisture}")
	public void setCriticalsoilMoisture(int criticalsoilMoisture) {
		logger.info("Ciritcal soil moisture set to: " + criticalsoilMoisture);
		SensorWorkerConfig.criticalsoilMoisture = criticalsoilMoisture;
	}

	@Value("${criticalPrecipitation}")
	public void setCriticalPrecipitation(int criticalPrecipitation) {
		logger.info("Ciritcal precipitation set to: " + criticalPrecipitation);
		SensorWorkerConfig.criticalPrecipitation = criticalPrecipitation;
	}
	
	@Value("${irrigation.host}")
	public void setIrrigationHost(String irrigationHost) {
		SensorWorkerConfig.irrigationHost = irrigationHost;
	}
	
	@Value("${irrigation.port}")
	public void setIrrigationPort(String irrigationPort) {
		SensorWorkerConfig.irrigationPort = irrigationPort;
	}
 
}
