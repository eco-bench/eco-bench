package de.tuberlin.ecobench.sensordataedgeworker;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SpringBootApplication
public class SensordataedgeworkerApplication implements ApplicationRunner {
	
    private static final Logger logger = LoggerFactory.getLogger(SensordataedgeworkerApplication.class);

	public static void main(String[] args) {
        
		SpringApplication.run(SensordataedgeworkerApplication.class, args);
	}
	
 	 @Override
	    public void run(ApplicationArguments args) throws Exception {
	        logger.info("Edge Node started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
	        for (String name : args.getOptionNames()){
	            logger.info("arg-" + name + "=" + args.getOptionValues(name));                 
	        }
 	    }
	}
 
