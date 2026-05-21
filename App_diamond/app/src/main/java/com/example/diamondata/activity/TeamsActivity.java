package com.example.diamondata.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondata.DiamondApiService;
import com.example.diamondata.R;
import com.example.diamondata.adapter.TeamsAdapter;
import com.example.diamondata.models.Team;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeamsActivity extends AppCompatActivity {
    private RecyclerView rvTeams;
    private DiamondApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teams);

        rvTeams = findViewById(R.id.rvTeams);
        rvTeams.setLayoutManager(new LinearLayoutManager(this));

        apiService = RetrofitClient.getRetrofitInstance().create(DiamondApiService.class);

        cargarEquipos();
    }

    private void cargarEquipos() {
        apiService.getAllTeams().enqueue(new Callback<List<Team>>() {
            @Override
            public void onResponse(Call<List<Team>> call, Response<List<Team>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Aquí conectarías tu Adapter (que crearemos luego)
                    TeamsAdapter adapter = new TeamsAdapter(response.body());
                    rvTeams.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Team>> call, Throwable t) {
                Toast.makeText(TeamsActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
}