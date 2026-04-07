# Enable nofall - set fall damage multiplier to 0 and safe fall distance very high
attribute @s minecraft:fall_damage_multiplier modifier remove reachfly:nofall
attribute @s minecraft:fall_damage_multiplier modifier add reachfly:nofall -1 add_multiplied_base
attribute @s minecraft:safe_fall_distance modifier remove reachfly:nofall_safe
attribute @s minecraft:safe_fall_distance modifier add reachfly:nofall_safe 999 add_value
scoreboard players set @s f1sch.nofall_on 1
tellraw @s [{"text":"[f1sch] ","color":"gold"},{"text":"NoFall ","color":"blue"},{"text":"enabled","color":"green"}]
