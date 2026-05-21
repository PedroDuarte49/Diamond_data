package com.example.diamondata.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diamondata.R;
import com.example.diamondata.activity.TeamDetailActivity;
import com.example.diamondata.models.Team;

import java.util.List;

public class TeamsAdapter extends RecyclerView.Adapter<TeamsAdapter.TeamViewHolder> {

    private final List<Team> teamList;

    public TeamsAdapter(List<Team> teamList) {
        this.teamList = teamList;
    }

    @NonNull
    @Override
    public TeamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_card, parent, false);
        return new TeamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TeamViewHolder holder, int position) {
        Team team = teamList.get(position);
        holder.tvTitle.setText(team.getName());
        holder.tvSubtitle.setText(team.getCity() + " • Coach: " + team.getCoachName());

        // Evento de navegación maestro-detalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TeamDetailActivity.class);
            intent.putExtra("TEAM_ID", team.getId());
            intent.putExtra("TEAM_NAME", team.getName());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return teamList != null ? teamList.size() : 0;
    }

    static class TeamViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;

        public TeamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitleRow);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitleRow);
        }
    }
}
