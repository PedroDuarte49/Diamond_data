package com.example.diamondata.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
            // Aquí podrás lanzar el Intent hacia tu GameListActivity si lo deseas
            Toast.makeText(this, "Abriendo lista de partidos...", Toast.LENGTH_SHORT).show();
        });
    }

    private void obtenerRoster() {
        apiService.getTeamDetail(teamId).enqueue(new Callback<TeamDetailResponse>() {
            @Override
            public void onResponse(Call<TeamDetailResponse> call, Response<TeamDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Player> roster = response.body().getPlayers();
                    // Conectamos un adaptador personalizado para los jugadores
                    // Le pasamos la lista de jugadores y una interfaz de callback para el click
                    PlayerAdapter adapter = new PlayerAdapter(roster, player -> mostrarDialogoEstadisticas(player));
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

    // 4. LÓGICA DEL DIÁLOGO EMERGENTE (Formulario Complejo)
    private void mostrarDialogoEstadisticas(Player player) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_stats_form);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvPlayerNameDialog = dialog.findViewById(R.id.tvPlayerNameDialog);
        tvPlayerNameDialog.setText("Stats de: " + player.getName() + " (#" + player.getNumber() + ")");

        Button btnConfirmStats = dialog.findViewById(R.id.btnConfirmStats);

        btnConfirmStats.setOnClickListener(v -> {
            // Evaluamos el tipo de perfil técnico ("F" = Fielder / "P" = Pitcher)
            if ("P".equalsIgnoreCase(player.getPositionType())) {
                enviarEstadisticasPitcher(player.getId(), dialog);
            } else {
                enviarEstadisticasFielder(player.getId(), dialog);
            }
        });

        dialog.show();
    }

    private void enviarEstadisticasFielder(int playerId, Dialog dialog) {
        // Enlazar los EditText del GridLayout del layout dialog_stats_form.xml
        EditText etJ = dialog.findViewById(R.id.etFielderJ);
        EditText etTB = dialog.findViewById(R.id.etFielderTB);
        EditText etC = dialog.findViewById(R.id.etFielderC);
        EditText etH = dialog.findViewById(R.id.etFielderH);
        EditText etH2 = dialog.findViewById(R.id.etFielderH2);
        EditText etH3 = dialog.findViewById(R.id.etFielderH3);
        EditText etH4 = dialog.findViewById(R.id.etFielderH4);
        EditText etCIS = dialog.findViewById(R.id.etFielderCIS);
        EditText etBB = dialog.findViewById(R.id.etFielderBB);
        EditText etBBI = dialog.findViewById(R.id.etFielderBBI);
        EditText etSO = dialog.findViewById(R.id.etFielderSO);
        EditText etBR = dialog.findViewById(R.id.etFielderBR);
        EditText etAR = dialog.findViewById(R.id.etFielderAR);

        FielderStat stats = new FielderStat();
        stats.setPlayer(playerId);
        stats.setGame(1); // ID de juego estático temporal o recuperado dinámicamente

        // Mapeo seguro capturando datos numéricos (si está vacío por defecto es 0)
        stats.setJ(etJ.getText().toString().isEmpty() ? 0 : Integer.parseInt(etJ.getText().toString()));
        stats.setTb(etTB.getText().toString().isEmpty() ? 0 : Integer.parseInt(etTB.getText().toString()));
        stats.setC(etC.getText().toString().isEmpty() ? 0 : Integer.parseInt(etC.getText().toString()));
        stats.setH(etH.getText().toString().isEmpty() ? 0 : Integer.parseInt(etH.getText().toString()));
        stats.setH2(etH2.getText().toString().isEmpty() ? 0 : Integer.parseInt(etH2.getText().toString()));
        stats.setH3(etH3.getText().toString().isEmpty() ? 0 : Integer.parseInt(etH3.getText().toString()));
        stats.setH4(etH4.getText().toString().isEmpty() ? 0 : Integer.parseInt(etH4.getText().toString()));
        stats.setCis(etCIS.getText().toString().isEmpty() ? 0 : Integer.parseInt(etCIS.getText().toString()));
        stats.setBb(etBB.getText().toString().isEmpty() ? 0 : Integer.parseInt(etBB.getText().toString()));
        stats.setBbi(etBBI.getText().toString().isEmpty() ? 0 : Integer.parseInt(etBBI.getText().toString()));
        stats.setSo(etSO.getText().toString().isEmpty() ? 0 : Integer.parseInt(etSO.getText().toString()));
        stats.setBr(etBR.getText().toString().isEmpty() ? 0 : Integer.parseInt(etBR.getText().toString()));
        stats.setAr(etAR.getText().toString().isEmpty() ? 0 : Integer.parseInt(etAR.getText().toString()));

        // Enviamos el objeto mapeado vía POST asíncrono
        apiService.saveFielderStat(stats).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TeamDetailActivity.this, "Estadísticas de bateo guardadas", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enviarEstadisticasPitcher(int playerId, Dialog dialog) {
        EditText etG = dialog.findViewById(R.id.etPitcherG);
        EditText etP = dialog.findViewById(R.id.etPitcherP);
        EditText etJ = dialog.findViewById(R.id.etPitcherJ);
        EditText etA = dialog.findViewById(R.id.etPitcherA);
        EditText etJC = dialog.findViewById(R.id.etPitcherJC);
        EditText etBL = dialog.findViewById(R.id.etPitcherBL);
        EditText etSV = dialog.findViewById(R.id.etPitcherSV);
        EditText etIL = dialog.findViewById(R.id.etPitcherIL);
        EditText etH = dialog.findViewById(R.id.etPitcherH);
        EditText etCL = dialog.findViewById(R.id.etPitcherCL);
        EditText etSO = dialog.findViewById(R.id.etPitcherSO);

        PitcherStat stats = new PitcherStat();
        stats.setPlayer(playerId);
        stats.setGame(1);

        stats.setG(etG.getText().toString().isEmpty() ? 0 : Integer.parseInt(etG.getText().toString()));
        stats.setP(etP.getText().toString().isEmpty() ? 0 : Integer.parseInt(etP.getText().toString()));
        stats.setJ(etJ.getText().toString().isEmpty() ? 0 : Integer.parseInt(etJ.getText().toString()));
        stats.setA(etA.getText().toString().isEmpty() ? 0 : Integer.parseInt(etA.getText().toString()));
        stats.setJc(etJC.getText().toString().isEmpty() ? 0 : Integer.parseInt(etJC.getText().toString()));
        stats.setBl(etBL.getText().toString().isEmpty() ? 0 : Integer.parseInt(etBL.getText().toString()));
        stats.setSv(etSV.getText().toString().isEmpty() ? 0 : Integer.parseInt(etSV.getText().toString()));
        stats.setIl(etIL.getText().toString().isEmpty() ? 0.0 : Double.parseDouble(etIL.getText().toString()));
        stats.setH(etH.getText().toString().isEmpty() ? 0 : Integer.parseInt(etH.getText().toString()));
        stats.setCl(etCL.getText().toString().isEmpty() ? 0 : Integer.parseInt(etCL.getText().toString()));
        stats.setSo(etSO.getText().toString().isEmpty() ? 0 : Integer.parseInt(etSO.getText().toString()));

        apiService.savePitcherStat(stats).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TeamDetailActivity.this, "Estadísticas de Lanzador guardadas", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(TeamDetailActivity.this, "Error al guardar pitcher", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
