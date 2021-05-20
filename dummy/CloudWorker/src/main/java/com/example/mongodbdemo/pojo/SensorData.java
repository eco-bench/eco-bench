package com.example.mongodbdemo.pojo;

public class SensorData {


    /**
     *  sensorID automatisch generieren
     */
    private final static String sensorID = "temperature-"+(int)(Math.random()*100);
    private Double measurement;

    /**
     * für testZwecke
     * @return
     */
    public static String getSensorID() {
        return sensorID;
    }

    public Double getMeasurement() {
        return measurement;
    }

    public void setMeasurement(Double measurement) {
        this.measurement = measurement;
    }
}
