package com.example.mongodbdemo.service;

import com.example.mongodbdemo.controller.CloudController;
import com.example.mongodbdemo.pojo.SensorData;
import com.example.mongodbdemo.repository.SensorDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CloudService {

    Logger logger = LoggerFactory.getLogger(CloudController.class);

    @Autowired
    private SensorDataRepository repository;

    public void writeJsonToDatabase(SensorData data){
        logger.info("write sensor data "+ data +" to database");
        repository.save(data);
    }

}
