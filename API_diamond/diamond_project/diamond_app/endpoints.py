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

            body = json.loads(request.body)

            # Capturamos el equipo que nos envía Android

            equipo_id = body.get('team', None)

            new_player = Player.objects.create(

                name=body['name'],

                number=body['number'],

                position_type=body['type'],

                team_id=equipo_id

            )

            return JsonResponse({"message": "Jugador creado", "id": new_player.id}, status=201)


        except KeyError:

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
    if request.method == 'POST':
        data = json.loads(request.body)
        # TRUCO: Busca por jugador y partido. Si existe, lo actualiza. Si no, lo crea.
        stat, created = FielderStat.objects.update_or_create(
            player_id=data.get('player_id'),
            game_id=data.get('game_id'),
            defaults={
                'j': data.get('j', 0), 'tb': data.get('tb', 0), 'c': data.get('c', 0),
                'h': data.get('h', 0), 'h2': data.get('h2', 0), 'h3': data.get('h3', 0),
                'h4': data.get('h4', 0), 'cis': data.get('cis', 0), 'bb': data.get('bb', 0),
                'bbi': data.get('bbi', 0), 'so': data.get('so', 0), 'br': data.get('br', 0),
                'ar': data.get('ar', 0)
            }
        )
        return JsonResponse({"message": "Bateador actualizado correctamente"})

@csrf_exempt
def pitcher_stats_handler(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        stat, created = PitcherStat.objects.update_or_create(
            player_id=data.get('player_id'),
            game_id=data.get('game_id'),
            defaults={
                'j': data.get('j', 0), 'a': data.get('a', 0), 'g': data.get('g', 0),
                'p': data.get('p', 0), 'jc': data.get('jc', 0), 'bl': data.get('bl', 0),
                'sv': data.get('sv', 0), 'il': data.get('il', 0.0), 'h': data.get('h', 0),
                'cl': data.get('cl', 0), 'so': data.get('so', 0)
            }
        )
        return JsonResponse({"message": "Pitcher actualizado correctamente"})

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
            "players": [{"id": p.id, "name": p.name, "number": p.number, "type": p.position_type} for p in players]
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

@csrf_exempt
def games_handler(request):
    if request.method == 'GET':
        games = Game.objects.all()
        data = [{
            "id": g.id,
            "opponent": g.opponent,
            "date": str(g.date),
            "season": g.season,
            "location": g.location
        } for g in games]
        return JsonResponse(data, safe=False, status=200)

    elif request.method == 'POST':
        try:
            body = json.loads(request.body)
            new_game = Game.objects.create(
                opponent=body['opponent'],
                date=body['date'], # Debe enviarse en formato YYYY-MM-DD
                season=body.get('season', '2026'),
                location=body['location']
            )
            return JsonResponse({"message": "Partido creado", "id": new_game.id}, status=201)
        except Exception as e:
            return JsonResponse({"error": str(e)}, status=400)

@csrf_exempt
def game_detail(request, game_id):
    try:
        game = Game.objects.get(id=game_id)
    except Game.DoesNotExist:
        return JsonResponse({"error": "Partido no encontrado"}, status=404)

    if request.method == 'PUT':
        body = json.loads(request.body)
        game.team_score = body.get('team_score', game.team_score)
        game.opponent_score = body.get('opponent_score', game.opponent_score)
        game.save()
        return JsonResponse({"message": "Resultado guardado"})

    elif request.method == 'GET':
        return JsonResponse({
            "id": game.id,
            "opponent": game.opponent,
            "date": game.date,
            "season": game.season,
            "location": game.location,
            "team_score": game.team_score,
            "opponent_score": game.opponent_score
        })

    # Opcional: Responder si el método no es ni GET ni PUT
    return JsonResponse({"error": "Método no permitido"}, status=405)

@csrf_exempt
def get_player_game_stats(request, game_id, player_id):
    if request.method == 'GET':
        try:
            f = FielderStat.objects.get(game_id=game_id, player_id=player_id)
            return JsonResponse({"j": f.j, "tb": f.tb, "c": f.c, "h": f.h, "h2": f.h2, "h3": f.h3, "h4": f.h4, "cis": f.cis, "bb": f.bb, "bbi": f.bbi, "so": f.so, "br": f.br, "ar": f.ar})
        except FielderStat.DoesNotExist:
            pass

        try:
            p = PitcherStat.objects.get(game_id=game_id, player_id=player_id)
            return JsonResponse({"j": p.j, "a": p.a, "g": p.g, "p": p.p, "jc": p.jc, "bl": p.bl, "sv": p.sv, "il": p.il, "h": p.h, "cl": p.cl, "so": p.so})
        except PitcherStat.DoesNotExist:
            return JsonResponse({"error": "No stats"}, status=404)

