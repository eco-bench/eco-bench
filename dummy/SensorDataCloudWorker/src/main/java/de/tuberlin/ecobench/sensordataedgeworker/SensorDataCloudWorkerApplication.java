package de.tuberlin.ecobench.sensordataedgeworker;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication
public class SensorDataCloudWorkerApplication implements ApplicationRunner {
	
    private static final Logger logger = LoggerFactory.getLogger(SensorDataCloudWorkerApplication.class);

	public static void main(String[] args) {
        
		SpringApplication.run(SensorDataCloudWorkerApplication.class, args);
	}
	
	//Quelle: https://memorynotfound.com/spring-boot-passing-command-line-arguments-example/
	 @Override
	    public void run(ApplicationArguments args) throws Exception {
	        logger.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
	        logger.info("NonOptionArgs: {}", args.getNonOptionArgs());
	        logger.info("OptionNames: {}", args.getOptionNames());

	        for (String name : args.getOptionNames()){
	            logger.info("arg-" + name + "=" + args.getOptionValues(name));
	        }
 	    }
	}
 
