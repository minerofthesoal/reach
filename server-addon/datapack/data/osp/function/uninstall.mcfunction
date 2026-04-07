# ============================================================
# OSP Server Addon v3 - Uninstall
# Removes all scoreboards, tags, and attribute modifiers
# ============================================================

# Remove all attribute modifiers from all players
execute as @a run attribute @s minecraft:attack_knockback modifier remove reachfly:knockback_boost
execute as @a run attribute @s minecraft:block_interaction_range modifier remove reachfly:block_reach
execute as @a run attribute @s minecraft:entity_interaction_range modifier remove reachfly:entity_reach
execute as @a run attribute @s minecraft:movement_speed modifier remove reachfly:speed_boost
execute as @a run attribute @s minecraft:fall_damage_multiplier modifier remove reachfly:nofall
execute as @a run attribute @s minecraft:safe_fall_distance modifier remove reachfly:nofall_safe
execute as @a run attribute @s minecraft:flying_speed modifier remove reachfly:fly_speed

# Remove tags
tag @a remove osp.kb_active

# Remove all scoreboards
scoreboard objectives remove osp.knockback
scoreboard objectives remove osp.reach
scoreboard objectives remove osp.speed
scoreboard objectives remove osp.nofall
scoreboard objectives remove osp.fly
scoreboard objectives remove osp.tp
scoreboard objectives remove osp.kb_str
scoreboard objectives remove osp.reach_dist
scoreboard objectives remove osp.speed_mult
scoreboard objectives remove osp.fly_speed
scoreboard objectives remove osp.tp_x
scoreboard objectives remove osp.tp_y
scoreboard objectives remove osp.tp_z
scoreboard objectives remove osp.kb_on
scoreboard objectives remove osp.reach_on
scoreboard objectives remove osp.speed_on
scoreboard objectives remove osp.nofall_on
scoreboard objectives remove osp.fly_on
scoreboard objectives remove osp.help
scoreboard objectives remove osp.op

# Clear data storage
data remove storage osp:temp {}
data remove storage osp:tp {}

# Restore gamerules
gamerule sendCommandFeedback true
gamerule logAdminCommands true

tellraw @a [{"text":"[OSP] ","color":"gold","bold":true},{"text":"Server Addon v3 uninstalled.","color":"red"}]
