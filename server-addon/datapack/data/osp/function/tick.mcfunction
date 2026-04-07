# ============================================================
# OSP Server Addon v3 - Main tick function (runs every tick)
# ============================================================

# Enable triggers for all players each tick
scoreboard players enable @a osp.knockback
scoreboard players enable @a osp.reach
scoreboard players enable @a osp.speed
scoreboard players enable @a osp.nofall
scoreboard players enable @a osp.fly
scoreboard players enable @a osp.tp
scoreboard players enable @a osp.kb_str
scoreboard players enable @a osp.reach_dist
scoreboard players enable @a osp.speed_mult
scoreboard players enable @a osp.fly_speed
scoreboard players enable @a osp.tp_x
scoreboard players enable @a osp.tp_y
scoreboard players enable @a osp.tp_z
scoreboard players enable @a osp.help

# Process help requests
execute as @a[scores={osp.help=1..}] run function osp:help
execute as @a[scores={osp.help=1..}] run scoreboard players set @s osp.help 0

# Process value-setting triggers (these now reset their own scores)
execute as @a[scores={osp.kb_str=1..}] run function osp:features/set_knockback_str
execute as @a[scores={osp.reach_dist=1..}] run function osp:features/set_reach_dist
execute as @a[scores={osp.speed_mult=1..}] run function osp:features/set_speed_mult
execute as @a[scores={osp.fly_speed=1..}] run function osp:features/set_fly_speed

# Process feature toggles
execute as @a[scores={osp.knockback=1..}] run function osp:features/knockback
execute as @a[scores={osp.reach=1..}] run function osp:features/reach
execute as @a[scores={osp.speed=1..}] run function osp:features/speed
execute as @a[scores={osp.nofall=1..}] run function osp:features/nofall
execute as @a[scores={osp.fly=1..}] run function osp:features/fly
execute as @a[scores={osp.tp=1..}] run function osp:features/teleport

# Apply ongoing effects for enabled features
execute as @a[scores={osp.nofall_on=1}] run function osp:features/nofall_tick
execute as @a[scores={osp.fly_on=1}] run function osp:features/fly_tick
