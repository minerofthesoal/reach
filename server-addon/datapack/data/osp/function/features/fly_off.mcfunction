# Disable fly
gamemode survival @s
scoreboard players set @s osp.fly_on 0
tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Fly ","color":"light_purple"},{"text":"disabled","color":"gray"}]
