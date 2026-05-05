import json
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
from .models import Player, Game, FielderStat, PitcherStat, Team


# --- GESTIÓN DE JUGADORES ---

@csrf_exempt
def players_handler(request):
    """
    GET: Listar jugadores (soporta filtro por tipo vía Query Params)
    POST: Crear un nuevo jugador [cite: 110]
    """
    if request.method == 'GET':
        # Ejemplo de Query Param: /api/players/?type=P
        p_type = request.GET.get('type')
        if p_type:
            players = Player.objects.filter(position_type=p_type)
        else:
            players = Player.objects.all()

        data = [{
            "id": p.id,
            "name": p.name,
            "number": p.number,
            "type": p.position_type
        } for p in players]
        return JsonResponse(data, safe=False, status=200)

    elif request.method == 'POST':
        try:
            body = json.loads(request.body)  # Uso de JSON en el cuerpo [cite: 104]
            new_player = Player.objects.create(
                name=body['name'],
                number=body['number'],
                position_type=body['type']
            )
            return JsonResponse({"id": new_player.id, "message": "Jugador creado"}, status=201)
        except (KeyError, json.JSONDecodeError):
            return JsonResponse({"error": "Datos inválidos"}, status=400)

    return JsonResponse({"error": "Método no soportado"}, status=405)


@csrf_exempt
def player_detail(request, player_id):
    """
    Uso de Path Params para identificar al jugador
    GET: Ver un jugador
    PUT: Actualizar jugador [cite: 110]
    DELETE: Eliminar jugador [cite: 110]
    """
    try:
        player = Player.objects.get(id=player_id)
    except Player.DoesNotExist:
        return JsonResponse({"error": "Jugador no encontrado"}, status=404)

    if request.method == 'GET':
        return JsonResponse({
            "id": player.id,
            "name": player.name,
            "number": player.number,
            "type": player.position_type
        })

    elif request.method == 'PUT':
        body = json.loads(request.body)
        player.name = body.get('name', player.name)
        player.number = body.get('number', player.number)
        player.save()
        return JsonResponse({"message": "Jugador actualizado"}, status=200)

    elif request.method == 'DELETE':
        player.delete()
        return JsonResponse({"message": "Jugador eliminado"}, status=200)

    return JsonResponse({"error": "Método no soportado"}, status=405)


# --- GESTIÓN DE ESTADÍSTICAS (Relación N:N) ---

@csrf_exempt
def fielder_stats_handler(request):
    """
    POST: Registrar estadísticas de un bateador en un partido [cite: 110]
    """
    if request.method == 'POST':
        body = json.loads(request.body)
        try:
            # Cálculo automático del promedio (PRO) antes de guardar
            h = int(body['h'])
            tb = int(body['tb'])
            avg = h / tb if tb > 0 else 0.0

            stat = FielderStat.objects.create(
                player_id=body['player_id'],
                game_id=body['game_id'],
                j=body.get('j', 1),
                tb=tb,
                c=body.get('c', 0),
                h=h,
                h2=body.get('h2', 0),
                h3=body.get('h3', 0),
                h4=body.get('h4', 0),
                cis=body.get('cis', 0),
                bb=body.get('bb', 0),
                so=body.get('so', 0),
                pro=round(avg, 3)
            )
            return JsonResponse({"id": stat.id, "message": "Estadística de bateo guardada"}, status=201)
        except Exception as e:
            return JsonResponse({"error": str(e)}, status=400)

    return JsonResponse({"error": "Método no soportado"}, status=405)


@csrf_exempt
def pitcher_stats_handler(request):
    """
    POST: Registrar estadísticas de un lanzador en un partido [cite: 110]
    """
    if request.method == 'POST':
        body = json.loads(request.body)
        try:
            stat = PitcherStat.objects.create(
                player_id=body['player_id'],
                game_id=body['game_id'],
                g=body.get('g', 0),
                p=body.get('p', 0),
                pcl=body.get('pcl', 0.0),  # ERA
                il=body.get('il', 0.0),  # Entradas lanzadas
                so=body.get('so', 0),
                bb=body.get('bb', 0)
            )
            return JsonResponse({"id": stat.id, "message": "Estadística de pitcheo guardada"}, status=201)
        except Exception as e:
            return JsonResponse({"error": str(e)}, status=400)

    return JsonResponse({"error": "Método no soportado"}, status=405)

@csrf_exempt
def teams_handler(request):
    """
    GET: Listar todos los equipos.
    POST: Crear un nuevo equipo.
    """
    if request.method == 'GET':
        teams = Team.objects.all()
        data = [{
            "id": t.id,
            "name": t.name,
            "city": t.city,
            "coach": t.coach_name
        } for t in teams]
        return JsonResponse(data, safe=False, status=200)

    elif request.method == 'POST':
        body = json.loads(request.body)
        new_team = Team.objects.create(
            name=body['name'],
            city=body['city'],
            coach_name=body['coach']
        )
        return JsonResponse({"id": new_team.id, "message": "Equipo creado"}, status=201)

@csrf_exempt
def team_detail(request, team_id):
    """
    GET: Ver info de un equipo y sus jugadores (Path Param).
    PUT: Editar nombre, ciudad o entrenador.
    DELETE: Borrar equipo.
    """
    try:
        team = Team.objects.get(id=team_id)
    except Team.DoesNotExist:
        return JsonResponse({"error": "Equipo no encontrado"}, status=404)

    if request.method == 'GET':
        # Devolvemos el equipo y su lista de jugadores
        players = team.players.all()
        return JsonResponse({
            "id": team.id,
            "name": team.name,
            "city": team.city,
            "coach": team.coach_name,
            "players": [{"id": p.id, "name": p.name} for p in players]
        })

    elif request.method == 'PUT':
        body = json.loads(request.body)
        team.name = body.get('name', team.name)
        team.city = body.get('city', team.city)
        team.coach_name = body.get('coach', team.coach_name)
        team.save()
        return JsonResponse({"message": "Equipo actualizado"})

    elif request.method == 'DELETE':
        team.delete()
        return JsonResponse({"message": "Equipo eliminado"}, status=200)

@csrf_exempt
def season_stats(request, player_id, season):
    if request.method == 'GET':
        stats = FielderStat.objects.filter(player_id=player_id, game__season=season)
        total_h = sum(s.h for s in stats)
        total_tb = sum(s.tb for s in stats)
        avg = total_h / total_tb if total_tb > 0 else 0

        return JsonResponse({
            "player_id": player_id,
            "season": season,
            "total_hits": total_h,
            "avg": round(avg, 3)
        })