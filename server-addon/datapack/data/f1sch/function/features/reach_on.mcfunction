# Enable reach
# Default reach dist = 100 (= 10.0 blocks)
execute unless score @s f1sch.reach_dist matches 1.. run scoreboard players set @s f1sch.reach_dist 100

# Store as float for macro
execute store result storage f1sch:temp block_boost float 0.1 run scoreboard players get @s f1sch.reach_dist
execute store result storage f1sch:temp entity_boost float 0.1 run scoreboard players get @s f1sch.reach_dist

# Apply via macro
function f1sch:features/macros/apply_reach with storage f1sch:temp

scoreboard players set @s f1sch.reach_on 1
tellraw @s [{"text":"[f1sch] ","color":"gold"},{"text":"Reach ","color":"aqua"},{"text":"enabled","color":"green"}]
