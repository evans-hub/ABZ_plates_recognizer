package com.glowingsoft.carplaterecognizer.Entity;

public class Model_data {
    private String date;
    private String imageUrl;
    private int xValue;
    private int yValue;

    public Model_data(int xValue2, int yValue2, String date2, String imageUrl2) {
        this.xValue = xValue2;
        this.yValue = yValue2;
        this.date = date2;
        this.imageUrl = imageUrl2;
    }

    public Model_data() {
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl2) {
        this.imageUrl = imageUrl2;
    }

    public int getxValue() {
        return this.xValue;
    }

    public void setxValue(int xValue2) {
        this.xValue = xValue2;
    }

    public int getyValue() {
        return this.yValue;
    }

    public void setyValue(int yValue2) {
        this.yValue = yValue2;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String date2) {
        this.date = date2;
    }
}
