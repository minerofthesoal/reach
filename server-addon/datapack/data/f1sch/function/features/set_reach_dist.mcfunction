# Store reach distance value (x10) and reapply if active
title @s actionbar [{"text":"[f1sch] ","color":"gold"},{"text":"Reach distance: ","color":"gray"},{"score":{"name":"@s","objective":"f1sch.reach_dist"},"color":"aqua"},{"text":"/10 blocks","color":"gray"}]

# Reapply if reach is currently active
execute if score @s f1sch.reach_on matches 1 run attribute @s minecraft:block_interaction_range modifier remove reachfly:block_reach
execute if score @s f1sch.reach_on matches 1 run attribute @s minecraft:entity_interaction_range modifier remove reachfly:entity_reach
execute if score @s f1sch.reach_on matches 1 run execute store result storage f1sch:temp block_boost float 0.1 run scoreboard players get @s f1sch.reach_dist
execute if score @s f1sch.reach_on matches 1 run execute store result storage f1sch:temp entity_boost float 0.1 run scoreboard players get @s f1sch.reach_dist
execute if score @s f1sch.reach_on matches 1 run function f1sch:features/macros/apply_reach with storage f1sch:temp

# Reset trigger so it doesn't re-run every tick
scoreboard players set @s f1sch.reach_dist 0
