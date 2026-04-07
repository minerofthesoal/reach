# Store fly speed (x10) and reapply if active
title @s actionbar [{"text":"[f1sch] ","color":"gold"},{"text":"Fly speed: ","color":"gray"},{"score":{"name":"@s","objective":"f1sch.fly_speed"},"color":"light_purple"},{"text":"/10x","color":"gray"}]

# Reapply if fly is currently active
execute if score @s f1sch.fly_on matches 1 run attribute @s minecraft:flying_speed modifier remove reachfly:fly_speed
execute if score @s f1sch.fly_on matches 1 run scoreboard players remove @s f1sch.fly_speed 10
execute if score @s f1sch.fly_on matches 1 run execute store result storage f1sch:temp value float 0.005 run scoreboard players get @s f1sch.fly_speed
execute if score @s f1sch.fly_on matches 1 run scoreboard players add @s f1sch.fly_speed 10
execute if score @s f1sch.fly_on matches 1 run function f1sch:features/macros/apply_fly_speed with storage f1sch:temp

# Reset trigger so it doesn't re-run every tick
scoreboard players set @s f1sch.fly_speed 0
