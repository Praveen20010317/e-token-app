package com.example.cafetea;

import java.io.Serializable;

public class Orders implements Serializable {
    public String order_no;
    public String id;
    public String name;
    public String coffee;
    public String tea;
    public String snacks;
    public String status;
    public String description;
    public String time;
    public String amount;
    public String dept;
    public String uid;
    public String locationK;
    public String category;
    public String key;

    public Orders(String order_no,String id,
                  String name, String coffee,
                  String tea, String snacks,
                  String status, String description,
                  String time, String amount, String dept, String location, String uid, String category) {
        this.order_no = order_no;
        this.id = id;
        this.name = name;
        this.coffee = coffee;
        this.tea = tea;
        this.snacks = snacks;
        this.status = status;
        this.description = description;
        this.time = time;
        this.amount = amount;
        this.dept = dept;
        this.locationK = location;
        this.uid = uid;
        this.category = category;
    }
    public Orders() {
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getLocationK() {
        return locationK;
    }

    public void setLocationK(String locationK) {
        this.locationK = locationK;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoffee() {
        return coffee;
    }

    public void setCoffee(String coffee) {
        this.coffee = coffee;
    }

    public String getTea() {
        return tea;
    }

    public void setTea(String tea) {
        this.tea = tea;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSnacks() {
        return snacks;
    }

    public void setSnacks(String snacks) {
        this.snacks = snacks;
    }
}
