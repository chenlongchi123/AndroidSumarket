package com.chen.supermarketmanagement.bean;

public class Goods {
    private int id;
    private String name;
    private double price;
    private int pnum;
    private String description;
    private String type;
    private String imgurl;
    private String odate;
    private double price2;

    public double getPrice2() {
        return price2;
    }

    public void setPrice2(double price2) {
        this.price2 = price2;
    }

    public String getOdate() {
        return odate;
    }
    public void setOdate(String odate) {
        this.odate = odate;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public int getPnum() {
        return pnum;
    }
    public void setPnum(int pnum) {
        this.pnum = pnum;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getImgurl() {
        return imgurl;
    }
    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }


}

