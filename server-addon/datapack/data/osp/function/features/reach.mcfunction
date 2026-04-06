# Toggle Reach on/off

# Remove existing modifiers
attribute @s minecraft:block_interaction_range modifier remove reachfly:block_reach
attribute @s minecraft:entity_interaction_range modifier remove reachfly:entity_reach

# If currently ON -> turn OFF
execute if score @s osp.reach_on matches 1 run scoreboard players set @s osp.reach_on 0
execute if score @s osp.reach_on matches 1 run tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Reach ","color":"aqua"},{"text":"disabled","color":"gray"}]

# If currently OFF -> turn ON
# Default reach dist = 100 (= 10.0 blocks)
execute if score @s osp.reach_on matches 0 unless score @s osp.reach_dist matches 1.. run scoreboard players set @s osp.reach_dist 100
# Convert score/10 to block boost: score=100 -> 10.0 blocks -> block_boost=5.5 (10.0-4.5), entity_boost=7.0 (10.0-3.0)
# Store the reach distance, then compute boost in the macro
execute if score @s osp.reach_on matches 0 run execute store result storage osp:temp block_boost float 0.1 run scoreboard players get @s osp.reach_dist
execute if score @s osp.reach_on matches 0 run execute store result storage osp:temp entity_boost float 0.1 run scoreboard players get @s osp.reach_dist
# Subtract the vanilla defaults (4.5 for block, 3.0 for entity) - we add the full value as modifier
# Since we store dist/10 as float, modifier = (dist/10 - 4.5) for block, (dist/10 - 3.0) for entity
# Simpler: just set the modifier to the raw extra range
execute if score @s osp.reach_on matches 0 run function osp:features/macros/apply_reach with storage osp:temp
execute if score @s osp.reach_on matches 0 run scoreboard players set @s osp.reach_on 1
execute if score @s osp.reach_on matches 0 run tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Reach ","color":"aqua"},{"text":"enabled","color":"green"}]

# Reset trigger
scoreboard players set @s osp.reach 0
