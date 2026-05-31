package com.example.diamondata.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondata.DiamondApiService;
import com.example.diamondata.R;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamSeasonStatsActivity extends AppCompatActivity {

    private int teamId;
    private String teamName;
    private Spinner spinnerSeason;
    private DiamondApiService apiService;
    private RecyclerView rvSeasonStats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_season_stats);

        apiService = RetrofitClient.getRetrofitInstance().create(DiamondApiService.class);

        teamId = getIntent().getIntExtra("TEAM_ID", -1);
        teamName = getIntent().getStringExtra("TEAM_NAME");

        TextView tvTitle = findViewById(R.id.tvSeasonTeamName);
        tvTitle.setText(teamName + " - Histórico");

        spinnerSeason = findViewById(R.id.spinnerSeason);

        rvSeasonStats = findViewById(R.id.rvSeasonStats);
        rvSeasonStats.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));

        cargarTemporadasReales();

        // 2. Escuchador del desplegable
        spinnerSeason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String yearSeleccionado = parent.getItemAtPosition(position).toString();
                // Omitimos cargar si dice "Sin datos"
                if (!yearSeleccionado.equals("Sin datos")) {
                    pedirEstadisticasADjango(yearSeleccionado);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void cargarTemporadasReales() {
        apiService.getAvailableSeasons().enqueue(new Callback<java.util.List<String>>() {
            @Override
            public void onResponse(Call<java.util.List<String>> call, Response<java.util.List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<String> temporadasReales = response.body();

                    // Si la base de datos está vacía y no hay partidos aún
                    if (temporadasReales.isEmpty()) {
                        temporadasReales.add("Sin datos");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(TeamSeasonStatsActivity.this, android.R.layout.simple_spinner_dropdown_item, temporadasReales);
                    spinnerSeason.setAdapter(adapter);

                    // Nota: Al hacer 'setAdapter', Android seleccionará automáticamente
                    // el primer año de la lista y disparará 'pedirEstadisticasADjango' por ti.
                }
            }

            @Override
            public void onFailure(Call<java.util.List<String>> call, Throwable t) {
                Toast.makeText(TeamSeasonStatsActivity.this, "Error cargando temporadas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pedirEstadisticasADjango(String year) {
        apiService.getTeamSeasonStats(teamId, year).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonRespuesta = response.body().string();
                        org.json.JSONObject json = new org.json.JSONObject(jsonRespuesta);
                        org.json.JSONArray fielders = json.getJSONArray("fielders");
                        org.json.JSONArray pitchers = json.getJSONArray("pitchers");

                        java.util.List<com.example.diamondata.adapter.SeasonStatsAdapter.SeasonItem> listaItems = new java.util.ArrayList<>();

                        // 1. Procesamos los Bateadores de forma segura (usando optString)
                        if (fielders.length() > 0) {
                            listaItems.add(new com.example.diamondata.adapter.SeasonStatsAdapter.SeasonItem("⚾ BATEADORES", ""));
                            for(int i = 0; i < fielders.length(); i++) {
                                org.json.JSONObject f = fielders.getJSONObject(i);

                                // optString evita que la app crashee si el número o nombre es null
                                String nombre = f.optString("player__name", "Jugador") + " #" + f.optString("player__number", "?");
                                String stats = "H: " + f.optString("total_h", "0") +
                                        " | C: " + f.optString("total_c", "0") +
                                        " | HR: " + f.optString("total_h4", "0");

                                listaItems.add(new com.example.diamondata.adapter.SeasonStatsAdapter.SeasonItem(nombre, stats));
                            }
                        }

                        // 2. Procesamos los Lanzadores de forma segura
                        if (pitchers.length() > 0) {
                            listaItems.add(new com.example.diamondata.adapter.SeasonStatsAdapter.SeasonItem("🔥 LANZADORES", ""));
                            for(int i = 0; i < pitchers.length(); i++) {
                                org.json.JSONObject p = pitchers.getJSONObject(i);

                                String nombre = p.optString("player__name", "Jugador") + " #" + p.optString("player__number", "?");
                                String stats = "G: " + p.optString("total_g", "0") +
                                        " | P: " + p.optString("total_p", "0") +
                                        " | SO: " + p.optString("total_so", "0") +
                                        " | SV: " + p.optString("total_sv", "0");

                                listaItems.add(new com.example.diamondata.adapter.SeasonStatsAdapter.SeasonItem(nombre, stats));
                            }
                        }

                        // Si no hay datos de nada en este año, mostramos un aviso en la lista
                        if (listaItems.isEmpty()) {
                            listaItems.add(new com.example.diamondata.adapter.SeasonStatsAdapter.SeasonItem("No hay estadísticas registradas", ""));
                        }

                        // Pintamos la lista
                        rvSeasonStats.setAdapter(new com.example.diamondata.adapter.SeasonStatsAdapter(listaItems));

                    } catch (Exception e) {
                        e.printStackTrace();
                        String errorReal = e.toString();
                        Toast.makeText(TeamSeasonStatsActivity.this, "Error real: " + errorReal, Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(TeamSeasonStatsActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}