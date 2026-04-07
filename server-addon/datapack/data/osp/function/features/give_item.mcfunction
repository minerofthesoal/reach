# OSP Item Give - /trigger osp.give set <code>
# Use /trigger osp.help to see all codes
# The client mod's Item Give screen (Exploit tab) supports ALL items with search.

# === MATERIALS (1-20) ===
execute if entity @s[scores={osp.give=1}] run give @s minecraft:diamond 64
execute if entity @s[scores={osp.give=2}] run give @s minecraft:iron_ingot 64
execute if entity @s[scores={osp.give=3}] run give @s minecraft:gold_ingot 64
execute if entity @s[scores={osp.give=4}] run give @s minecraft:emerald 64
execute if entity @s[scores={osp.give=5}] run give @s minecraft:netherite_ingot 64
execute if entity @s[scores={osp.give=6}] run give @s minecraft:lapis_lazuli 64
execute if entity @s[scores={osp.give=7}] run give @s minecraft:redstone 64
execute if entity @s[scores={osp.give=8}] run give @s minecraft:coal 64
execute if entity @s[scores={osp.give=9}] run give @s minecraft:copper_ingot 64
execute if entity @s[scores={osp.give=10}] run give @s minecraft:amethyst_shard 64
execute if entity @s[scores={osp.give=11}] run give @s minecraft:quartz 64
execute if entity @s[scores={osp.give=12}] run give @s minecraft:glowstone_dust 64
execute if entity @s[scores={osp.give=13}] run give @s minecraft:obsidian 64
execute if entity @s[scores={osp.give=14}] run give @s minecraft:blaze_rod 64
execute if entity @s[scores={osp.give=15}] run give @s minecraft:ender_pearl 16
execute if entity @s[scores={osp.give=16}] run give @s minecraft:phantom_membrane 64
execute if entity @s[scores={osp.give=17}] run give @s minecraft:leather 64
execute if entity @s[scores={osp.give=18}] run give @s minecraft:string 64
execute if entity @s[scores={osp.give=19}] run give @s minecraft:slime_ball 64
execute if entity @s[scores={osp.give=20}] run give @s minecraft:bone 64

# === FOOD (21-35) ===
execute if entity @s[scores={osp.give=21}] run give @s minecraft:golden_apple 64
execute if entity @s[scores={osp.give=22}] run give @s minecraft:enchanted_golden_apple 64
execute if entity @s[scores={osp.give=23}] run give @s minecraft:cooked_beef 64
execute if entity @s[scores={osp.give=24}] run give @s minecraft:cooked_porkchop 64
execute if entity @s[scores={osp.give=25}] run give @s minecraft:bread 64
execute if entity @s[scores={osp.give=26}] run give @s minecraft:golden_carrot 64
execute if entity @s[scores={osp.give=27}] run give @s minecraft:cooked_salmon 64
execute if entity @s[scores={osp.give=28}] run give @s minecraft:cake 1
execute if entity @s[scores={osp.give=29}] run give @s minecraft:cookie 64
execute if entity @s[scores={osp.give=30}] run give @s minecraft:melon_slice 64
execute if entity @s[scores={osp.give=31}] run give @s minecraft:sweet_berries 64
execute if entity @s[scores={osp.give=32}] run give @s minecraft:chorus_fruit 64
execute if entity @s[scores={osp.give=33}] run give @s minecraft:dried_kelp 64
execute if entity @s[scores={osp.give=34}] run give @s minecraft:mushroom_stew 1
execute if entity @s[scores={osp.give=35}] run give @s minecraft:suspicious_stew 1

# === DIAMOND GEAR (36-45) ===
execute if entity @s[scores={osp.give=36}] run give @s minecraft:diamond_sword 1
execute if entity @s[scores={osp.give=37}] run give @s minecraft:diamond_pickaxe 1
execute if entity @s[scores={osp.give=38}] run give @s minecraft:diamond_axe 1
execute if entity @s[scores={osp.give=39}] run give @s minecraft:diamond_shovel 1
execute if entity @s[scores={osp.give=40}] run give @s minecraft:diamond_hoe 1
execute if entity @s[scores={osp.give=41}] run give @s minecraft:diamond_helmet 1
execute if entity @s[scores={osp.give=42}] run give @s minecraft:diamond_chestplate 1
execute if entity @s[scores={osp.give=43}] run give @s minecraft:diamond_leggings 1
execute if entity @s[scores={osp.give=44}] run give @s minecraft:diamond_boots 1
execute if entity @s[scores={osp.give=45}] run give @s minecraft:diamond_horse_armor 1

