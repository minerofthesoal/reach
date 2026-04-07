# Enable fly
# Default fly speed = 15 (= 1.5x)
execute unless score @s osp.fly_speed matches 1.. run scoreboard players set @s osp.fly_speed 15

# Fly speed boost: base=0.05, compute (score-10) * 0.005
scoreboard players remove @s osp.fly_speed 10
execute store result storage osp:temp value float 0.005 run scoreboard players get @s osp.fly_speed
scoreboard players add @s osp.fly_speed 10

function osp:features/macros/apply_fly_speed with storage osp:temp

gamemode creative @s
scoreboard players set @s osp.fly_on 1
tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Fly ","color":"light_purple"},{"text":"enabled ","color":"green"},{"text":"(creative flight)","color":"gray"}]
