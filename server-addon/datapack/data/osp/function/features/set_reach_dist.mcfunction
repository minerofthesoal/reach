# Store reach distance value (x10) and reapply if active
tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Reach distance: ","color":"gray"},{"score":{"name":"@s","objective":"osp.reach_dist"},"color":"aqua"},{"text":"/10 blocks","color":"gray"}]

# Reapply if reach is currently active
execute if score @s osp.reach_on matches 1 run attribute @s minecraft:block_interaction_range modifier remove reachfly:block_reach
execute if score @s osp.reach_on matches 1 run attribute @s minecraft:entity_interaction_range modifier remove reachfly:entity_reach
execute if score @s osp.reach_on matches 1 run execute store result storage osp:temp block_boost float 0.1 run scoreboard players get @s osp.reach_dist
execute if score @s osp.reach_on matches 1 run execute store result storage osp:temp entity_boost float 0.1 run scoreboard players get @s osp.reach_dist
execute if score @s osp.reach_on matches 1 run function osp:features/macros/apply_reach with storage osp:temp
