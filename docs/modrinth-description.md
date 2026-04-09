# f1sch client

A client-side Fabric mod with 25+ utility/hack features. Built for fun, not for cheating on public servers. Inspired by Meteor, Future, and Rusherhack.

## What it does

f1sch gives you a configurable ClickGUI (Right Shift) with features organized into categories: Combat, Movement, Visual, Exploits, and Pro. Everything has a keybind, a config slider, and an in-game HUD showing what's active.

### Combat
- **Reach** - Extend interaction range from 3 to 50 blocks
- **Auto Hit** - Auto-attacks nearest entity in range
- **Kill Aura / Kill Aura+** - Hit all entities in range (Pro version bypasses cooldown)
- **Knockback** - Crazy knockback on hit, up to 2500 strength
- **Low HP Kill / Auto Kill When Low** - Auto-target low health mobs or panic-attack when you're low

### Movement
- **Fly** - Creative-style flight in survival with adjustable speed
- **Speed** - Ground speed multiplier (1-10x)
- **Jesus** - Walk on water and lava
- **NoFall** - No fall damage
- **Scaffold** - Auto-place blocks under you while walking
- **Step** - Walk up blocks without jumping (1-10 height)
- **SafeWalk** - Can't walk off edges
- **Better Sprint** - Always sprinting
- **Fly to Coords** - Autopilot flight to any coordinates with collision avoidance
- **Walk to Coords** - Baritone-lite pathfinding on foot
- **Auto Elytra Swap** - Auto-equip elytra when falling

### Visual
- **ESP** - Glowing outlines on entities (color-coded: red=players, orange=hostile, green=passive) + tracer lines + path traces
- **X-Ray** - See ores, chests, spawners, and structures through blocks
- **Fullbright** - Max gamma, see in the dark

### Utility
- **Item Give** - Browse and give yourself any of 1706 items from a searchable GUI. No OP required (uses datapack triggers)
- **Teleport** - Instant TP to coordinates
- **Eating Assist** - Auto-eats best food when hungry
- **Auto Totem** - Keeps totems in your offhand
- **Auto Armor** - Auto-equip best armor
- **Auto Log** - Disconnect at a health threshold
- **Auto Respawn** - Instant respawn on death
- **Op Self** - Silent OP (requires server addon)

## Server Addon

The mod works standalone in singleplayer. For multiplayer, there's an optional server addon available in two forms:

- **Fabric mod** (full features) - place in the server's `mods/` folder
- **Datapack** (vanilla compatible) - place in `world/datapacks/`, works on vanilla, Paper, Fabric, Aternos, etc.

The datapack lets any player use `/trigger` commands for reach, knockback, speed, fly, nofall, teleport, and item give - no OP needed. Type `/trigger f1sch.help set 1` in chat for a clickable command list.

**Important:** The server addon has no permission system. Any player with the client mod (or who knows the trigger commands) can use all features. Only install it on servers where you trust all players.

## Honestly Known Bugs & Rough Edges

This mod is a passion project, not a polished product. Here's what you should know:

- **Reach on multiplayer** only extends client-side targeting. The server still validates at its own range, so you can't actually place blocks at 50 blocks on a vanilla server. Works fully in singleplayer.
- **Fly on multiplayer** may get you kicked for "Flying is not enabled on this server" without the server addon. In singleplayer, the mod patches the integrated server directly so this doesn't happen.
- **Teleport has two modes.** Normal mode uses the server addon (reliable). Beta mode spoofs your position client-side and **will rubberband** on most servers.
- **NoFall** sends spoofed on-ground packets. Works on most servers but anti-cheat plugins may catch it.
- **ESP** only sees entities in loaded chunks. The server addon can extend this range.
- **Walk to Coords** pathfinding is basic. It gets stuck sometimes, tries a 70-degree detour after 40 ticks, and a full reversal after 100 ticks. It's not Baritone.
- **Fly to Coords** collision avoidance only checks 4 blocks ahead. If it gets stuck for 60 ticks, it starts breaking blocks (even ones you might not want broken).
- **Some Pro features are incomplete.** NoSwing is a placeholder that only hides the animation client-side. A few others are thin wrappers.
- **Item Give trigger codes** are based on alphabetically sorted item IDs from MC 1.21.11. If Mojang adds items in a future version, codes may shift.
- **Anti-cheat servers** will likely flag speed, fly, nofall, teleport beta, and fast break. This mod is not designed to bypass anti-cheat.
- **The datapack sets `sendCommandFeedback` and `logAdminCommands` to false** globally to suppress output. If you need those gamerules on, you'll have to fight the tick function that re-enforces them every tick.
- **No permission system on the server addon.** Seriously. Anyone can OP themselves, teleport anywhere, or give themselves items if the addon is installed.
- **Config saves on every change** to `config/reachfly.json`. Keybinds persist across mod versions.

## Supported Versions

| Minecraft | Branch | Status |
|-----------|--------|--------|
| 1.21.11 | main | Active |
| 1.21.10 | mc-1.21.10 | Maintained |
| 1.21.9 | mc-1.21.9 | Maintained |
| 1.21.8 | mc-1.21.8 | Maintained |
| 1.21.4 | mc-1.21.4 | Maintained |
| 1.21.1 | mc-1.21.1 | Maintained |

Requires: Fabric Loader 0.18.0+, Fabric API, Java 21

## License

Apache-2.0
