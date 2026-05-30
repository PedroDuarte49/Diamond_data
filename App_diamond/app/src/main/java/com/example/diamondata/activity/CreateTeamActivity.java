package com.example.diamondata.activity;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.diamondata.DiamondApiService;
import com.example.diamondata.R;
import com.example.diamondata.models.Team;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTeamActivity extends AppCompatActivity {

    private TextInputEditText etTeamName, etTeamCity;
    private MaterialButton btnSaveTeam;
    private DiamondApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        // 1. Vincular componentes del Layout XML
        etTeamName = findViewById(R.id.etTeamName);
        etTeamCity = findViewById(R.id.etTeamCity);
        btnSaveTeam = findViewById(R.id.btnSaveTeam);

        // 2. Inicializar el cliente de red Retrofit
        apiService = RetrofitClient.getRetrofitInstance().create(DiamondApiService.class);

        // 3. Configurar el evento Click para guardar el equipo
        btnSaveTeam.setOnClickListener(v -> registrarEquipoEnBaseDeDatos());
    }

    private void registrarEquipoEnBaseDeDatos() {
        String name = etTeamName.getText().toString().trim();
        String city = etTeamCity.getText().toString().trim();

        // El modelo Team pide (name, city, coach_name).
        // Como el layout actual no tiene campo de Coach, enviaremos uno por defecto temporal o puedes añadirlo luego.
        String coachName = "Entrenador Principal";

        // Validación de campos vacíos
        if (name.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear el objeto modelo con los datos de la interfaz
        Team nuevoEquipo = new Team(name, city, coachName);

        // Realizar la petición POST asíncrona a Django
        apiService.createTeam(nuevoEquipo).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CreateTeamActivity.this, "¡Equipo creado con éxito!", Toast.LENGTH_SHORT).show();
                    finish(); // Cierra esta pantalla y regresa a la lista de equipos
                } else {
                    Toast.makeText(CreateTeamActivity.this, "Error del servidor al crear equipo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(CreateTeamActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}