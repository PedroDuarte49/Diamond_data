package com.example.diamondata;

import com.example.diamondata.models.FielderStat;
import com.example.diamondata.models.PitcherStat;
import com.example.diamondata.models.Player;
import com.example.diamondata.models.Team;

import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface DiamondApiService {

    // --- EQUIPOS ---
    @GET("api/teams/")
    Call<List<Team>> getAllTeams();

    @POST("api/teams/")
    Call<ResponseBody> createTeam(@Body Team team);

    @GET("api/teams/{id}/")
    Call<TeamDetailResponse> getTeamDetail(@Path("id") int teamId);

    // --- JUGADORES ---
    @POST("api/players/")
    Call<ResponseBody> createPlayer(@Body Player player);

    // --- ESTADÍSTICAS (Múltiples POSTs para el Box Score) ---
    @POST("api/stats/fielder/")
    Call<ResponseBody> saveFielderStat(@Body FielderStat stats);

    @POST("api/stats/pitcher/")
    Call<ResponseBody> savePitcherStat(@Body PitcherStat stats);
}