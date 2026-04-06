# Macro: Teleport to stored coordinates
# Called with: {x: <int>, y: <int>, z: <int>}
$tp @s $(x) $(y) $(z)
$tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Teleported to ","color":"green"},{"text":"$(x), $(y), $(z)","color":"yellow"}]
