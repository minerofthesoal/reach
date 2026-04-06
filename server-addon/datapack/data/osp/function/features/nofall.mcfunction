# Toggle NoFall on/off

# If currently ON -> turn OFF
execute if score @s osp.nofall_on matches 1 run attribute @s minecraft:fall_damage_multiplier modifier remove reachfly:nofall
execute if score @s osp.nofall_on matches 1 run attribute @s minecraft:safe_fall_distance modifier remove reachfly:nofall_safe
execute if score @s osp.nofall_on matches 1 run scoreboard players set @s osp.nofall_on 0
execute if score @s osp.nofall_on matches 1 run tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"NoFall ","color":"blue"},{"text":"disabled","color":"gray"}]

# If currently OFF -> turn ON
# Set fall damage multiplier to 0 and safe fall distance very high
execute if score @s osp.nofall_on matches 0 run attribute @s minecraft:fall_damage_multiplier modifier remove reachfly:nofall
execute if score @s osp.nofall_on matches 0 run attribute @s minecraft:fall_damage_multiplier modifier add reachfly:nofall -1 multiply_base
execute if score @s osp.nofall_on matches 0 run attribute @s minecraft:safe_fall_distance modifier remove reachfly:nofall_safe
execute if score @s osp.nofall_on matches 0 run attribute @s minecraft:safe_fall_distance modifier add reachfly:nofall_safe 999 add_value
execute if score @s osp.nofall_on matches 0 run scoreboard players set @s osp.nofall_on 1
execute if score @s osp.nofall_on matches 0 run tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"NoFall ","color":"blue"},{"text":"enabled","color":"green"}]

# Reset trigger
scoreboard players set @s osp.nofall 0
