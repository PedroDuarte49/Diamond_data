package com.example.diamondata.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondata.DiamondApiService;
import com.example.diamondata.R;
import com.example.diamondata.adapter.TeamsAdapter;
import com.example.diamondata.models.Team;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamsActivity extends AppCompatActivity {

    private RecyclerView rvTeams;
    private FloatingActionButton fabAddTeam;
    private DiamondApiService apiService;

    // Declaramos el adaptador a nivel de clase para poder actualizarlo
    private TeamsAdapter teamsAdapter;
    private List<Team> listaEquiposDeBéisbol = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        // 1. Vincular componentes del XML
        rvTeams = findViewById(R.id.listaEquipos);
        fabAddTeam = findViewById(R.id.fabAddTeam);

        rvTeams.setLayoutManager(new LinearLayoutManager(this));

        // SOLUCIÓN AL LOGCAT: Inicializamos la lista con un adaptador vacío de base
        // Esto le quita el mensaje de advertencia al sistema operativo de inmediato
        teamsAdapter = new TeamsAdapter(listaEquiposDeBéisbol, team -> {
            // Cuando tocamos un equipo en la pantalla principal, vamos a su detalle
            Intent intent = new Intent(TeamsActivity.this, TeamDetailActivity.class);
            intent.putExtra("TEAM_ID", team.getId());
            intent.putExtra("TEAM_NAME", team.getName());
            startActivity(intent);
        });
        rvTeams.setAdapter(teamsAdapter);

        // 2. Inicializar servicio de red (Retrofit)
        apiService = RetrofitClient.getRetrofitInstance().create(DiamondApiService.class);

        // 3. Configurar botón flotante para abrir el formulario de creación
        fabAddTeam.setOnClickListener(v -> {
            Intent intent = new Intent(TeamsActivity.this, CreateTeamActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Disparar la consulta a la base de datos de Django cada vez que la pantalla esté al frente
        cargarEquipos();
    }

    private void cargarEquipos() {
        if (apiService == null) return;

        apiService.getAllTeams().enqueue(new Callback<List<Team>>() {
            @Override
            public void onResponse(Call<List<Team>> call, Response<List<Team>> response) {
                // Escudo protector: si el usuario cambió de pantalla muy rápido, evitamos tocar la UI
                if (isFinishing() || isDestroyed() || rvTeams == null) return;

                if (response.isSuccessful() && response.body() != null) {
                    // Limpiamos la lista temporal e inyectamos los datos reales traídos de Django
                    listaEquiposDeBéisbol.clear();
                    listaEquiposDeBéisbol.addAll(response.body());

                    // Notificamos al adaptador que los datos cambiaron para que redibuje las tarjetas con bordes naranjas
                    teamsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(TeamsActivity.this, "Django denegó la consulta de equipos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Team>> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                // Si salta esto, tu servidor Django está apagado o la IP de RetrofitClient está mal configurada
                Toast.makeText(TeamsActivity.this, "Error de red contra Django: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}