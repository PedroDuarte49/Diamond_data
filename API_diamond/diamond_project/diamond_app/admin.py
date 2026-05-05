from django.contrib import admin
from .models import Player, Game, FielderStat, PitcherStat, Team

admin.site.register(Team)
admin.site.register(Player)
admin.site.register(Game)
admin.site.register(FielderStat)
admin.site.register(PitcherStat)