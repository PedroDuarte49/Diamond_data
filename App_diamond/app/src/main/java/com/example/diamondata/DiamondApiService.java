package com.example.diamondata;

import com.example.diamondata.models.FielderStat;
import com.example.diamondata.models.Game;
import com.example.diamondata.models.PitcherStat;
import com.example.diamondata.models.Player;
import com.example.diamondata.models.Team;

import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    // --- PARTIDOS ---
    @GET("api/games/")
    Call<List<Game>> getAllGames();

    @POST("api/games/")
    Call<ResponseBody> createGame(@Body Game game);

    @PUT("api/games/{id}/")
    Call<ResponseBody> updateGame(@Path("id") int gameId, @Body Game game);

    @GET("api/stats/game/{game_id}/player/{player_id}/")
    Call<okhttp3.ResponseBody> getPlayerGameStats(@Path("game_id") int gameId, @Path("player_id") int playerId);

    // Obtener estadísticas de un jugador por temporada
    @GET("stats/season/{player_id}/{season}/")
    Call<okhttp3.ResponseBody> getPlayerSeasonStats(@Path("player_id") int playerId, @Path("season") String season);

    // 3. Para traer el detalle del partido (para el marcador)
    @GET("api/games/{id}/")
    Call<Game> getGameDetail(@Path("id") int gameId);
}