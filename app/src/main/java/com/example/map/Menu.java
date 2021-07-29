package com.example.map;

public class Menu {
    String name;
    String add;
    String num;
    String opentime;
    String endtime;
    String breakT;
    String ima;
    String ima2;
    String recommend;
    String holiday;
    String cate;
    Double lat;
    Double lng;

    public Menu(String name, String add, String num, String opentime, String endtime, String breakT, String ima, String ima2, String recommend, String holiday, String cate) {
        this.name = name;
        this.add = add;
        this.num = num;
        this.opentime = opentime;
        this.endtime = endtime;
        this.breakT = breakT;
        this.ima = ima;
        this.ima2 = ima2;
        this.recommend = recommend;
        this.holiday = holiday;
        this.cate = cate;
    }

    public Menu(String name, String add, String num, String opentime, String endtime, String breakT, String ima, String ima2, String recommend, String holiday, Double lat, Double lng, String cate) {
        this.name = name;
        this.add = add;
        this.num = num;
        this.opentime = opentime;
        this.endtime = endtime;
        this.breakT = breakT;
        this.recommend = recommend;
        this.holiday = holiday;
        this.ima = ima;
        this.ima2 = ima2;
        this.lat = lat;
        this.lng = lng;
        this.cate = cate;
    }


    public String getHoliday() {
        return holiday;
    }

    public void setHoliday(String holiday) {
        this.holiday = holiday;
    }


    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
    public String getRecommend() {
        return recommend;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }
    public String getIma() {
        return ima;
    }

    public void setIma(String ima) {
        this.ima = ima;
    }

    public String getIma2() {
        return ima2;
    }

    public void setIma2(String ima2) {
        this.ima2 = ima2;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdd() {
        return add;
    }

    public void setAdd(String add) {
        this.add = add;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getOpentime() {
        return opentime;
    }

    public void setOpentime(String opentime) {
        this.opentime = opentime;
    }

    public String getBreakT() {
        return breakT;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public void setBreakT(String breakT) {
        this.breakT = breakT;
    }

}
