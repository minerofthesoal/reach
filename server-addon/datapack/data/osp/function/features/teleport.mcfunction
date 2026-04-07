# Teleport to stored coordinates
# Note: sendCommandFeedback is kept false globally (set in install.mcfunction)
# to prevent /trigger spam in player chat

# Store coords to data storage for macro
execute store result storage osp:tp x int 1 run scoreboard players get @s osp.tp_x
execute store result storage osp:tp y int 1 run scoreboard players get @s osp.tp_y
execute store result storage osp:tp z int 1 run scoreboard players get @s osp.tp_z

# Execute teleport via macro
function osp:features/macros/teleport with storage osp:tp

# Reset all TP scores to prevent re-triggering
scoreboard players set @s osp.tp 0
scoreboard players set @s osp.tp_x 0
scoreboard players set @s osp.tp_y 0
scoreboard players set @s osp.tp_z 0
