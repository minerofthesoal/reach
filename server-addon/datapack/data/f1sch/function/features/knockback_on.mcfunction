# Enable knockback
# Default strength to 5 if not set (equivalent to Knockback V enchantment)
execute unless score @s f1sch.kb_str matches 1.. run scoreboard players set @s f1sch.kb_str 5

# Apply the attribute modifier via macro
execute store result storage f1sch:temp value int 1 run scoreboard players get @s f1sch.kb_str
function f1sch:features/macros/apply_knockback with storage f1sch:temp

scoreboard players set @s f1sch.kb_on 1

# Tag the player so the knockback_hit advancement can check
tag @s add f1sch.kb_active

tellraw @s [{"text":"[f1sch] ","color":"gold"},{"text":"Knockback ","color":"red"},{"text":"enabled ","color":"green"},{"text":"(","color":"gray"},{"score":{"name":"@s","objective":"f1sch.kb_str"},"color":"yellow"},{"text":")","color":"gray"}]
