# Toggle NoFall on/off

# Branch based on current state
execute if score @s f1sch.nofall_on matches 1 run function f1sch:features/nofall_off
execute unless score @s f1sch.nofall_on matches 1 run function f1sch:features/nofall_on_enable

# Reset trigger
scoreboard players set @s f1sch.nofall 0
