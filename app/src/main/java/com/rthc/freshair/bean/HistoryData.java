package com.rthc.freshair.bean;

/**
 * Created by Administrator on 2016/8/10.
 *
 * @author wdjxysc
 */
public class HistoryData {
    /**
     * id : 0135a24704dd4b568406c1a39f257265
     * hcho : 10.280
     * time : 1469601859000
     * tvoc : 9.650
     * pm10 :
     * pm25 : 99.60
     * temperature : 28.60
     */

    private String id;
    private String hcho;
    private Long time;
    private String tvoc;
    private String pm10;
    private String pm25;
    private String temperature;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHcho() {
        return hcho;
    }

    public void setHcho(String hcho) {
        this.hcho = hcho;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getTvoc() {
        return tvoc;
    }

    public void setTvoc(String tvoc) {
        this.tvoc = tvoc;
    }

    public String getPm10() {
        return pm10;
    }

    public void setPm10(String pm10) {
        this.pm10 = pm10;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }
}
