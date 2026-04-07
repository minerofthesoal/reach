# Toggle Fly on/off

# Remove existing modifier
attribute @s minecraft:flying_speed modifier remove reachfly:fly_speed

# Branch based on current state
execute if score @s f1sch.fly_on matches 1 run function f1sch:features/fly_off
execute unless score @s f1sch.fly_on matches 1 run function f1sch:features/fly_on_enable

# Reset trigger
scoreboard players set @s f1sch.fly 0
