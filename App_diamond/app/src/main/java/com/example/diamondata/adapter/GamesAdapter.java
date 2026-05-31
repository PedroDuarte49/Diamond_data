package com.example.diamondata.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondata.R;
import com.example.diamondata.activity.GameBoxScoreActivity;
import com.example.diamondata.models.Game;

import java.util.List;

public class GamesAdapter extends RecyclerView.Adapter<GamesAdapter.GameViewHolder> {

    private List<Game> gamesList;
    private int teamId;

    public GamesAdapter(List<Game> gamesList, int teamId) {
        this.gamesList = gamesList;
        this.teamId = teamId;
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        Game game = gamesList.get(position);

        holder.tvGameTitle.setText("VS " + game.getOpponent());
        holder.tvGameDetails.setText(game.getDate() + " | " + game.getLocation());
        holder.tvGameSeason.setText("Temporada: " + game.getSeason());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), GameBoxScoreActivity.class);
            intent.putExtra("GAME_ID", game.getId());
            intent.putExtra("TEAM_ID", teamId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return gamesList == null ? 0 : gamesList.size();
    }

    public static class GameViewHolder extends RecyclerView.ViewHolder {
        TextView tvGameTitle, tvGameDetails, tvGameSeason;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGameTitle = itemView.findViewById(R.id.tvGameTitle);
            tvGameDetails = itemView.findViewById(R.id.tvGameDetails);
            tvGameSeason = itemView.findViewById(R.id.tvGameSeason);
        }
    }
}