# === NETHERITE GEAR (46-55) ===
execute if entity @s[scores={osp.give=46}] run give @s minecraft:netherite_sword 1
execute if entity @s[scores={osp.give=47}] run give @s minecraft:netherite_pickaxe 1
execute if entity @s[scores={osp.give=48}] run give @s minecraft:netherite_axe 1
execute if entity @s[scores={osp.give=49}] run give @s minecraft:netherite_shovel 1
execute if entity @s[scores={osp.give=50}] run give @s minecraft:netherite_hoe 1
execute if entity @s[scores={osp.give=51}] run give @s minecraft:netherite_helmet 1
execute if entity @s[scores={osp.give=52}] run give @s minecraft:netherite_chestplate 1
execute if entity @s[scores={osp.give=53}] run give @s minecraft:netherite_leggings 1
execute if entity @s[scores={osp.give=54}] run give @s minecraft:netherite_boots 1
execute if entity @s[scores={osp.give=55}] run give @s minecraft:netherite_upgrade_smithing_template 1

# === IRON GEAR (56-64) ===
execute if entity @s[scores={osp.give=56}] run give @s minecraft:iron_sword 1
execute if entity @s[scores={osp.give=57}] run give @s minecraft:iron_pickaxe 1
execute if entity @s[scores={osp.give=58}] run give @s minecraft:iron_axe 1
execute if entity @s[scores={osp.give=59}] run give @s minecraft:iron_shovel 1
execute if entity @s[scores={osp.give=60}] run give @s minecraft:iron_helmet 1
execute if entity @s[scores={osp.give=61}] run give @s minecraft:iron_chestplate 1
execute if entity @s[scores={osp.give=62}] run give @s minecraft:iron_leggings 1
execute if entity @s[scores={osp.give=63}] run give @s minecraft:iron_boots 1
execute if entity @s[scores={osp.give=64}] run give @s minecraft:iron_horse_armor 1

# === WEAPONS & COMBAT (65-80) ===
execute if entity @s[scores={osp.give=65}] run give @s minecraft:bow 1
execute if entity @s[scores={osp.give=66}] run give @s minecraft:crossbow 1
execute if entity @s[scores={osp.give=67}] run give @s minecraft:arrow 64
execute if entity @s[scores={osp.give=68}] run give @s minecraft:spectral_arrow 64
execute if entity @s[scores={osp.give=69}] run give @s minecraft:shield 1
execute if entity @s[scores={osp.give=70}] run give @s minecraft:totem_of_undying 1
execute if entity @s[scores={osp.give=71}] run give @s minecraft:trident 1
execute if entity @s[scores={osp.give=72}] run give @s minecraft:mace 1
execute if entity @s[scores={osp.give=73}] run give @s minecraft:tnt 64
execute if entity @s[scores={osp.give=74}] run give @s minecraft:end_crystal 4
execute if entity @s[scores={osp.give=75}] run give @s minecraft:fire_charge 64
execute if entity @s[scores={osp.give=76}] run give @s minecraft:flint_and_steel 1
execute if entity @s[scores={osp.give=77}] run give @s minecraft:lava_bucket 1
execute if entity @s[scores={osp.give=78}] run give @s minecraft:water_bucket 1
execute if entity @s[scores={osp.give=79}] run give @s minecraft:snowball 16
execute if entity @s[scores={osp.give=80}] run give @s minecraft:egg 16

# === UTILITY & TRANSPORT (81-100) ===
execute if entity @s[scores={osp.give=81}] run give @s minecraft:elytra 1
execute if entity @s[scores={osp.give=82}] run give @s minecraft:firework_rocket 64
execute if entity @s[scores={osp.give=83}] run give @s minecraft:ender_chest 1
execute if entity @s[scores={osp.give=84}] run give @s minecraft:shulker_box 1
execute if entity @s[scores={osp.give=85}] run give @s minecraft:crafting_table 1
execute if entity @s[scores={osp.give=86}] run give @s minecraft:anvil 1
execute if entity @s[scores={osp.give=87}] run give @s minecraft:enchanting_table 1
execute if entity @s[scores={osp.give=88}] run give @s minecraft:brewing_stand 1
execute if entity @s[scores={osp.give=89}] run give @s minecraft:furnace 1
execute if entity @s[scores={osp.give=90}] run give @s minecraft:blast_furnace 1
execute if entity @s[scores={osp.give=91}] run give @s minecraft:smoker 1
execute if entity @s[scores={osp.give=92}] run give @s minecraft:chest 64
execute if entity @s[scores={osp.give=93}] run give @s minecraft:hopper 64
execute if entity @s[scores={osp.give=94}] run give @s minecraft:dispenser 64
execute if entity @s[scores={osp.give=95}] run give @s minecraft:dropper 64
execute if entity @s[scores={osp.give=96}] run give @s minecraft:piston 64
execute if entity @s[scores={osp.give=97}] run give @s minecraft:sticky_piston 64
execute if entity @s[scores={osp.give=98}] run give @s minecraft:minecart 1
execute if entity @s[scores={osp.give=99}] run give @s minecraft:saddle 1
execute if entity @s[scores={osp.give=100}] run give @s minecraft:name_tag 64

