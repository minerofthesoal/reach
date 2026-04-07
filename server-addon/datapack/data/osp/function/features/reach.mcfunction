# Toggle Reach on/off

# Remove existing modifiers
attribute @s minecraft:block_interaction_range modifier remove reachfly:block_reach
attribute @s minecraft:entity_interaction_range modifier remove reachfly:entity_reach

# Branch based on current state
execute if score @s osp.reach_on matches 1 run function osp:features/reach_off
execute unless score @s osp.reach_on matches 1 run function osp:features/reach_on

# Reset trigger
scoreboard players set @s osp.reach 0
