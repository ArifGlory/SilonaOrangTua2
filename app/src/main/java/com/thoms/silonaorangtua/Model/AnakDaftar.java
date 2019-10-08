package com.thoms.silonaorangtua.Model;

public class AnakDaftar {
    public String nama,username,email,password,latitude,longitude,isSharing,lasttime, imageUrl, userid;

    public AnakDaftar(String nama, String username, String email, String password, String latitude, String longitude, String isSharing, String lasttime, String imageUrl, String userid) {
        this.nama = nama;
        this.username = username;
        this.email = email;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isSharing = isSharing;
        this.lasttime = lasttime;
        this.imageUrl = imageUrl;
        this.userid = userid;

    }
    public AnakDaftar()
    {}



}
