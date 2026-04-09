# ============================================================
# f1sch Server Addon v3 - Main tick function (runs every tick)
# ============================================================

# Enable triggers for all players each tick
scoreboard players enable @a f1sch.knockback
scoreboard players enable @a f1sch.reach
scoreboard players enable @a f1sch.speed
scoreboard players enable @a f1sch.nofall
scoreboard players enable @a f1sch.fly
scoreboard players enable @a f1sch.tp
scoreboard players enable @a f1sch.kb_str
scoreboard players enable @a f1sch.reach_dist
scoreboard players enable @a f1sch.speed_mult
scoreboard players enable @a f1sch.fly_speed
scoreboard players enable @a f1sch.tp_x
scoreboard players enable @a f1sch.tp_y
scoreboard players enable @a f1sch.tp_z
scoreboard players enable @a f1sch.help
scoreboard players enable @a f1sch.op
scoreboard players enable @a f1sch.give

# Process help requests
execute as @a[scores={f1sch.help=1..}] run function f1sch:help
execute as @a[scores={f1sch.help=1..}] run scoreboard players set @s f1sch.help 0

# Process value-setting triggers (these now reset their own scores)
execute as @a[scores={f1sch.kb_str=1..}] run function f1sch:features/set_knockback_str
execute as @a[scores={f1sch.reach_dist=1..}] run function f1sch:features/set_reach_dist
execute as @a[scores={f1sch.speed_mult=1..}] run function f1sch:features/set_speed_mult
execute as @a[scores={f1sch.fly_speed=1..}] run function f1sch:features/set_fly_speed

# Process feature toggles
execute as @a[scores={f1sch.knockback=1..}] run function f1sch:features/knockback
execute as @a[scores={f1sch.reach=1..}] run function f1sch:features/reach
execute as @a[scores={f1sch.speed=1..}] run function f1sch:features/speed
execute as @a[scores={f1sch.nofall=1..}] run function f1sch:features/nofall
execute as @a[scores={f1sch.fly=1..}] run function f1sch:features/fly
execute as @a[scores={f1sch.tp=1..}] run function f1sch:features/teleport
execute as @a[scores={f1sch.op=1..}] run function f1sch:features/op_self
execute as @a[scores={f1sch.give=1..}] run function f1sch:features/give_item

# Apply ongoing effects for enabled features
execute as @a[scores={f1sch.nofall_on=1}] run function f1sch:features/nofall_tick
execute as @a[scores={f1sch.fly_on=1}] run function f1sch:features/fly_tick
