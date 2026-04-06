# Toggle Speed on/off

# Remove existing modifier
attribute @s minecraft:movement_speed modifier remove reachfly:speed_boost

# If currently ON -> turn OFF
execute if score @s osp.speed_on matches 1 run scoreboard players set @s osp.speed_on 0
execute if score @s osp.speed_on matches 1 run tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Speed ","color":"green"},{"text":"disabled","color":"gray"}]

# If currently OFF -> turn ON
# Default speed mult = 20 (= 2.0x)
execute if score @s osp.speed_on matches 0 unless score @s osp.speed_mult matches 1.. run scoreboard players set @s osp.speed_mult 20
# Base walking speed = 0.1, boost = 0.1 * (mult - 1)
# Score 20 = 2.0x -> boost = 0.1 * 1.0 = 0.1
# We store score/100 as the boost: score=20 -> 0.01 * (20-10) = 0.1
# Simpler: store (score-10) * 0.01 as boost
execute if score @s osp.speed_on matches 0 run scoreboard players remove @s osp.speed_mult 10
execute if score @s osp.speed_on matches 0 run execute store result storage osp:temp value float 0.01 run scoreboard players get @s osp.speed_mult
execute if score @s osp.speed_on matches 0 run scoreboard players add @s osp.speed_mult 10
execute if score @s osp.speed_on matches 0 run function osp:features/macros/apply_speed with storage osp:temp
execute if score @s osp.speed_on matches 0 run scoreboard players set @s osp.speed_on 1
execute if score @s osp.speed_on matches 0 run tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Speed ","color":"green"},{"text":"enabled","color":"green"}]

# Reset trigger
scoreboard players set @s osp.speed 0
