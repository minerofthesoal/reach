# Toggle Speed on/off

# Remove existing modifier
attribute @s minecraft:movement_speed modifier remove reachfly:speed_boost

# Branch based on current state
execute if score @s f1sch.speed_on matches 1 run function f1sch:features/speed_off
execute unless score @s f1sch.speed_on matches 1 run function f1sch:features/speed_on

# Reset trigger
scoreboard players set @s f1sch.speed 0
