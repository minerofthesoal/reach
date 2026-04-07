# Enable knockback
# Default strength to 50 if not set
execute unless score @s osp.kb_str matches 1.. run scoreboard players set @s osp.kb_str 50

# Apply the modifier via macro
execute store result storage osp:temp value int 1 run scoreboard players get @s osp.kb_str
function osp:features/macros/apply_knockback with storage osp:temp

scoreboard players set @s osp.kb_on 1
tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Knockback ","color":"red"},{"text":"enabled ","color":"green"},{"text":"(","color":"gray"},{"score":{"name":"@s","objective":"osp.kb_str"},"color":"yellow"},{"text":")","color":"gray"}]
