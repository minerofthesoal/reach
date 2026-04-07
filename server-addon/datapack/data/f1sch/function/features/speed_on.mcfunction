# Enable speed
# Default speed mult = 20 (= 2.0x)
execute unless score @s f1sch.speed_mult matches 1.. run scoreboard players set @s f1sch.speed_mult 20

# boost = (mult - 10) * 0.01 -> score=20 means 2.0x -> boost=0.1
scoreboard players remove @s f1sch.speed_mult 10
execute store result storage f1sch:temp value float 0.01 run scoreboard players get @s f1sch.speed_mult
scoreboard players add @s f1sch.speed_mult 10

function f1sch:features/macros/apply_speed with storage f1sch:temp

scoreboard players set @s f1sch.speed_on 1
tellraw @s [{"text":"[f1sch] ","color":"gold"},{"text":"Speed ","color":"green"},{"text":"enabled","color":"green"}]
