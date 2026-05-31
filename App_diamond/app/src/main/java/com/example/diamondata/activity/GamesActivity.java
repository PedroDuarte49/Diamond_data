package com.example.diamondata.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondata.DiamondApiService;
import com.example.diamondata.R;
import com.example.diamondata.adapter.GamesAdapter;
import com.example.diamondata.models.Game;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GamesActivity extends AppCompatActivity {

    private RecyclerView rvGames;
    private DiamondApiService apiService;
    private int teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        teamId = getIntent().getIntExtra("TEAM_ID", -1);
        rvGames = findViewById(R.id.rvGames);
        rvGames.setLayoutManager(new LinearLayoutManager(this));

        apiService = RetrofitClient.getRetrofitInstance().create(DiamondApiService.class);

        FloatingActionButton fabAddGame = findViewById(R.id.fabAddGame);
        fabAddGame.setOnClickListener(v -> abrirDialogoNuevoPartido());

        cargarPartidos();
    }

    private void cargarPartidos() {
        apiService.getAllGames().enqueue(new Callback<List<Game>>() {
            @Override
            public void onResponse(Call<List<Game>> call, Response<List<Game>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    GamesAdapter adapter = new GamesAdapter(response.body(), teamId);
                    rvGames.setAdapter(adapter);

                }
            }

            @Override
            public void onFailure(Call<List<Game>> call, Throwable t) {
                Toast.makeText(GamesActivity.this, "Error cargando partidos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void abrirDialogoNuevoPartido() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_create_game);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etOpponent = dialog.findViewById(R.id.etGameOpponent);
        EditText etDate = dialog.findViewById(R.id.etGameDate);
        EditText etLocation = dialog.findViewById(R.id.etGameLocation);
        EditText etSeason = dialog.findViewById(R.id.etGameSeason);
        Button btnSaveGame = dialog.findViewById(R.id.btnSaveGame);

        // --- CONFIGURACIÓN DEL CALENDARIO ---

        // 1. Evitamos que se abra el teclado al tocar la fecha
        etDate.setFocusable(false);
        etDate.setClickable(true);

        // 2. Al tocar el campo de fecha, abrimos el calendario de Android
        etDate.setOnClickListener(v -> {
            // Obtenemos la fecha actual para que el calendario se abra en el día de hoy
            java.util.Calendar calendario = java.util.Calendar.getInstance();
            int anioActual = calendario.get(java.util.Calendar.YEAR);
            int mesActual = calendario.get(java.util.Calendar.MONTH);
            int diaActual = calendario.get(java.util.Calendar.DAY_OF_MONTH);

            android.app.DatePickerDialog datePickerDialog = new android.app.DatePickerDialog(
                    GamesActivity.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        // 3. Formateamos la fecha exactamente como Django la exige: YYYY-MM-DD
                        // (El %02d asegura que los meses como "5" se escriban como "05")
                        String fechaSeleccionada = String.format(java.util.Locale.getDefault(), "%04d-%02d-%02d", year, (monthOfYear + 1), dayOfMonth);

                        // 4. Escribimos la fecha en su campo correspondiente
                        etDate.setText(fechaSeleccionada);

                        // 5. TRUCO MÁGICO: Auto-rellenamos la temporada con el año seleccionado
                        etSeason.setText(String.valueOf(year));
                    },
                    anioActual, mesActual, diaActual);

            datePickerDialog.show();
        });

        btnSaveGame.setOnClickListener(v -> {
            String opp = etOpponent.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String loc = etLocation.getText().toString().trim();
            String season = etSeason.getText().toString().trim();

            if (opp.isEmpty() || date.isEmpty() || loc.isEmpty()) {
                Toast.makeText(this, "Rellena los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            Game nuevoPartido = new Game(opp, date, season.isEmpty() ? "2026" : season, loc);

            apiService.createGame(nuevoPartido).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(GamesActivity.this, "¡Partido programado!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        cargarPartidos(); // Recarga la lista
                    } else {
                        // TRUCO: Leemos el error exacto que nos devuelve Django
                        try {
                            String errorDjango = response.errorBody().string();
                            Toast.makeText(GamesActivity.this, "Error: " + errorDjango, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(GamesActivity.this, "Error de red", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }
}