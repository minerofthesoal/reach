# ============================================================
# OSP Help - Show all available commands
# ============================================================

tellraw @s [{"text":"\n"},{"text":"═══ OSP Server Addon v2 ═══","color":"gold","bold":true}]
tellraw @s [{"text":""},{"text":"\n▸ Knockback","color":"red","bold":true}]
tellraw @s [{"text":"  /trigger osp.kb_str set <1-2500>","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.kb_str set 50"}},{"text":" - Set strength","color":"gray"}]
tellraw @s [{"text":"  /trigger osp.knockback set 1","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.knockback set 1"}},{"text":" - Toggle on/off","color":"gray"}]

tellraw @s [{"text":""},{"text":"\n▸ Reach","color":"aqua","bold":true}]
tellraw @s [{"text":"  /trigger osp.reach_dist set <30-500>","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.reach_dist set 100"}},{"text":" - Set distance (x10, e.g. 100 = 10 blocks)","color":"gray"}]
tellraw @s [{"text":"  /trigger osp.reach set 1","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.reach set 1"}},{"text":" - Toggle on/off","color":"gray"}]

tellraw @s [{"text":""},{"text":"\n▸ Speed","color":"green","bold":true}]
tellraw @s [{"text":"  /trigger osp.speed_mult set <10-100>","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.speed_mult set 20"}},{"text":" - Set multiplier (x10, e.g. 20 = 2.0x)","color":"gray"}]
tellraw @s [{"text":"  /trigger osp.speed set 1","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.speed set 1"}},{"text":" - Toggle on/off","color":"gray"}]

tellraw @s [{"text":""},{"text":"\n▸ NoFall","color":"blue","bold":true}]
tellraw @s [{"text":"  /trigger osp.nofall set 1","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.nofall set 1"}},{"text":" - Toggle on/off (zero fall damage)","color":"gray"}]

tellraw @s [{"text":""},{"text":"\n▸ Fly","color":"light_purple","bold":true}]
tellraw @s [{"text":"  /trigger osp.fly_speed set <10-100>","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.fly_speed set 15"}},{"text":" - Set speed (x10, e.g. 15 = 1.5x)","color":"gray"}]
tellraw @s [{"text":"  /trigger osp.fly set 1","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.fly set 1"}},{"text":" - Toggle on/off","color":"gray"}]

tellraw @s [{"text":""},{"text":"\n▸ Teleport","color":"dark_green","bold":true}]
tellraw @s [{"text":"  /trigger osp.tp_x set <x>","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.tp_x set 0"}},{"text":" - Set X coord","color":"gray"}]
tellraw @s [{"text":"  /trigger osp.tp_y set <y>","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.tp_y set 100"}},{"text":" - Set Y coord","color":"gray"}]
tellraw @s [{"text":"  /trigger osp.tp_z set <z>","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.tp_z set 0"}},{"text":" - Set Z coord","color":"gray"}]
tellraw @s [{"text":"  /trigger osp.tp set 1","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.tp set 1"}},{"text":" - Teleport now!","color":"gray"}]

tellraw @s [{"text":""},{"text":"\n  Tip: ","color":"gray"},{"text":"Click","color":"yellow","bold":true},{"text":" any command above to auto-fill it!","color":"gray"},{"text":"\n"}]
