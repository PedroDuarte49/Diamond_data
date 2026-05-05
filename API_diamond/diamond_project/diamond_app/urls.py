from django.urls import path
from . import endpoints

urlpatterns = [
    # Gestión de Jugadores (GET y POST)
    path('players/', endpoints.players_handler),

    # Detalle de Jugador (GET, PUT y DELETE usando Path Params)
    path('players/<int:player_id>/', endpoints.player_detail),

    # Registro de Estadísticas (Relación N:N)
    path('stats/fielder/', endpoints.fielder_stats_handler),
    path('stats/pitcher/', endpoints.pitcher_stats_handler),

    # Información de los equipos
    path('teams/', endpoints.teams_handler),
    path('teams/<int:team_id>/', endpoints.team_detail),
]