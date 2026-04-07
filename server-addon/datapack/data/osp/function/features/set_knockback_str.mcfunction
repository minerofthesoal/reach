# Store knockback strength and reapply if active
# Show confirmation as actionbar (no chat spam)
title @s actionbar [{"text":"[OSP] ","color":"gold"},{"text":"Knockback strength: ","color":"gray"},{"score":{"name":"@s","objective":"osp.kb_str"},"color":"red"}]

# Reapply if knockback is currently active
execute if score @s osp.kb_on matches 1 run attribute @s minecraft:attack_knockback modifier remove reachfly:knockback_boost
execute if score @s osp.kb_on matches 1 run execute store result storage osp:temp value int 1 run scoreboard players get @s osp.kb_str
execute if score @s osp.kb_on matches 1 run function osp:features/macros/apply_knockback with storage osp:temp

# Reset trigger so it doesn't re-run every tick
scoreboard players set @s osp.kb_str 0
