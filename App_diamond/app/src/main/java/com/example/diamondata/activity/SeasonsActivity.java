package com.example.diamondata.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondata.DiamondApiService;
import com.example.diamondata.R;
import com.example.diamondata.adapter.TeamsAdapter; // Tu adaptador actual de equipos
import com.example.diamondata.models.Team;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeasonsActivity extends AppCompatActivity {

    private RecyclerView rvTeams;
    private DiamondApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seasons);

        rvTeams = findViewById(R.id.rvSeasonsTeams);
        rvTeams.setLayoutManager(new LinearLayoutManager(this));
        apiService = RetrofitClient.getRetrofitInstance().create(DiamondApiService.class);

        cargarEquipos();
    }

    private void cargarEquipos() {
        apiService.getAllTeams().enqueue(new Callback<List<Team>>() {
            @Override
            public void onResponse(Call<List<Team>> call, Response<List<Team>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    TeamsAdapter adapter = new TeamsAdapter(response.body(), (Team team) -> {
                        Intent intent = new Intent(SeasonsActivity.this, TeamSeasonStatsActivity.class);
                        intent.putExtra("TEAM_ID", team.getId());
                        intent.putExtra("TEAM_NAME", team.getName());
                        startActivity(intent);
                    });
                    rvTeams.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Team>> call, Throwable t) {
                Toast.makeText(SeasonsActivity.this, "Error cargando equipos", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
