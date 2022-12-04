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

    public Model(String car_plate2, String name2, String car_model2, String time_in2, String type2, String millis2) {
        this.car_plate = car_plate2;
        this.name = name2;
        this.car_model = car_model2;
        this.time_in = time_in2;
        this.type = type2;
        this.millis = millis2;
    }

    public String getCar_plate() {
        return this.car_plate;
    }

    public void setCar_plate(String car_plate2) {
        this.car_plate = car_plate2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getCar_model() {
        return this.car_model;
    }

    public void setCar_model(String car_model2) {
        this.car_model = car_model2;
    }

    public String getTime_in() {
        return this.time_in;
    }

    public void setTime_in(String time_in2) {
        this.time_in = time_in2;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type2) {
        this.type = type2;
    }

    public String getMillis() {
        return this.millis;
    }

    public void setMillis(String millis2) {
        this.millis = millis2;
    }
}