# === POTIONS & EFFECTS (101-115) ===
execute if entity @s[scores={osp.give=101}] run give @s minecraft:experience_bottle 64
execute if entity @s[scores={osp.give=102}] run give @s minecraft:glass_bottle 64
execute if entity @s[scores={osp.give=103}] run give @s minecraft:dragon_breath 64
execute if entity @s[scores={osp.give=104}] run give @s minecraft:ghast_tear 64
execute if entity @s[scores={osp.give=105}] run give @s minecraft:fermented_spider_eye 64
execute if entity @s[scores={osp.give=106}] run give @s minecraft:magma_cream 64
execute if entity @s[scores={osp.give=107}] run give @s minecraft:nether_wart 64
execute if entity @s[scores={osp.give=108}] run give @s minecraft:glistering_melon_slice 64
execute if entity @s[scores={osp.give=109}] run give @s minecraft:rabbit_foot 64
execute if entity @s[scores={osp.give=110}] run give @s minecraft:spider_eye 64
execute if entity @s[scores={osp.give=111}] run give @s minecraft:sugar 64
execute if entity @s[scores={osp.give=112}] run give @s minecraft:gunpowder 64
execute if entity @s[scores={osp.give=113}] run give @s minecraft:golden_apple 64
execute if entity @s[scores={osp.give=114}] run give @s minecraft:turtle_scute 64
execute if entity @s[scores={osp.give=115}] run give @s minecraft:breeze_rod 64

# === BLOCKS (116-145) ===
execute if entity @s[scores={osp.give=116}] run give @s minecraft:stone 64
execute if entity @s[scores={osp.give=117}] run give @s minecraft:cobblestone 64
execute if entity @s[scores={osp.give=118}] run give @s minecraft:deepslate 64
execute if entity @s[scores={osp.give=119}] run give @s minecraft:dirt 64
execute if entity @s[scores={osp.give=120}] run give @s minecraft:grass_block 64
execute if entity @s[scores={osp.give=121}] run give @s minecraft:sand 64
execute if entity @s[scores={osp.give=122}] run give @s minecraft:gravel 64
execute if entity @s[scores={osp.give=123}] run give @s minecraft:oak_log 64
execute if entity @s[scores={osp.give=124}] run give @s minecraft:oak_planks 64
execute if entity @s[scores={osp.give=125}] run give @s minecraft:glass 64
execute if entity @s[scores={osp.give=126}] run give @s minecraft:bricks 64
execute if entity @s[scores={osp.give=127}] run give @s minecraft:stone_bricks 64
execute if entity @s[scores={osp.give=128}] run give @s minecraft:nether_bricks 64
execute if entity @s[scores={osp.give=129}] run give @s minecraft:end_stone 64
execute if entity @s[scores={osp.give=130}] run give @s minecraft:purpur_block 64
execute if entity @s[scores={osp.give=131}] run give @s minecraft:prismarine 64
execute if entity @s[scores={osp.give=132}] run give @s minecraft:sea_lantern 64
execute if entity @s[scores={osp.give=133}] run give @s minecraft:glowstone 64
execute if entity @s[scores={osp.give=134}] run give @s minecraft:torch 64
execute if entity @s[scores={osp.give=135}] run give @s minecraft:lantern 64
execute if entity @s[scores={osp.give=136}] run give @s minecraft:beacon 1
execute if entity @s[scores={osp.give=137}] run give @s minecraft:conduit 1
execute if entity @s[scores={osp.give=138}] run give @s minecraft:sponge 64
execute if entity @s[scores={osp.give=139}] run give @s minecraft:tinted_glass 64
execute if entity @s[scores={osp.give=140}] run give @s minecraft:rail 64
execute if entity @s[scores={osp.give=141}] run give @s minecraft:powered_rail 64
execute if entity @s[scores={osp.give=142}] run give @s minecraft:redstone_block 64
execute if entity @s[scores={osp.give=143}] run give @s minecraft:diamond_block 64
execute if entity @s[scores={osp.give=144}] run give @s minecraft:iron_block 64
execute if entity @s[scores={osp.give=145}] run give @s minecraft:gold_block 64

