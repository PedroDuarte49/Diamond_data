package com.example.diamondata.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.diamondata.R;
import java.util.List;

public class SeasonStatsAdapter extends RecyclerView.Adapter<SeasonStatsAdapter.StatsViewHolder> {

    // Modelo de datos interno (Nombre del jugador y su línea de estadísticas)
    public static class SeasonItem {
        public String nombre;
        public String stats;
        public SeasonItem(String nombre, String stats) {
            this.nombre = nombre;
            this.stats = stats;
        }
    }

    private List<SeasonItem> listaStats;

    public SeasonStatsAdapter(List<SeasonItem> listaStats) {
        this.listaStats = listaStats;
    }

    @NonNull
    @Override
    public StatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_season_stat, parent, false);
        return new StatsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsViewHolder holder, int position) {
        SeasonItem item = listaStats.get(position);
        holder.tvName.setText(item.nombre);
        holder.tvStats.setText(item.stats);

        // Si no hay estadísticas (usamos esto para crear títulos separadores), ocultamos el texto naranja
        if (item.stats.isEmpty()) {
            holder.tvStats.setVisibility(View.GONE);
            holder.tvName.setTextColor(0xFFFFA500); // Color naranja para los títulos "BATEADORES"
        } else {
            holder.tvStats.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() { return listaStats.size(); }

    public static class StatsViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvStats;
        public StatsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRowPlayerName);
            tvStats = itemView.findViewById(R.id.tvRowPlayerStats);
        }
    }
}
