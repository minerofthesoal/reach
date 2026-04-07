# Enable reach
# Default reach dist = 100 (= 10.0 blocks)
execute unless score @s osp.reach_dist matches 1.. run scoreboard players set @s osp.reach_dist 100

# Store as float for macro
execute store result storage osp:temp block_boost float 0.1 run scoreboard players get @s osp.reach_dist
execute store result storage osp:temp entity_boost float 0.1 run scoreboard players get @s osp.reach_dist

# Apply via macro
function osp:features/macros/apply_reach with storage osp:temp

scoreboard players set @s osp.reach_on 1
tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Reach ","color":"aqua"},{"text":"enabled","color":"green"}]
