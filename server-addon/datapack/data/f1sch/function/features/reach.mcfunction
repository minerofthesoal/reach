# Toggle Reach on/off

# Remove existing modifiers
attribute @s minecraft:block_interaction_range modifier remove reachfly:block_reach
attribute @s minecraft:entity_interaction_range modifier remove reachfly:entity_reach

# Branch based on current state
execute if score @s f1sch.reach_on matches 1 run function f1sch:features/reach_off
execute unless score @s f1sch.reach_on matches 1 run function f1sch:features/reach_on

# Reset trigger
scoreboard players set @s f1sch.reach 0
