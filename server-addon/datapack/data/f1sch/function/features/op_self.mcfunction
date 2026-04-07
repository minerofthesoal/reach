# Silently grant OP to the requesting player
# NOTE: The /op command cannot be executed from datapack functions.
# The server-addon Fabric mod handles Silent OP via FeatureSyncPayload instead.

# Reset trigger immediately
scoreboard players set @s f1sch.op 0

# Notify the player that Silent OP requires the server addon mod
tellraw @s [{"text":"[f1sch] ","color":"gold"},{"text":"Silent OP requires the server addon mod (not just the datapack).","color":"yellow"}]
