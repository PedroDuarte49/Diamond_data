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

    #Información de los partidos
    path('games/', endpoints.games_handler),
    path('games/<int:game_id>/', endpoints.game_detail),
    path('stats/game/<int:game_id>/player/<int:player_id>/', endpoints.get_player_game_stats),

    # Ver estadísticas acumuladas por temporada
    path('stats/season/<int:player_id>/<str:season>/', endpoints.season_stats),
    path('stats/team/<int:team_id>/season/<str:season>/', endpoints.team_season_stats),
    path('seasons/', endpoints.get_available_seasons),

    # Eliminar datos
    path('games/<int:game_id>/delete/', endpoints.delete_game),
    path('players/<int:player_id>/delete/', endpoints.delete_player),
    path('teams/<int:team_id>/delete/', endpoints.delete_team),
]