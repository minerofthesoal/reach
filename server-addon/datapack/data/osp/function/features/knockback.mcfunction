# Toggle Knockback on/off

# Remove existing modifier either way
attribute @s minecraft:attack_knockback modifier remove reachfly:knockback_boost

# If currently ON -> turn OFF
execute if score @s osp.kb_on matches 1 run scoreboard players set @s osp.kb_on 0
execute if score @s osp.kb_on matches 1 run tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Knockback ","color":"red"},{"text":"disabled","color":"gray"}]

# If currently OFF -> turn ON
execute if score @s osp.kb_on matches 0 unless score @s osp.kb_str matches 1.. run scoreboard players set @s osp.kb_str 50
execute if score @s osp.kb_on matches 0 run execute store result storage osp:temp value int 1 run scoreboard players get @s osp.kb_str
execute if score @s osp.kb_on matches 0 run function osp:features/macros/apply_knockback with storage osp:temp
execute if score @s osp.kb_on matches 0 run scoreboard players set @s osp.kb_on 1
execute if score @s osp.kb_on matches 0 run tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Knockback ","color":"red"},{"text":"enabled ","color":"green"},{"text":"(","color":"gray"},{"score":{"name":"@s","objective":"osp.kb_str"},"color":"yellow"},{"text":")","color":"gray"}]

# Reset trigger
scoreboard players set @s osp.knockback 0
