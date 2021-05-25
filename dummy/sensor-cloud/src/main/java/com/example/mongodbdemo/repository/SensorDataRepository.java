package com.example.mongodbdemo.repository;

import com.example.mongodbdemo.pojo.SensorData;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SensorDataRepository extends MongoRepository<SensorData, String> {
}
