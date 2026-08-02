# Disable knockback
scoreboard players set @s f1sch.kb_on 0
tag @s remove f1sch.kb_active
tellraw @s [{"text":"[f1sch] ","color":"gold"},{"text":"Knockback ","color":"red"},{"text":"disabled","color":"gray"}]
