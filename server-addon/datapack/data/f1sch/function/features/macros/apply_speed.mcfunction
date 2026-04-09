# Macro: Apply speed attribute modifier
# Called with: {value: <float>}
# Base walking speed = 0.1, so boost = 0.1 * (multiplier - 1)
$return run attribute @s minecraft:movement_speed modifier add reachfly:speed_boost $(value) add_value
