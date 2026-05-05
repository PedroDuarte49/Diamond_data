from django.db import models


class Team(models.Model):
    name = models.CharField(max_length=150)
    city = models.CharField(max_length=100)
    coach_name = models.CharField(max_length=100) # Para el caso de uso de Entrenador

    def __str__(self):
        return self.name

class Player(models.Model):
    POSITIONS = [('F', 'Fielder'), ('P', 'Pitcher')]
    name = models.CharField(max_length=100)
    number = models.IntegerField()
    position_type = models.CharField(max_length=1, choices=POSITIONS)
    # Vinculamos al jugador con un equipo
    team = models.ForeignKey(Team, on_delete=models.CASCADE, related_name='players', null=True, blank=True)

    def __str__(self):
        return f"{self.name} ({self.team.name if self.team else 'Sin equipo'})"


class Game(models.Model):
    opponent = models.CharField(max_length=100)
    date = models.DateField()
    season = models.CharField(max_length=10, default="2026")
    location = models.CharField(max_length=100)


# Relación N:N para Bateadores
class FielderStat(models.Model):
    player = models.ForeignKey(Player, on_delete=models.CASCADE)
    game = models.ForeignKey(Game, on_delete=models.CASCADE)

    j = models.IntegerField(default=0)  # Juegos
    tb = models.IntegerField(default=0)  # Turnos al Bate
    c = models.IntegerField(default=0)  # Carreras
    h = models.IntegerField(default=0)  # Hits
    h2 = models.IntegerField(default=0)  # 2B
    h3 = models.IntegerField(default=0)  # 3B
    h4 = models.IntegerField(default=0)  # Home Runs
    cis = models.IntegerField(default=0)  # Carreras Impulsadas
    bb = models.IntegerField(default=0)  # Bases por Bola
    bbi = models.IntegerField(default=0)  # BB Intencional
    so = models.IntegerField(default=0)  # Strikeouts
    br = models.IntegerField(default=0)  # Bases Robadas
    ar = models.IntegerField(default=0)  # Atrapado Robando
    pro = models.FloatField(default=0.0)  # Promedio (AVG)


# Relación N:N para Pitchers
class PitcherStat(models.Model):
    player = models.ForeignKey(Player, on_delete=models.CASCADE)
    game = models.ForeignKey(Game, on_delete=models.CASCADE)

    g = models.IntegerField(default=0)  # Ganados
    p = models.IntegerField(default=0)  # Perdidos
    pcl = models.FloatField(default=0.0)  # Efectividad (ERA)
    j = models.IntegerField(default=0)  # Juegos
    a = models.IntegerField(default=0)  # Aperturas
    jc = models.IntegerField(default=0)  # Juegos Completos
    bl = models.IntegerField(default=0)  # Blanqueadas
    sv = models.IntegerField(default=0)  # Salvados
    il = models.FloatField(default=0.0)  # Entradas Lanzadas
    h = models.IntegerField(default=0)  # Hits permitidos
    cl = models.IntegerField(default=0)  # Carreras Limpias
    so = models.IntegerField(default=0)  # Strikeouts
    # ... puedes añadir el resto siguiendo el mismo patrón
