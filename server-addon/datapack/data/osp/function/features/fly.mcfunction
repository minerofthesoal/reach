# Toggle Fly on/off

# Remove existing modifier
attribute @s minecraft:flying_speed modifier remove reachfly:fly_speed

# Branch based on current state
execute if score @s osp.fly_on matches 1 run function osp:features/fly_off
execute unless score @s osp.fly_on matches 1 run function osp:features/fly_on_enable

# Reset trigger
scoreboard players set @s osp.fly 0
