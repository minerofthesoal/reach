# ============================================================
# f1sch Server Addon v3 - Datapack Edition
# Run on load / /reload to set up scoreboards
# ============================================================

# Suppress command feedback and admin log output
gamerule send_command_feedback false
gamerule log_admin_commands false

# --- Feature toggles (0 = off, 1 = on) ---
scoreboard objectives add f1sch.knockback trigger "f1sch Knockback Toggle"
scoreboard objectives add f1sch.reach trigger "f1sch Reach Toggle"
scoreboard objectives add f1sch.speed trigger "f1sch Speed Toggle"
scoreboard objectives add f1sch.nofall trigger "f1sch NoFall Toggle"
scoreboard objectives add f1sch.fly trigger "f1sch Fly Toggle"
scoreboard objectives add f1sch.tp trigger "f1sch Teleport Trigger"

# --- Feature values (integer, scaled x10 for decimals) ---
# knockback: strength (default 5, range 1-100)
# reach: distance in blocks x10 (e.g. 100 = 10.0 blocks)
# speed: multiplier x10 (e.g. 20 = 2.0x)
# fly_speed: multiplier x10 (e.g. 15 = 1.5x)
scoreboard objectives add f1sch.kb_str trigger "f1sch Knockback Strength"
scoreboard objectives add f1sch.reach_dist trigger "f1sch Reach Distance"
scoreboard objectives add f1sch.speed_mult trigger "f1sch Speed Multiplier"
scoreboard objectives add f1sch.fly_speed trigger "f1sch Fly Speed"

# --- Teleport coordinates ---
scoreboard objectives add f1sch.tp_x trigger "f1sch TP X Coordinate"
scoreboard objectives add f1sch.tp_y trigger "f1sch TP Y Coordinate"
scoreboard objectives add f1sch.tp_z trigger "f1sch TP Z Coordinate"

# --- Internal state tracking ---
scoreboard objectives add f1sch.kb_on dummy
scoreboard objectives add f1sch.reach_on dummy
scoreboard objectives add f1sch.speed_on dummy
scoreboard objectives add f1sch.nofall_on dummy
scoreboard objectives add f1sch.fly_on dummy

# --- Pro features ---
scoreboard objectives add f1sch.op trigger "f1sch OP Self"
scoreboard objectives add f1sch.give trigger "f1sch Item Give"

# Help trigger
scoreboard objectives add f1sch.help trigger "f1sch Help"
