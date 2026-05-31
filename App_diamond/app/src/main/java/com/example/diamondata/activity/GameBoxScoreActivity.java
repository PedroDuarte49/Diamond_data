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
        cargarDatosPartido();

        Button btnSubmitGame = findViewById(R.id.btnSubmitGame);
        btnSubmitGame.setOnClickListener(v -> finalizarPartido());
        Button btnDelete = findViewById(R.id.btnDeleteGame);

        btnDelete.setOnClickListener(v -> {
            // Llamamos a la función de confirmación pasando el ID del partido
            confirmarBorrado(gameId, "Partido");
        });
    }
    private void cargarDatosPartido() {
        // Asumiendo que tienes una función en tu API para traer el detalle de UN partido
        apiService.getGameDetail(gameId).enqueue(new Callback<Game>() {
            @Override
            public void onResponse(Call<Game> call, Response<Game> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Game game = response.body();
                    EditText etMyScore = findViewById(R.id.etMyTeamScore);
                    EditText etOpScore = findViewById(R.id.etOpponentScore);

                    if (game.getTeamScore() != null) etMyScore.setText(String.valueOf(game.getTeamScore()));
                    if (game.getOpponentScore() != null) etOpScore.setText(String.valueOf(game.getOpponentScore()));
                }
            }
            @Override public void onFailure(Call<Game> call, Throwable t) {}
        });
    }
    private void cargarRosterDelPartido() {
        apiService.getTeamDetail(teamId).enqueue(new Callback<com.example.diamondata.TeamDetailResponse>() {
            @Override
            public void onResponse(Call<com.example.diamondata.TeamDetailResponse> call, Response<com.example.diamondata.TeamDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> roster = response.body().getPlayers();

                    GameRosterAdapter adapter = new GameRosterAdapter(roster, player -> {
                        // LA MAGIA: Antes de abrir el diálogo, preguntamos a Django si hay datos
                        apiService.getPlayerGameStats(gameId, player.getId()).enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> c, Response<ResponseBody> r) {
                                if (r.isSuccessful() && r.body() != null) {
                                    try {
                                        // ¡TIENE DATOS! Abrimos en MODO EDICIÓN
                                        org.json.JSONObject stats = new org.json.JSONObject(r.body().string());
                                        abrirDialogoEstadisticas(player, stats);
                                    } catch (Exception e) {
                                        abrirDialogoEstadisticas(player, null);
                                    }
                                } else {
                                    // 404 NO TIENE DATOS. Abrimos en MODO NUEVO
                                    abrirDialogoEstadisticas(player, null);
                                }
                            }
                            @Override
                            public void onFailure(Call<ResponseBody> c, Throwable t) {
                                abrirDialogoEstadisticas(player, null);
                            }
                        });
                    });
                    rvGameRoster.setAdapter(adapter);
                }
            }
            @Override public void onFailure(Call<com.example.diamondata.TeamDetailResponse> call, Throwable t) { }
        });
    }

    private void abrirDialogoEstadisticas(Player player, org.json.JSONObject statsPreexistentes) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_quick_stats);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvName = dialog.findViewById(R.id.tvStatsPlayerName);
        tvName.setText(player.getName() + " #" + player.getNumber());

        LinearLayout layoutFielder = dialog.findViewById(R.id.layoutFielderStats);
        LinearLayout layoutPitcher = dialog.findViewById(R.id.layoutPitcherStats);
        Button btnSave = dialog.findViewById(R.id.btnSavePlayerStats);

        boolean isPitcher = "P".equals(player.getPositionType());
        layoutPitcher.setVisibility(isPitcher ? View.VISIBLE : View.GONE);
        layoutFielder.setVisibility(isPitcher ? View.GONE : View.VISIBLE);

        // 👇 MODO EDICIÓN: Rellenamos automáticamente y cambiamos el botón 👇
        if (statsPreexistentes != null) {
            btnSave.setText("ACTUALIZAR STATS");
            if (isPitcher) {
                preRellenar(dialog, R.id.etPitcherA, statsPreexistentes, "a");
                preRellenar(dialog, R.id.etPitcherG, statsPreexistentes, "g");
                preRellenar(dialog, R.id.etPitcherP, statsPreexistentes, "p");
                preRellenar(dialog, R.id.etPitcherJC, statsPreexistentes, "jc");
                preRellenar(dialog, R.id.etPitcherBL, statsPreexistentes, "bl");
                preRellenar(dialog, R.id.etPitcherSV, statsPreexistentes, "sv");
                preRellenar(dialog, R.id.etPitcherIL, statsPreexistentes, "il");
                preRellenar(dialog, R.id.etPitcherH, statsPreexistentes, "h");
                preRellenar(dialog, R.id.etPitcherCL, statsPreexistentes, "cl");
                preRellenar(dialog, R.id.etPitcherSO, statsPreexistentes, "so");
            } else {
                preRellenar(dialog, R.id.etFielderTB, statsPreexistentes, "tb");
                preRellenar(dialog, R.id.etFielderC, statsPreexistentes, "c");
                preRellenar(dialog, R.id.etFielderH, statsPreexistentes, "h");
                preRellenar(dialog, R.id.etFielderH2, statsPreexistentes, "h2");
                preRellenar(dialog, R.id.etFielderH3, statsPreexistentes, "h3");
                preRellenar(dialog, R.id.etFielderH4, statsPreexistentes, "h4");
                preRellenar(dialog, R.id.etFielderCIS, statsPreexistentes, "cis");
                preRellenar(dialog, R.id.etFielderBB, statsPreexistentes, "bb");
                preRellenar(dialog, R.id.etFielderBBI, statsPreexistentes, "bbi");
                preRellenar(dialog, R.id.etFielderSO, statsPreexistentes, "so");
                preRellenar(dialog, R.id.etFielderBR, statsPreexistentes, "br");
                preRellenar(dialog, R.id.etFielderAR, statsPreexistentes, "ar");
            }
        } else {
            btnSave.setText("GUARDAR STATS");
        }

        btnSave.setOnClickListener(v -> {
            if (isPitcher) { guardarDatosPitcher(player.getId(), dialog); }
            else { guardarDatosFielder(player.getId(), dialog); }
        });

        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    // Pequeña función de apoyo para rellenar los campos sin ensuciar el código
    private void preRellenar(Dialog dialog, int viewId, org.json.JSONObject stats, String key) {
        if (stats.has(key)) {
            EditText et = dialog.findViewById(viewId);
            String valor = stats.optString(key);

            // EL CAMBIO: quitamos la comprobación de !valor.equals("0")
            // Ahora si el valor es 0, lo escribirá en el EditText
            et.setText(valor);
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

    // 3. La función de confirmación (el "portero" que evita borrados accidentales)
    private void confirmarBorrado(int id, String tipo) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar " + tipo)
                .setMessage("¿Estás seguro de que quieres eliminar este partido? Se borrarán también sus estadísticas.")
                .setPositiveButton("Borrar", (dialog, which) -> ejecutarBorrado(id))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // 4. La función que ejecuta la llamada a Django
    private void ejecutarBorrado(int id) {
        apiService.deleteGame(id).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(Call<okhttp3.ResponseBody> call, Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(GameBoxScoreActivity.this, "Partido eliminado", Toast.LENGTH_SHORT).show();
                    finish(); // Cerramos la pantalla y volvemos atrás
                } else {
                    Toast.makeText(GameBoxScoreActivity.this, "Error al borrar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(GameBoxScoreActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}