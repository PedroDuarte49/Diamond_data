package com.example.diamondata.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diamondata.R;
import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Vincular los componentes del XML con Java
        MaterialCardView cardEquipos = findViewById(R.id.cardEquipos);
        MaterialCardView cardYo = findViewById(R.id.cardYo);
        MaterialCardView cardTemporadas = findViewById(R.id.cardHistorico);

        // 2. Configurar el click para "Gestión de Equipos"
        cardEquipos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TeamsActivity.class);
            startActivity(intent);
        });

        // 3. Configurar el click para "Mi Perfil"
        cardYo.setOnClickListener(v -> {
        });

        // 4. Configurar el click para "Histórico/Temporadas"
        cardTemporadas.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SeasonsActivity.class);
            startActivity(intent);
        });
    }
}