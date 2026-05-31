package com.example.diamondata.models;

import com.google.gson.annotations.SerializedName;

public class FielderStat {
    @SerializedName("player_id")
    private int player; // ID del jugador
    @SerializedName("game_id")
    private int game;   // ID del partido

    private int j;
    private int tb;
    private int c;
    private int h;
    private int h2;
    private int h3;
    private int h4;
    private int cis;
    private int bb;
    private int bbi;
    private int so;
    private int br;
    private int ar;
    private double pro; // Promedio (AVG)

    // Constructor vacío requerido por GSON para la deserialización
    public FielderStat() {}

    // Getters y Setters para capturar y enviar la información
    public int getPlayer() { return player; }
    public void setPlayer(int player) { this.player = player; }
    public int getGame() { return game; }
    public void setGame(int game) { this.game = game; }
    public int getJ() { return j; }
    public void setJ(int j) { this.j = j; }
    public int getTb() { return tb; }
    public void setTb(int tb) { this.tb = tb; }
    public int getC() { return c; }
    public void setC(int c) { this.c = c; }
    public int getH() { return h; }
    public void setH(int h) { this.h = h; }
    public int getH2() { return h2; }
    public void setH2(int h2) { this.h2 = h2; }
    public int getH3() { return h3; }
    public void setH3(int h3) { this.h3 = h3; }
    public int getH4() { return h4; }
    public void setH4(int h4) { this.h4 = h4; }
    public int getCis() { return cis; }
    public void setCis(int cis) { this.cis = cis; }
    public int getBb() { return bb; }
    public void setBb(int bb) { this.bb = bb; }
    public int getBbi() { return bbi; }
    public void setBbi(int bbi) { this.bbi = bbi; }
    public int getSo() { return so; }
    public void setSo(int so) { this.so = so; }
    public int getBr() { return br; }
    public void setBr(int br) { this.br = br; }
    public int getAr() { return ar; }
    public void setAr(int ar) { this.ar = ar; }
    public double getPro() { return pro; }
    public void setPro(double pro) { this.pro = pro; }
}