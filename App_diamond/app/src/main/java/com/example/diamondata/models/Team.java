package com.example.diamondata.models;

import com.google.gson.annotations.SerializedName;

public class Team {
    private int id;
    private String name;
    private String city;

    // Esto obliga a Android a llamar a la etiqueta "coach" cuando lo envíe a Django
    @SerializedName("coach")
    private String coach_name;

    public Team(String name, String city, String coach_name) {
        this.name = name;
        this.city = city;
        this.coach_name = coach_name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getCoachName() { return coach_name; }
}