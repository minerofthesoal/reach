# Disable nofall
attribute @s minecraft:fall_damage_multiplier modifier remove reachfly:nofall
attribute @s minecraft:safe_fall_distance modifier remove reachfly:nofall_safe
scoreboard players set @s f1sch.nofall_on 0
tellraw @s [{"text":"[f1sch] ","color":"gold"},{"text":"NoFall ","color":"blue"},{"text":"disabled","color":"gray"}]
