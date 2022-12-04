package com.glowingsoft.carplaterecognizer.Entity;

public class Pay {
    String car_model;
    String car_plate;
    String date;
    String payment;
    String amount;
    String name;
    String distance;
    String state;

    public Pay() {
    }

    public Pay(String car_model, String car_plate, String date, String payment, String amount, String name, String distance, String state) {
        this.car_model = car_model;
        this.car_plate = car_plate;
        this.date = date;
        this.payment = payment;
        this.amount = amount;
        this.name = name;
        this.distance = distance;
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

}
