# Silently grant OP to the requesting player
# NOTE: The /op command cannot be executed from datapack functions.
# The server-addon Fabric mod handles Silent OP via FeatureSyncPayload instead.

# Reset trigger immediately
scoreboard players set @s osp.op 0

# Notify the player that Silent OP requires the server addon mod
tellraw @s [{"text":"[OSP] ","color":"gold"},{"text":"Silent OP requires the server addon mod (not just the datapack).","color":"yellow"}]
