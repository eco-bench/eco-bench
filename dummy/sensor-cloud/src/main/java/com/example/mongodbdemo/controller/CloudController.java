package com.example.mongodbdemo.controller;

import com.example.mongodbdemo.pojo.SensorData;
import com.example.mongodbdemo.service.CloudService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.MediaType;
@Controller
@EnableScheduling
public class CloudController {

    Logger logger = LoggerFactory.getLogger(CloudController.class);

    @Autowired
    CloudService cloudService;

 	@PostMapping(path="/sensorData",produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity postSensorData(@RequestBody SensorData data) {
        cloudService.writeJsonToDatabase(data);
    	return new ResponseEntity<SensorData>(HttpStatus.OK);
    }

}
