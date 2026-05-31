package com.example.diamondata.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.diamondata.R;
import com.example.diamondata.models.Player;
import java.util.List;

public class GameRosterAdapter extends RecyclerView.Adapter<GameRosterAdapter.ViewHolder> {

    private List<Player> playerList;
    private OnPlayerClickListener listener;

    public interface OnPlayerClickListener {
        void onPlayerClick(Player player);
    }

    public GameRosterAdapter(List<Player> playerList, OnPlayerClickListener listener) {
        this.playerList = playerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false); // Usa tu item_player existente
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Player player = playerList.get(position);
        holder.tvName.setText(player.getName() + " (#" + player.getNumber() + ")");
        holder.itemView.setOnClickListener(v -> listener.onPlayerClick(player));
    }

    @Override
    public int getItemCount() { return playerList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPlayerName); // Asegúrate que este ID coincida con tu item_player
        }
    }
}