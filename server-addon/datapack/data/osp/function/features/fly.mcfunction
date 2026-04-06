# Toggle Fly on/off

# Remove existing modifier
attribute @s minecraft:flying_speed modifier remove reachfly:fly_speed

# If currently ON -> turn OFF
execute if score @s osp.fly_on matches 1 run gamemode survival @s
execute if score @s osp.fly_on matches 1 run scoreboard players set @s osp.fly_on 0
execute if score @s osp.fly_on matches 1 run tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Fly ","color":"light_purple"},{"text":"disabled","color":"gray"}]

# If currently OFF -> turn ON
# Default fly speed = 15 (= 1.5x)
execute if score @s osp.fly_on matches 0 unless score @s osp.fly_speed matches 1.. run scoreboard players set @s osp.fly_speed 15
# Fly speed boost: base=0.05, score=15 -> 1.5x -> boost = 0.05 * 0.5 = 0.025
# We compute (score-10) * 0.005 as the boost
execute if score @s osp.fly_on matches 0 run scoreboard players remove @s osp.fly_speed 10
execute if score @s osp.fly_on matches 0 run execute store result storage osp:temp value float 0.005 run scoreboard players get @s osp.fly_speed
execute if score @s osp.fly_on matches 0 run scoreboard players add @s osp.fly_speed 10
execute if score @s osp.fly_on matches 0 run function osp:features/macros/apply_fly_speed with storage osp:temp
# Use spectator-like approach: give levitation effect controlled by player
execute if score @s osp.fly_on matches 0 run gamemode creative @s
execute if score @s osp.fly_on matches 0 run scoreboard players set @s osp.fly_on 1
execute if score @s osp.fly_on matches 0 run tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Fly ","color":"light_purple"},{"text":"enabled ","color":"green"},{"text":"(creative flight)","color":"gray"}]

# Reset trigger
scoreboard players set @s osp.fly 0
