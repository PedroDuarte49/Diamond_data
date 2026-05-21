package com.example.diamondata;

import com.example.diamondata.models.Player;

import java.util.List;

public class TeamDetailResponse {
    private int id;
    private String name;
    private String city;
    private String coach;
    private List<Player> players; // Mapea la lista del roster

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCity() { return city; }
    public String getCoach() { return coach; }
    public List<Player> getPlayers() { return players; }
}
