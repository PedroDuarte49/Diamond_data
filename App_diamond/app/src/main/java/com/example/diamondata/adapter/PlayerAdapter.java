package com.example.diamondata.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondata.R;
import com.example.diamondata.models.Player;

import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private final List<Player> playerList;
    private final OnPlayerClickListener listener;
    private final OnPlayerDeleteListener deleteListener; // NUEVO ESCUCHADOR PARA LA PAPELERA

    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }

    public interface OnPlayerDeleteListener {
        void onDeleteClick(Player player);
    }

    // Ahora el constructor pide 2 acciones: la de la tarjeta y la de la papelera
    public PlayerAdapter(List<Player> playerList, OnPlayerClickListener listener, OnPlayerDeleteListener deleteListener) {
        this.playerList = playerList;
        this.listener = listener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // AHORA SÍ USA TU DISEÑO PERSONALIZADO:
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player_delete, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.tvName.setText(player.getName() + " - #" + player.getNumber());
        holder.tvPosition.setText("Posición: " + ("P".equalsIgnoreCase(player.getPositionType()) ? "Pitcher (Lanzador)" : "Fielder (Bateador)"));

        // Clic en toda la tarjeta -> Abre las estadísticas
        holder.itemView.setOnClickListener(v -> listener.onPlayerClick(player));

        // Clic en la papelera -> Borra al jugador
        holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(player));
    }

    @Override
    public int getItemCount() {
        return playerList != null ? playerList.size() : 0;
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPosition;
        ImageView btnDelete;

        public PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPlayerName);
            tvPosition = itemView.findViewById(R.id.tvPlayerPosition);
            btnDelete = itemView.findViewById(R.id.btnDeletePlayer); // Enlazamos la papelera
        }
    }
}