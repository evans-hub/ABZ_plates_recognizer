package com.glowingsoft.carplaterecognizer.Entity;

public class staff {
    String car_model;
    String car_plate;
    String county;
    String distance;
    String email_address;
    String id_number;
    String name;
    String phone_number;
    String status;
    String amount="0";
    String payment;
    int times;
    String state;

    public staff() {
    }

    public staff(String id_number, String name,String phone_number,String county,String email_address, String car_plate, String car_model, String distance, String status, String amount, String payment, int times, String state) {
        this.car_model = car_model;
        this.car_plate = car_plate;
        this.county = county;
        this.distance = distance;
        this.email_address = email_address;
        this.id_number = id_number;
        this.name = name;
        this.phone_number = phone_number;
        this.status = status;
        this.amount = amount;
        this.payment = payment;
        this.times = times;
        this.state = state;
    }

    public int getTimes() {
        return times;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setTimes(int times) {
        this.times = times;
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

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getId_number() {
        return id_number;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }
}
