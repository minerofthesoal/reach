# Store speed multiplier (x10) and reapply if active
tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Speed multiplier: ","color":"gray"},{"score":{"name":"@s","objective":"osp.speed_mult"},"color":"green"},{"text":"/10x","color":"gray"}]

# Reapply if speed is currently active
execute if score @s osp.speed_on matches 1 run attribute @s minecraft:movement_speed modifier remove reachfly:speed_boost
execute if score @s osp.speed_on matches 1 run scoreboard players remove @s osp.speed_mult 10
execute if score @s osp.speed_on matches 1 run execute store result storage osp:temp value float 0.01 run scoreboard players get @s osp.speed_mult
execute if score @s osp.speed_on matches 1 run scoreboard players add @s osp.speed_mult 10
execute if score @s osp.speed_on matches 1 run function osp:features/macros/apply_speed with storage osp:temp
