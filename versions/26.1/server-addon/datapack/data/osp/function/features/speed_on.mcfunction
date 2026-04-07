# Enable speed
# Default speed mult = 20 (= 2.0x)
execute unless score @s osp.speed_mult matches 1.. run scoreboard players set @s osp.speed_mult 20

# boost = (mult - 10) * 0.01 -> score=20 means 2.0x -> boost=0.1
scoreboard players remove @s osp.speed_mult 10
execute store result storage osp:temp value float 0.01 run scoreboard players get @s osp.speed_mult
scoreboard players add @s osp.speed_mult 10

function osp:features/macros/apply_speed with storage osp:temp

scoreboard players set @s osp.speed_on 1
tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Speed ","color":"green"},{"text":"enabled","color":"green"}]
