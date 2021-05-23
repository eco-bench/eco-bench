package com.example.mongodbdemo.pojo;

@Data
public class SensorData {

	
    private String sensorID;
    private Double value;
 

    public Double getValue() {
        return this.value;
    }

    public void setValue(Double measurement) {
        this.value = measurement;
    }
}
