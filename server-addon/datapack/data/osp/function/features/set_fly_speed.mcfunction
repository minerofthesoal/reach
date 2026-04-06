# Store fly speed (x10) and reapply if active
tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Fly speed: ","color":"gray"},{"score":{"name":"@s","objective":"osp.fly_speed"},"color":"light_purple"},{"text":"/10x","color":"gray"}]

# Reapply if fly is currently active
execute if score @s osp.fly_on matches 1 run attribute @s minecraft:flying_speed modifier remove reachfly:fly_speed
execute if score @s osp.fly_on matches 1 run scoreboard players remove @s osp.fly_speed 10
execute if score @s osp.fly_on matches 1 run execute store result storage osp:temp value float 0.005 run scoreboard players get @s osp.fly_speed
execute if score @s osp.fly_on matches 1 run scoreboard players add @s osp.fly_speed 10
execute if score @s osp.fly_on matches 1 run function osp:features/macros/apply_fly_speed with storage osp:temp
