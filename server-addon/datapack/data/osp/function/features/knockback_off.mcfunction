# Disable knockback
scoreboard players set @s osp.kb_on 0
tag @s remove osp.kb_active
tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Knockback ","color":"red"},{"text":"disabled","color":"gray"}]
