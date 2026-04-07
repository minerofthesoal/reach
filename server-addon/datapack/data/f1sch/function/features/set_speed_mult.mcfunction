# Store speed multiplier (x10) and reapply if active
title @s actionbar [{"text":"[f1sch] ","color":"gold"},{"text":"Speed multiplier: ","color":"gray"},{"score":{"name":"@s","objective":"f1sch.speed_mult"},"color":"green"},{"text":"/10x","color":"gray"}]

# Reapply if speed is currently active
execute if score @s f1sch.speed_on matches 1 run attribute @s minecraft:movement_speed modifier remove reachfly:speed_boost
execute if score @s f1sch.speed_on matches 1 run scoreboard players remove @s f1sch.speed_mult 10
execute if score @s f1sch.speed_on matches 1 run execute store result storage f1sch:temp value float 0.01 run scoreboard players get @s f1sch.speed_mult
execute if score @s f1sch.speed_on matches 1 run scoreboard players add @s f1sch.speed_mult 10
execute if score @s f1sch.speed_on matches 1 run function f1sch:features/macros/apply_speed with storage f1sch:temp

# Reset trigger so it doesn't re-run every tick
scoreboard players set @s f1sch.speed_mult 0
