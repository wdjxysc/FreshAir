package com.rthc.freshair.util;

import com.rthc.freshair.MyApplication;
import com.rthc.freshair.R;

/**
 * Created by Administrator on 2016/7/29.
 *
 * @author wdjxysc
 */
public class AirQuality {

    static Float[] valueAxisYPm25 = {0f, 35f, 75f, 115f, 150f, 250f, 500f, 600f};
    static String[] lvlStrPm25 = {"优", "良", "轻度污染", "中度污染", "重度污染", "严重污染", "爆表"};


    static Float[] valueAxisYPm10 = {0f, 50f, 150f, 250f, 350f, 420f, 600f};
    static String[] lvlStrPm10 = {"优", "良", "轻度污染", "中度污染", "重度污染", "严重污染", "爆表"};


    static Float[] valueAxisYTvoc = {0f, 0.6f, 1.0f, 1.6f};
    static String[] lvlStrTvoc = {"优", "良", "轻度污染", "中度污染"};

    static Float[] valueAxisYHcho = {0f, 0.03f, 0.08f, 0.16f, 0.3f, 0.8f};
    static String[] lvlStrHcho = {"优", "良", "轻度污染", "中度污染", "重度污染", "严重污染"};


    static int[] colors = {
            MyApplication.getInstance().getResources().getColor(R.color.you),
            MyApplication.getInstance().getResources().getColor(R.color.liang),
            MyApplication.getInstance().getResources().getColor(R.color.qingduwuran),
            MyApplication.getInstance().getResources().getColor(R.color.midwuran),
            MyApplication.getInstance().getResources().getColor(R.color.heavywuran),
            MyApplication.getInstance().getResources().getColor(R.color.yanzhongwuran),
            MyApplication.getInstance().getResources().getColor(R.color.baobiao)};

    public enum AirQualityType {
        Pm25,
        Pm10,
        Hcho,
        Tvoc
    }

    public static DataShow getLvl(AirQualityType airQualityType, float value) {
        DataShow dataShow = new DataShow();

        String result = "良";

        Float[] valueAxisY;
        String[] lvlStrs;

        switch (airQualityType) {
            case Pm25:
                valueAxisY = valueAxisYPm25;
                lvlStrs = lvlStrPm25;
                break;
            case Pm10:
                valueAxisY = valueAxisYPm10;
                lvlStrs = lvlStrPm10;
                break;
            case Hcho:
                valueAxisY = valueAxisYTvoc;
                lvlStrs = lvlStrTvoc;
                break;
            case Tvoc:
                valueAxisY = valueAxisYHcho;
                lvlStrs = lvlStrHcho;
                break;
            default:
                valueAxisY = valueAxisYPm25;
                lvlStrs = lvlStrPm25;
                break;
        }


        dataShow = getLvlName(value, valueAxisY, lvlStrs, colors);

        return dataShow;
    }

    /**
     * 根据数值 获取污染等级名称
     *
     * @param value
     * @param lvlData 等级数据
     * @param lvlStr  等级名称
     * @return
     */
    static DataShow getLvlName(float value, Float[] lvlData, String[] lvlStr, int[] colors) {
        DataShow dataShow = new DataShow();
        dataShow.setValue(value);
        dataShow.setColor(colors[0]);
        dataShow.setLvlStr(lvlStr[0]);
        if (value > lvlData[lvlData.length - 1]) {
            dataShow.setLvlStr(lvlStr[lvlStr.length - 1]);
            dataShow.setColor(colors[lvlStr.length - 1]);
        }

        for (int i = 0; i < lvlData.length; i++) {
            if (value < lvlData[i]) {
                dataShow.setLvlStr(lvlStr[i - 1]);
                dataShow.setColor(colors[i - 1]);
                break;
            }
        }
        return dataShow;
    }

    public static class DataShow {
        int color;
        float value;
        String lvlStr;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public String getLvlStr() {
            return lvlStr;
        }

        public void setLvlStr(String lvlStr) {
            this.lvlStr = lvlStr;
        }
    }
}
