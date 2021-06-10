package de.tuberlin.ecobench.edgeirrigationsystem.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
 
@Component
public class IrrigationProperties {
	
	private static int logStorageLimit;
	private static String benchmarkEndPointHost;
	private static String benchmarkEndpointPort;
	private static String benchmarkEndpointURL;
	
    private static final Logger log = LoggerFactory.getLogger(IrrigationProperties.class);

 

	@Value("${logStorageLimit}")
	public void setlogStorageLimit(int logStorageLimit) {
		log.info("LOGSOTRAGELIMIT adjusted to " + logStorageLimit);
		IrrigationProperties.logStorageLimit = logStorageLimit;
	}

	@Value("${benchmarkEndPointHost}")
	public void setBenchmarkEndPointHost(String benchmarkEndPointHost) {
		log.info("benchmarkEndPointHost adjusted to " + benchmarkEndPointHost);
		IrrigationProperties.benchmarkEndPointHost = benchmarkEndPointHost;
	}

	@Value("${benchmarkEndpointPort}")
	public void setBenchmarkEndpointPort(String benchmarkEndpointPort) {
		log.info("benchmarkEndpointPort adjusted to " + benchmarkEndpointPort);
		IrrigationProperties.benchmarkEndpointPort = benchmarkEndpointPort;
	}

	@Value("${benchmarkEndpointURL}")
	public void setBenchmarkEndpointURL(String benchmarkEndpointURL) {
		log.info("benchmarkEndpointURL adjusted to " + benchmarkEndpointURL);
		IrrigationProperties.benchmarkEndpointURL = benchmarkEndpointURL;
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
	
	
}
