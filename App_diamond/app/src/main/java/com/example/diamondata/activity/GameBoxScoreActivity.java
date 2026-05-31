package com.example.diamondata.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondata.DiamondApiService;
import com.example.diamondata.R;
import com.example.diamondata.adapter.GameRosterAdapter;
import com.example.diamondata.models.FielderStat;
import com.example.diamondata.models.Game;
import com.example.diamondata.models.PitcherStat;
import com.example.diamondata.models.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameBoxScoreActivity extends AppCompatActivity {

    private int gameId;
    private int teamId;
    private DiamondApiService apiService;
    private RecyclerView rvGameRoster;

    // TRUCO: Aquí guardaremos los IDs de los jugadores que ya rellenaste
    private Set<Integer> jugadoresGuardados = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_box_score);

        gameId = getIntent().getIntExtra("GAME_ID", -1);
        teamId = getIntent().getIntExtra("TEAM_ID", -1);

        apiService = RetrofitClient.getRetrofitInstance().create(DiamondApiService.class);
        rvGameRoster = findViewById(R.id.rvGameRoster);
        rvGameRoster.setLayoutManager(new LinearLayoutManager(this));

        cargarRosterDelPartido();

        Button btnSubmitGame = findViewById(R.id.btnSubmitGame);
        btnSubmitGame.setOnClickListener(v -> finalizarPartido());
    }

    private void cargarRosterDelPartido() {
        apiService.getTeamDetail(teamId).enqueue(new Callback<com.example.diamondata.TeamDetailResponse>() {
            @Override
            public void onResponse(Call<com.example.diamondata.TeamDetailResponse> call, Response<com.example.diamondata.TeamDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> roster = response.body().getPlayers();
                    GameRosterAdapter adapter = new GameRosterAdapter(roster, player -> {
                        // SOLUCIÓN PROBLEMA 2: Evitar que el usuario lo rellene varias veces
                        if (jugadoresGuardados.contains(player.getId())) {
                            Toast.makeText(GameBoxScoreActivity.this, "Ya añadiste los datos de " + player.getName(), Toast.LENGTH_SHORT).show();
                        } else {
                            abrirDialogoEstadisticas(player);
                        }
                    });
                    rvGameRoster.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<com.example.diamondata.TeamDetailResponse> call, Throwable t) {
                Toast.makeText(GameBoxScoreActivity.this, "Error cargando roster", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirDialogoEstadisticas(Player player) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_quick_stats);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvName = dialog.findViewById(R.id.tvStatsPlayerName);
        tvName.setText(player.getName() + " #" + player.getNumber());

        // Atención: hemos cambiado de GridLayout a LinearLayout en el XML
        LinearLayout layoutFielder = dialog.findViewById(R.id.layoutFielderStats);
        LinearLayout layoutPitcher = dialog.findViewById(R.id.layoutPitcherStats);
        Button btnSave = dialog.findViewById(R.id.btnSavePlayerStats);

        boolean isPitcher = "P".equals(player.getPositionType());
        layoutPitcher.setVisibility(isPitcher ? View.VISIBLE : View.GONE);
        layoutFielder.setVisibility(isPitcher ? View.GONE : View.VISIBLE);

        btnSave.setOnClickListener(v -> {
            if (isPitcher) {
                guardarDatosPitcher(player.getId(), dialog);
            } else {
                guardarDatosFielder(player.getId(), dialog);
            }
        });

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void guardarDatosFielder(int playerId, Dialog dialog) {
        FielderStat stat = new FielderStat();
        stat.setPlayer(playerId);
        stat.setGame(gameId);

        // 1. Extraemos TB y BB primero para calcular los juegos
        int tb = obtenerValor(dialog, R.id.etFielderTB);
        int bb = obtenerValor(dialog, R.id.etFielderBB);

        stat.setTb(tb);
        stat.setBb(bb);

        // 👇 LÓGICA AUTOMÁTICA: Si bateó o recibió base por bolas, suma 1 juego (J) 👇
        if (tb > 0 || bb > 0) {
            stat.setJ(1);
        } else {
            stat.setJ(0);
        }

        // Mapeamos el resto de las estadísticas normalmente
        stat.setC(obtenerValor(dialog, R.id.etFielderC));
        stat.setH(obtenerValor(dialog, R.id.etFielderH));
        stat.setH2(obtenerValor(dialog, R.id.etFielderH2));
        stat.setH3(obtenerValor(dialog, R.id.etFielderH3));
        stat.setH4(obtenerValor(dialog, R.id.etFielderH4));
        stat.setCis(obtenerValor(dialog, R.id.etFielderCIS));
        stat.setBbi(obtenerValor(dialog, R.id.etFielderBBI));
        stat.setSo(obtenerValor(dialog, R.id.etFielderSO));
        stat.setBr(obtenerValor(dialog, R.id.etFielderBR));
        stat.setAr(obtenerValor(dialog, R.id.etFielderAR));

        apiService.saveFielderStat(stat).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Toast.makeText(GameBoxScoreActivity.this, "¡Guardado!", Toast.LENGTH_SHORT).show();
                    jugadoresGuardados.add(playerId);
                    dialog.dismiss();
                }else {
                    // 👇 TRUCO ACTIVADO 👇
                    try {
                        String errorDjango = response.errorBody().string();
                        Toast.makeText(GameBoxScoreActivity.this, "Django dice: " + errorDjango, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override public void onFailure(Call<ResponseBody> c, Throwable t) {
                Toast.makeText(GameBoxScoreActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void guardarDatosPitcher(int playerId, Dialog dialog) {
        PitcherStat stat = new PitcherStat();
        stat.setPlayer(playerId);
        stat.setGame(gameId);

        // 1. Extraemos los Innings Lanzados (IL) a prueba de balas
        EditText etIL = dialog.findViewById(R.id.etPitcherIL);

        // TRUCO: Cambiamos comas por puntos para que Django no se queje
        String valIL = etIL.getText().toString().replace(",", ".").trim();
        double il = valIL.isEmpty() ? 0.0 : Double.parseDouble(valIL);

        stat.setIl(il);

        // LÓGICA AUTOMÁTICA: Si lanzó más de 0 innings, suma 1 juego (J)
        if (il > 0.0) {
            stat.setJ(1);
        } else {
            stat.setJ(0);
        }

        // Mapeamos el resto de las estadísticas enteras
        stat.setA(obtenerValor(dialog, R.id.etPitcherA));
        stat.setG(obtenerValor(dialog, R.id.etPitcherG));
        stat.setP(obtenerValor(dialog, R.id.etPitcherP));
        stat.setJc(obtenerValor(dialog, R.id.etPitcherJC));
        stat.setBl(obtenerValor(dialog, R.id.etPitcherBL));
        stat.setSv(obtenerValor(dialog, R.id.etPitcherSV));
        stat.setH(obtenerValor(dialog, R.id.etPitcherH));
        stat.setCl(obtenerValor(dialog, R.id.etPitcherCL));
        stat.setSo(obtenerValor(dialog, R.id.etPitcherSO));

        apiService.savePitcherStat(stat).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Toast.makeText(GameBoxScoreActivity.this, "¡Pitcher Guardado!", Toast.LENGTH_SHORT).show();
                    jugadoresGuardados.add(playerId);
                    dialog.dismiss();
                } else {
                    // Seguimos dejando el chivato por si acaso
                    try {
                        String errorDjango = response.errorBody().string();
                        Toast.makeText(GameBoxScoreActivity.this, "Django dice: " + errorDjango, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override public void onFailure(Call<ResponseBody> c, Throwable t) {
                Toast.makeText(GameBoxScoreActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Función auxiliar para leer los EditText y devolver un 0 si están vacíos
    private int obtenerValor(Dialog dialog, int editTextId) {
        EditText et = dialog.findViewById(editTextId);
        String val = et.getText().toString();
        return val.isEmpty() ? 0 : Integer.parseInt(val);
    }

    private void finalizarPartido() {
        EditText etMyScore = findViewById(R.id.etMyTeamScore);
        EditText etOpScore = findViewById(R.id.etOpponentScore);

        // Creamos un objeto partido solo con los campos necesarios para actualizar el resultado
        Game gameUpdate = new Game(null, null, null, null);
        gameUpdate.setTeamScore(etMyScore.getText().toString().isEmpty() ? 0 : Integer.parseInt(etMyScore.getText().toString()));
        gameUpdate.setOpponentScore(etOpScore.getText().toString().isEmpty() ? 0 : Integer.parseInt(etOpScore.getText().toString()));

        // Enviamos la petición PUT a Django
        apiService.updateGame(gameId, gameUpdate).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GameBoxScoreActivity.this, "¡Box Score Enviado!", Toast.LENGTH_LONG).show();

                    // SOLUCIÓN PROBLEMA 3: Esto cierra la pantalla y te devuelve a los partidos
                    finish();
                }else {
                    // 👇 TRUCO ACTIVADO 👇
                    try {
                        String errorDjango = response.errorBody().string();
                        Toast.makeText(GameBoxScoreActivity.this, "Django dice: " + errorDjango, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(GameBoxScoreActivity.this, "Error guardando el partido", Toast.LENGTH_SHORT).show();
            }
        });
    }
}