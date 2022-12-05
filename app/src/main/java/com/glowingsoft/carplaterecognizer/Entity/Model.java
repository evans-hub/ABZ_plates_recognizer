package com.glowingsoft.carplaterecognizer.Entity;

public class Model {
    String car_model;
    String car_plate;
    String millis;
    String name;
    String time_in;
    String type;

    public Model() {
    }

    public Model(String name, String car_plate,String car_model, String time_in,String type,String millis) {
        this.car_model = car_model;
        this.car_plate = car_plate;
        this.millis = millis;
        this.name = name;
        this.time_in = time_in;
        this.type = type;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getCar_plate() {
        return car_plate;
    }

    public void setCar_plate(String car_plate) {
        this.car_plate = car_plate;
    }

    public String getMillis() {
        return millis;
    }

    public void setMillis(String millis) {
        this.millis = millis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime_in() {
        return time_in;
    }

    public void setTime_in(String time_in) {
        this.time_in = time_in;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}