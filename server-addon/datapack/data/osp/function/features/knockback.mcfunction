# Toggle Knockback on/off
# Uses a two-function approach to avoid score race conditions

# Remove existing modifier
attribute @s minecraft:attack_knockback modifier remove reachfly:knockback_boost

# Branch based on current state
execute if score @s osp.kb_on matches 1 run function osp:features/knockback_off
execute unless score @s osp.kb_on matches 1 run function osp:features/knockback_on

# Reset trigger
scoreboard players set @s osp.knockback 0
