package com.example.diamondata.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondata.DiamondApiService;
import com.example.diamondata.R;
import com.example.diamondata.TeamDetailResponse;
import com.example.diamondata.adapter.PlayerAdapter;
import com.example.diamondata.models.FielderStat;
import com.example.diamondata.models.PitcherStat;
import com.example.diamondata.models.Player;

import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamDetailActivity extends AppCompatActivity {

    private TextView tvDetailTeamName;
    private RecyclerView rvPlayers;
    private Button btnViewMatches;
    private DiamondApiService apiService;
    private int teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_detail);

        // 1. Obtener los datos del Intent enviados desde el TeamsAdapter
        teamId = getIntent().getIntExtra("TEAM_ID", -1);
        String teamName = getIntent().getStringExtra("TEAM_NAME");

        // 2. Inicializar componentes de la interfaz
        tvDetailTeamName = findViewById(R.id.tvDetailTeamName);
        rvPlayers = findViewById(R.id.rvPlayers);
        btnViewMatches = findViewById(R.id.btnViewMatches);

        if (teamName != null) {
            tvDetailTeamName.setText(teamName);
        }

        rvPlayers.setLayoutManager(new LinearLayoutManager(this));
        apiService = RetrofitClient.getRetrofitInstance().create(DiamondApiService.class);

        // 3. Cargar el Roster desde el backend
        obtenerRoster();

        // Acción del botón de partidos
        btnViewMatches.setOnClickListener(v -> {
            Intent intent = new Intent(TeamDetailActivity.this, GamesActivity.class);
            intent.putExtra("TEAM_ID", teamId);
            startActivity(intent);
        });

        com.google.android.material.floatingactionbutton.FloatingActionButton fabAddPlayer = findViewById(R.id.fabAddPlayer);
        fabAddPlayer.setOnClickListener(v -> abrirDialogoNuevoJugador());
    }

    private void obtenerRoster() {
        apiService.getTeamDetail(teamId).enqueue(new Callback<TeamDetailResponse>() {
            @Override
            public void onResponse(Call<TeamDetailResponse> call, Response<TeamDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> roster = response.body().getPlayers();
                    // Conectamos un adaptador personalizado para los jugadores
                    // Le pasamos la lista de jugadores y una interfaz de callback para el click
                    PlayerAdapter adapter = new PlayerAdapter(roster, player -> verEstadisticasTemporada(player));
                    rvPlayers.setAdapter(adapter);
                } else {
                    Toast.makeText(TeamDetailActivity.this, "No se pudo obtener el roster", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TeamDetailResponse> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Función para VER las estadísticas acumuladas
    private void verEstadisticasTemporada(Player player) {
        String temporadaActual = "2026"; // Puedes hacerlo dinámico en el futuro

        apiService.getPlayerSeasonStats(player.getId(), temporadaActual).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Leemos la respuesta de Django (que viene en formato JSON)
                        String jsonStats = response.body().string();
                        org.json.JSONObject statsObj = new org.json.JSONObject(jsonStats);

                        mostrarDialogoResumen(player, statsObj, temporadaActual);

                    } catch (Exception e) {
                        Toast.makeText(TeamDetailActivity.this, "Error leyendo stats", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TeamDetailActivity.this, "El jugador aún no tiene estadísticas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Crea un diálogo sencillo para mostrar la información
    private void mostrarDialogoResumen(Player player, org.json.JSONObject statsObj, String season) throws org.json.JSONException {
        Dialog dialog = new Dialog(this);
        // Usamos un layout básico de Android o puedes crear uno propio (ej. dialog_view_stats)
        dialog.setContentView(R.layout.dialog_create_player); // Reutilizamos temporalmente el fondo oscuro
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // Vamos a inyectar el texto dinámicamente en el layout
        LinearLayout layoutPrincipal = (LinearLayout) dialog.findViewById(R.id.rgPosition).getParent().getParent();
        layoutPrincipal.removeAllViews(); // Limpiamos el formulario viejo de crear jugador

        TextView tvTitulo = new TextView(this);
        tvTitulo.setText("Estadísticas " + season + "\n" + player.getName());
        tvTitulo.setTextSize(22);
        tvTitulo.setTextColor(getResources().getColor(R.color.accent_orange));
        tvTitulo.setPadding(0, 0, 0, 30);
        layoutPrincipal.addView(tvTitulo);

        TextView tvStats = new TextView(this);
        tvStats.setTextColor(getResources().getColor(R.color.text_primary));
        tvStats.setTextSize(16);
        tvStats.setLineSpacing(0, 1.5f);

        // Formateamos el texto dependiendo de si es Pitcher o Fielder
        StringBuilder sb = new StringBuilder();
        if ("P".equals(player.getPositionType())) {
            sb.append("Juegos: ").append(statsObj.optInt("j", 0)).append("\n");
            sb.append("Ganados (G): ").append(statsObj.optInt("g", 0)).append("\n");
            sb.append("Perdidos (P): ").append(statsObj.optInt("p", 0)).append("\n");
            sb.append("Efectividad (ERA): ").append(statsObj.optDouble("pcl", 0.0)).append("\n");
            sb.append("Ponches (SO): ").append(statsObj.optInt("so", 0)).append("\n");
        } else {
            sb.append("Juegos: ").append(statsObj.optInt("j", 0)).append("\n");
            sb.append("Hits (H): ").append(statsObj.optInt("total_h", 0)).append("\n");
            sb.append("Home Runs (HR): ").append(statsObj.optInt("total_hr", 0)).append("\n");
            sb.append("Carreras Impulsadas: ").append(statsObj.optInt("total_cis", 0)).append("\n");
            sb.append("Promedio (AVG): ").append(statsObj.optDouble("avg", 0.0)).append("\n");
        }

        tvStats.setText(sb.toString());
        layoutPrincipal.addView(tvStats);

        dialog.show();
    }

    private void abrirDialogoNuevoJugador() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_player);
        // Hacemos que los bordes del diálogo sean transparentes para que respete tu diseño de esquinas
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etName = dialog.findViewById(R.id.etPlayerName);
        EditText etNumber = dialog.findViewById(R.id.etPlayerNumber);
        Button btnSavePlayer = dialog.findViewById(R.id.btnSavePlayer);
        // RadioButtons para saber si es Fielder o Pitcher
        android.widget.RadioButton rbFielder = dialog.findViewById(R.id.rbFielder);

        btnSavePlayer.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String numberStr = etNumber.getText().toString().trim();

            if (name.isEmpty() || numberStr.isEmpty()) {
                Toast.makeText(this, "Rellena nombre y número", Toast.LENGTH_SHORT).show();
                return;
            }

            int number = Integer.parseInt(numberStr);
            String position = rbFielder.isChecked() ? "F" : "P"; // "F" de Fielder o "P" de Pitcher

            // Creamos el jugador asignándole el ID del equipo en el que estamos (teamId)
            Player nuevoJugador = new Player(name, number, position, teamId);

            // Llamada a Django
            apiService.createPlayer(nuevoJugador).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(TeamDetailActivity.this, "Jugador fichado con éxito", Toast.LENGTH_SHORT).show();
                        dialog.dismiss(); // Cierra el formulario emergente
                        obtenerRoster(); // ¡Recargamos la lista automáticamente para ver al nuevo jugador!
                    } else {
                        // TRUCO: Leer exactamente el mensaje de queja de Django
                        try {
                            String errorDjango = response.errorBody().string();
                            Toast.makeText(TeamDetailActivity.this, "Django dice: " + errorDjango, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(TeamDetailActivity.this, "Fallo de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
