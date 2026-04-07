# Toggle NoFall on/off

# Branch based on current state
execute if score @s osp.nofall_on matches 1 run function osp:features/nofall_off
execute unless score @s osp.nofall_on matches 1 run function osp:features/nofall_on_enable

# Reset trigger
scoreboard players set @s osp.nofall 0
