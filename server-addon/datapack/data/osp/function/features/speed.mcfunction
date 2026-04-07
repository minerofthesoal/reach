# Toggle Speed on/off

# Remove existing modifier
attribute @s minecraft:movement_speed modifier remove reachfly:speed_boost

# Branch based on current state
execute if score @s osp.speed_on matches 1 run function osp:features/speed_off
execute unless score @s osp.speed_on matches 1 run function osp:features/speed_on

# Reset trigger
scoreboard players set @s osp.speed 0
