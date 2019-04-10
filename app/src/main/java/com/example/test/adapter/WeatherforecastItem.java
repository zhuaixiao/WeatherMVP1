package com.example.test.adapter;

public class WeatherforecastItem {
    private String cond;
    private String tmpmax;
    private String tmpmin;
    private String date;

    public WeatherforecastItem() {

    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getTmpmax() {
        return tmpmax;
    }

    public void setTmpmax(String tmpmax) {
        this.tmpmax = tmpmax;
    }

    public String getTmpmin() {
        return tmpmin;
    }

    public void setTmpmin(String tmpmin) {
        this.tmpmin = tmpmin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
