# Teleport to stored coordinates

# Store coords to data storage for macro
execute store result storage osp:tp x int 1 run scoreboard players get @s osp.tp_x
execute store result storage osp:tp y int 1 run scoreboard players get @s osp.tp_y
execute store result storage osp:tp z int 1 run scoreboard players get @s osp.tp_z

# Execute teleport via macro
function osp:features/macros/teleport with storage osp:tp

# Reset trigger
scoreboard players set @s osp.tp 0
