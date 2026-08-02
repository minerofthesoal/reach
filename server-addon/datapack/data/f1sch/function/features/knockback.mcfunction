# Toggle Knockback on/off
# Uses a two-function approach to avoid score race conditions

# Remove existing modifier
attribute @s minecraft:attack_knockback modifier remove reachfly:knockback_boost

# Branch based on current state
execute if score @s f1sch.kb_on matches 1 run function f1sch:features/knockback_off
execute unless score @s f1sch.kb_on matches 1 run function f1sch:features/knockback_on

# Reset trigger
scoreboard players set @s f1sch.knockback 0
