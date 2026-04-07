# ============================================================
# OSP Server Addon v3 - Datapack Edition
# Run on load / /reload to set up scoreboards
# ============================================================

# Suppress trigger feedback in chat
gamerule sendCommandFeedback false

# --- Feature toggles (0 = off, 1 = on) ---
scoreboard objectives add osp.knockback trigger "OSP Knockback Toggle"
scoreboard objectives add osp.reach trigger "OSP Reach Toggle"
scoreboard objectives add osp.speed trigger "OSP Speed Toggle"
scoreboard objectives add osp.nofall trigger "OSP NoFall Toggle"
scoreboard objectives add osp.fly trigger "OSP Fly Toggle"
scoreboard objectives add osp.tp trigger "OSP Teleport Trigger"

# --- Feature values (integer, scaled x10 for decimals) ---
# knockback: strength (default 0, range 1-2500)
# reach: distance in blocks x10 (e.g. 100 = 10.0 blocks)
# speed: multiplier x10 (e.g. 20 = 2.0x)
# fly_speed: multiplier x10 (e.g. 15 = 1.5x)
scoreboard objectives add osp.kb_str trigger "OSP Knockback Strength"
scoreboard objectives add osp.reach_dist trigger "OSP Reach Distance"
scoreboard objectives add osp.speed_mult trigger "OSP Speed Multiplier"
scoreboard objectives add osp.fly_speed trigger "OSP Fly Speed"

# --- Teleport coordinates ---
scoreboard objectives add osp.tp_x trigger "OSP TP X Coordinate"
scoreboard objectives add osp.tp_y trigger "OSP TP Y Coordinate"
scoreboard objectives add osp.tp_z trigger "OSP TP Z Coordinate"

# --- Internal state tracking ---
scoreboard objectives add osp.kb_on dummy
scoreboard objectives add osp.reach_on dummy
scoreboard objectives add osp.speed_on dummy
scoreboard objectives add osp.nofall_on dummy
scoreboard objectives add osp.fly_on dummy

tellraw @a [{"text":"[OSP] ","color":"gold","bold":true},{"text":"Server Addon v3 (Datapack) loaded!","color":"green"},{"text":"\n"},{"text":"  Use ","color":"gray"},{"text":"/trigger osp.help","color":"yellow","clickEvent":{"action":"run_command","value":"/trigger osp.help"}},{"text":" for commands","color":"gray"}]

# Help trigger
scoreboard objectives add osp.help trigger "OSP Help"
