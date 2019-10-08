package com.thoms.silonaorangtua.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Rincian {
    public String Waktu, Latt, Longi;

    public String getWaktu() {
        return Waktu;
    }

    public void setWaktu(String waktu) {
        Waktu = Waktu;
    }

    public String getLatt() {
        return Latt;
    }

    public void setLatt(String latt) {
        Latt = Latt;
    }

    public String getLongi() {
        return Longi;
    }

    public void setLongi(String longi) {
        Longi = Longi;
    }

    public Rincian(String Waktu, String Latt, String Longi) {
        this.Waktu = Waktu;
        this.Latt = Latt;
        this.Longi = Longi;
    }

    public Rincian()
    {}
}