# === REDSTONE (146-160) ===
execute if entity @s[scores={osp.give=146}] run give @s minecraft:redstone 64
execute if entity @s[scores={osp.give=147}] run give @s minecraft:redstone_torch 64
execute if entity @s[scores={osp.give=148}] run give @s minecraft:repeater 64
execute if entity @s[scores={osp.give=149}] run give @s minecraft:comparator 64
execute if entity @s[scores={osp.give=150}] run give @s minecraft:observer 64
execute if entity @s[scores={osp.give=151}] run give @s minecraft:daylight_detector 64
execute if entity @s[scores={osp.give=152}] run give @s minecraft:lever 64
execute if entity @s[scores={osp.give=153}] run give @s minecraft:stone_button 64
execute if entity @s[scores={osp.give=154}] run give @s minecraft:stone_pressure_plate 64
execute if entity @s[scores={osp.give=155}] run give @s minecraft:tripwire_hook 64
execute if entity @s[scores={osp.give=156}] run give @s minecraft:trapped_chest 64
execute if entity @s[scores={osp.give=157}] run give @s minecraft:note_block 64
execute if entity @s[scores={osp.give=158}] run give @s minecraft:target 64
execute if entity @s[scores={osp.give=159}] run give @s minecraft:sculk_sensor 64
execute if entity @s[scores={osp.give=160}] run give @s minecraft:calibrated_sculk_sensor 64

# === SPECIAL & RARE (161-180) ===
execute if entity @s[scores={osp.give=161}] run give @s minecraft:nether_star 1
execute if entity @s[scores={osp.give=162}] run give @s minecraft:dragon_egg 1
execute if entity @s[scores={osp.give=163}] run give @s minecraft:elytra 1
execute if entity @s[scores={osp.give=164}] run give @s minecraft:heart_of_the_sea 1
execute if entity @s[scores={osp.give=165}] run give @s minecraft:nautilus_shell 64
execute if entity @s[scores={osp.give=166}] run give @s minecraft:wither_skeleton_skull 3
execute if entity @s[scores={osp.give=167}] run give @s minecraft:soul_sand 4
execute if entity @s[scores={osp.give=168}] run give @s minecraft:respawn_anchor 1
execute if entity @s[scores={osp.give=169}] run give @s minecraft:lodestone 1
execute if entity @s[scores={osp.give=170}] run give @s minecraft:recovery_compass 1
execute if entity @s[scores={osp.give=171}] run give @s minecraft:spyglass 1
execute if entity @s[scores={osp.give=172}] run give @s minecraft:brush 1
execute if entity @s[scores={osp.give=173}] run give @s minecraft:lead 64
execute if entity @s[scores={osp.give=174}] run give @s minecraft:fishing_rod 1
execute if entity @s[scores={osp.give=175}] run give @s minecraft:shears 1
execute if entity @s[scores={osp.give=176}] run give @s minecraft:compass 1
execute if entity @s[scores={osp.give=177}] run give @s minecraft:clock 1
execute if entity @s[scores={osp.give=178}] run give @s minecraft:map 1
execute if entity @s[scores={osp.give=179}] run give @s minecraft:book 64
execute if entity @s[scores={osp.give=180}] run give @s minecraft:writable_book 1

# === SPAWN EGGS & MISC (181-200) ===
execute if entity @s[scores={osp.give=181}] run give @s minecraft:command_block 1
execute if entity @s[scores={osp.give=182}] run give @s minecraft:chain_command_block 1
execute if entity @s[scores={osp.give=183}] run give @s minecraft:repeating_command_block 1
execute if entity @s[scores={osp.give=184}] run give @s minecraft:structure_block 1
execute if entity @s[scores={osp.give=185}] run give @s minecraft:barrier 64
execute if entity @s[scores={osp.give=186}] run give @s minecraft:light 64
execute if entity @s[scores={osp.give=187}] run give @s minecraft:spawner 1
execute if entity @s[scores={osp.give=188}] run give @s minecraft:trial_spawner 1
execute if entity @s[scores={osp.give=189}] run give @s minecraft:vault 1
execute if entity @s[scores={osp.give=190}] run give @s minecraft:debug_stick 1
execute if entity @s[scores={osp.give=191}] run give @s minecraft:knowledge_book 1
execute if entity @s[scores={osp.give=192}] run give @s minecraft:bundle 1
execute if entity @s[scores={osp.give=193}] run give @s minecraft:copper_bulb 64
execute if entity @s[scores={osp.give=194}] run give @s minecraft:trial_key 64
execute if entity @s[scores={osp.give=195}] run give @s minecraft:ominous_trial_key 64
execute if entity @s[scores={osp.give=196}] run give @s minecraft:wind_charge 64
execute if entity @s[scores={osp.give=197}] run give @s minecraft:wolf_armor 1
execute if entity @s[scores={osp.give=198}] run give @s minecraft:decorated_pot 1
execute if entity @s[scores={osp.give=199}] run give @s minecraft:heavy_core 1
execute if entity @s[scores={osp.give=200}] run give @s minecraft:bed 1

# Reset trigger
scoreboard players set @s osp.give 0
