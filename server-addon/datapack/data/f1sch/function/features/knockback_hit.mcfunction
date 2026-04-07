# Runs when ANY player hurts an entity (advancement reward)
# Revoke immediately so it can trigger again
advancement revoke @s only f1sch:knockback_hit

# Only apply extra knockback if this player has it enabled
execute unless entity @s[tag=f1sch.kb_active] run return 0

# Find the nearest non-player entity that was just hurt (HurtTime = 10 on damage)
# Apply upward + horizontal velocity to launch them
# The attack_knockback attribute handles base knockback, this amplifies it
execute at @s as @e[type=!player,distance=..6,sort=nearest,limit=1,nbt={HurtTime:10s}] run data modify entity @s Motion[1] set value 0.8d
