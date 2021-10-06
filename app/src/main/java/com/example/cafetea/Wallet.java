package com.example.cafetea;

public class Wallet {
    public String wallet;
    public String email;
    public String full_name;
    public String user_id;
    public String dept;
    public String uid;
    public String password;
    public String category;
    public String key;

    public Wallet(String wallet, String email, String full_name, String user_id, String dept, String uid, String key) {
        this.wallet = wallet;
        this.email = email;
        this.full_name = full_name;
        this.user_id = user_id;
        this.dept = dept;
        this.uid = uid;
        this.key = key;
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

    public String getDept() {
        return dept;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public Wallet() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }
}
