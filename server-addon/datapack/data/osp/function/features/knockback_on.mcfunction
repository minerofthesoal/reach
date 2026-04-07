# Enable knockback
# Default strength to 5 if not set (equivalent to Knockback V enchantment)
execute unless score @s osp.kb_str matches 1.. run scoreboard players set @s osp.kb_str 5

# Apply the attribute modifier via macro
execute store result storage osp:temp value int 1 run scoreboard players get @s osp.kb_str
function osp:features/macros/apply_knockback with storage osp:temp

scoreboard players set @s osp.kb_on 1

# Tag the player so the knockback_hit advancement can check
tag @s add osp.kb_active

tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Knockback ","color":"red"},{"text":"enabled ","color":"green"},{"text":"(","color":"gray"},{"score":{"name":"@s","objective":"osp.kb_str"},"color":"yellow"},{"text":")","color":"gray"}]
