# Teleport to stored coordinates
# Note: sendCommandFeedback is kept false globally (set in install.mcfunction)
# to prevent /trigger spam in player chat

# Store coords to data storage for macro
execute store result storage f1sch:tp x int 1 run scoreboard players get @s f1sch.tp_x
execute store result storage f1sch:tp y int 1 run scoreboard players get @s f1sch.tp_y
execute store result storage f1sch:tp z int 1 run scoreboard players get @s f1sch.tp_z

# Execute teleport via macro
function f1sch:features/macros/teleport with storage f1sch:tp

# Reset all TP scores to prevent re-triggering
scoreboard players set @s f1sch.tp 0
scoreboard players set @s f1sch.tp_x 0
scoreboard players set @s f1sch.tp_y 0
scoreboard players set @s f1sch.tp_z 0
