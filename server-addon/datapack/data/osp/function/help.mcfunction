# ============================================================
# OSP Help - Show all available commands
# ============================================================

tellraw @s [{"text":"\n"},{"text":"═══ OSP Server Addon v3 ═══","color":"gold","bold":true}]
tellraw @s [{"text":""},{"text":"\n▸ Knockback","color":"red","bold":true}]
tellraw @s [{"text":"  /trigger osp.kb_str set <1-100>","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.kb_str set 5"}},{"text":" - Set strength (default 5)","color":"gray"}]
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

tellraw @s [{"text":""},{"text":"\n▸ Item Give","color":"gold","bold":true}]
tellraw @s [{"text":"  /trigger osp.give set <code>","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.give set 1"}},{"text":" - Give item by code","color":"gray"}]
tellraw @s [{"text":"  Codes: ","color":"gray"},{"text":"1","color":"white"},{"text":"=Diamond ","color":"gray"},{"text":"2","color":"white"},{"text":"=Iron ","color":"gray"},{"text":"3","color":"white"},{"text":"=Gold ","color":"gray"},{"text":"4","color":"white"},{"text":"=Emerald ","color":"gray"},{"text":"5","color":"white"},{"text":"=Netherite","color":"gray"}]
tellraw @s [{"text":"  ","color":"gray"},{"text":"6","color":"white"},{"text":"=EnchGApple ","color":"gray"},{"text":"7","color":"white"},{"text":"=EnderPearl ","color":"gray"},{"text":"8","color":"white"},{"text":"=XPBottle ","color":"gray"},{"text":"9-15","color":"white"},{"text":"=DiaGear ","color":"gray"},{"text":"16-22","color":"white"},{"text":"=NetGear","color":"gray"}]
tellraw @s [{"text":"  ","color":"gray"},{"text":"23","color":"white"},{"text":"=Totem ","color":"gray"},{"text":"24","color":"white"},{"text":"=GApple ","color":"gray"},{"text":"25","color":"white"},{"text":"=Arrows ","color":"gray"},{"text":"26","color":"white"},{"text":"=Bow ","color":"gray"},{"text":"27","color":"white"},{"text":"=Crossbow ","color":"gray"},{"text":"28","color":"white"},{"text":"=Shield ","color":"gray"},{"text":"29","color":"white"},{"text":"=Elytra ","color":"gray"},{"text":"30","color":"white"},{"text":"=Fireworks","color":"gray"}]

tellraw @s [{"text":""},{"text":"\n▸ Silent OP","color":"dark_red","bold":true}]
tellraw @s [{"text":"  /trigger osp.op set 1","color":"yellow","clickEvent":{"action":"suggest_command","value":"/trigger osp.op set 1"}},{"text":" - Request OP (requires server addon mod)","color":"gray"}]

tellraw @s [{"text":""},{"text":"\n  Tip: ","color":"gray"},{"text":"Click","color":"yellow","bold":true},{"text":" any command above to auto-fill it!","color":"gray"},{"text":"\n"}]
