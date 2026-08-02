# Macro: Apply reach attribute modifiers
# Called with: {block_boost: <float>, entity_boost: <float>}
$attribute @s minecraft:block_interaction_range modifier add reachfly:block_reach $(block_boost) add_value
$return run attribute @s minecraft:entity_interaction_range modifier add reachfly:entity_reach $(entity_boost) add_value
