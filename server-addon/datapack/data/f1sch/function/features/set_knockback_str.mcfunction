# Store knockback strength and reapply if active
# Show confirmation as actionbar (no chat spam)
title @s actionbar [{"text":"[f1sch] ","color":"gold"},{"text":"Knockback strength: ","color":"gray"},{"score":{"name":"@s","objective":"f1sch.kb_str"},"color":"red"}]

# Reapply if knockback is currently active
execute if score @s f1sch.kb_on matches 1 run attribute @s minecraft:attack_knockback modifier remove reachfly:knockback_boost
execute if score @s f1sch.kb_on matches 1 run execute store result storage f1sch:temp value int 1 run scoreboard players get @s f1sch.kb_str
execute if score @s f1sch.kb_on matches 1 run function f1sch:features/macros/apply_knockback with storage f1sch:temp

# Reset trigger so it doesn't re-run every tick
scoreboard players set @s f1sch.kb_str 0
