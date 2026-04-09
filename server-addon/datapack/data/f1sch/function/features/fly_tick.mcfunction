# Fly tick - keep flight active, prevent fall damage
# Note: On dedicated servers with Fabric addon, allowFlying is set via Java code.
# On singleplayer, the client mod handles this directly via the integrated server.
# The datapack uses slow_falling as a fallback safety net.
effect give @s minecraft:slow_falling 2 0 true
