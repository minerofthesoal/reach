# f1sch client

A client-side Fabric mod for Minecraft with 25+ hack features including extended reach, fly, ESP, auto-combat, teleport, X-Ray, knockback, Jesus (walk on water), step, safewalk, auto-log, and more. Inspired by Meteor, Future, and Rusherhack clients.

**Supported Versions:** 1.21.11 | 1.21.4 | 1.21.1

[![Build](https://github.com/minerofthesoal/reach/actions/workflows/build.yml/badge.svg)](https://github.com/minerofthesoal/reach/actions/workflows/build.yml)

## Features

| Feature | Description | Default Key |
|---------|-------------|-------------|
| **Reach** | Extend block/entity interaction range (3-50 blocks) | `R` |
| **Fly** | Survival flight with adjustable speed | `G` |
| **ESP** | Entity tracers + path trace to mobs/players | `X` |
| **Auto Hit** | Automatically attacks nearest entity | `V` |
| **Low HP Kill** | Targets entities below a health threshold | `B` |
| **Auto Kill When Low** | Attacks nearby when YOUR hp is low | `K` |
| **Eating Assist** | Auto-eats best food from hotbar when hungry | `N` |
| **Jesus** | Walk on water and lava | `U` |
| **NoFall** | Prevents fall damage | `I` |
| **Fullbright** | Night vision (max gamma) | `L` |
| **Speed** | Ground speed multiplier | `O` |
| **X-Ray** | See ores/chests/spawners through blocks | `Z` |
| **Knockback** | Massive knockback on hit (up to 2500) | `J` |
| **Teleport** | Instant teleport to any coordinates | `T` |
| **Auto Elytra Swap** | Auto-equip elytra when falling | `Y` |
| **Fly to Coords** | Auto-fly to target coordinates | `P` |
| **Walk to Coords** | Simple pathfinding to coordinates | `;` |
| **Auto Totem** | Auto-move totems to offhand | `M` |
| **Auto Armor** | Auto-equip best armor | `,` |
| **Scaffold** | Auto-place blocks below you while walking | `.` |
| **Better Sprint** | Always sprint when moving forward | GUI |
| **SafeWalk** | Prevent walking off block edges | GUI |
| **Step** | Step up blocks without jumping (1-10 height) | GUI |
| **Auto Log** | Auto-disconnect at low HP threshold | GUI |
| **Auto Respawn** | Auto-respawn on death | GUI |
| **HUD Toggle** | Show/hide status overlay | `H` |
| **Config Screen** | ClickGUI with categories and toggles | `Right Shift` |
| **Item Give** | Give yourself any item (1706 items, no OP needed) | Config Screen |

All keybinds are configurable in Minecraft's Controls menu under the "f1sch client" category.

## Installation

### Client Mod

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for your Minecraft version
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest JAR from [Releases](../../releases) or [Actions](../../actions)
4. Place the JAR in your `.minecraft/mods/` folder
5. Launch Minecraft

### Server Addon v2 (Optional)

The server addon v2 enables **full server-authoritative support** for multiplayer servers. Players without the client mod are completely unaffected.

**Supported server-side features:**
| Feature | What it does server-side |
|---------|--------------------------|
| **Teleport** | Reliable instant teleport (no rubberbanding) |
| **Knockback** | Server applies ATTACK_KNOCKBACK attribute modifier |
| **Reach** | Server applies BLOCK/ENTITY_INTERACTION_RANGE attributes |
| **Speed** | Server applies MOVEMENT_SPEED attribute modifier |
| **NoFall** | Server resets fall distance every tick |
| **Fly** | Server grants flight permission (survives respawn) |
| **ESP Extended** | Server sends entity positions beyond normal tracking range |

**Two installation options:**

#### Option A: Fabric Mod (full features, requires Fabric on server)

1. Build: `cd server-addon && ../gradlew build`
2. Place `f1sch-server-addon-2.0.0.jar` in the server's `mods/` folder
3. Restart the server
4. Features auto-sync from the client mod via custom packets

#### Option B: Data Pack (no Fabric needed, works on any server)

1. Build: `cd server-addon && ../gradlew buildDatapack`
2. Place `f1sch-server-addon-datapack-2.0.0.zip` in the server's `world/datapacks/` folder
3. Run `/reload` or restart the server
4. Use `/trigger f1sch.help` in-game for all commands

**Data Pack commands:**
```
/trigger f1sch.help              # Show all commands (clickable!)
/trigger f1sch.knockback set 1   # Toggle knockback on/off
/trigger f1sch.kb_str set 50     # Set knockback strength (1-2500)
/trigger f1sch.reach set 1       # Toggle reach on/off
/trigger f1sch.reach_dist set 100 # Set reach (x10, e.g. 100 = 10 blocks)
/trigger f1sch.speed set 1       # Toggle speed on/off
/trigger f1sch.speed_mult set 20  # Set speed (x10, e.g. 20 = 2.0x)
/trigger f1sch.nofall set 1      # Toggle nofall on/off
/trigger f1sch.fly set 1         # Toggle fly on/off
/trigger f1sch.tp_x set 100      # Set teleport X
/trigger f1sch.tp_y set 64       # Set teleport Y
/trigger f1sch.tp_z set 200      # Set teleport Z
/trigger f1sch.tp set 1          # Teleport now!
```

To uninstall the data pack cleanly: `/function f1sch:uninstall`

Without the server addon, features still work in **client-only mode** (Teleport uses Beta incremental mode, attributes only apply client-side, ESP limited to loaded chunks).

### Aternos Server Setup

To install the server addon on an **Aternos** Minecraft server:

**As Fabric Mod (requires Fabric server):**

1. Go to your Aternos server panel
2. Click **Software & Plugins** (or **Mods**) in the left sidebar
3. Make sure your server is set to **Fabric** as the server software
4. Click **Upload** and select the `f1sch-server-addon-2.0.0.jar` file
5. Start/restart your server

**As Data Pack (works on ANY server - Vanilla, Fabric, Paper, etc.):**

1. In the Aternos panel, go to **Files** > navigate to your `world/datapacks/` folder
2. Click **Upload** and upload the `f1sch-server-addon-datapack-2.0.0.zip`
3. Restart the server (or run `/reload` in console)
4. Players use `/trigger f1sch.help` in-game for commands

**Via Aternos Console (after addon is installed):**

The addon registers automatically. Verify it's loaded by checking the console for:
```
[f1sch Server Addon v2] Ready. Supported features: Teleport, Knockback, Reach, Speed, NoFall, Fly, ESP
```

All features auto-sync when toggled in-game. The server addon cleans up all attribute modifiers when a player disconnects.

### Version Branches

| Branch | Minecraft | Fabric API | Loom |
|--------|-----------|------------|------|
| `claude/minecraft-reach-fly-mod-0zKqV` (main) | 1.21.11 | 0.141.3+1.21.11 | 1.13.6 |
| `mc-1.21.4` | 1.21.4 | 0.110.5+1.21.4 | 1.9-SNAPSHOT |
| `mc-1.21.1` | 1.21.1 | 0.102.0+1.21.1 | 1.7-SNAPSHOT |

## Wiki

Detailed documentation for every feature is in the [docs/wiki](docs/wiki/) folder:

- [Features Overview](docs/wiki/Features.md) - All features with config options and details
- [Keybinds](docs/wiki/Keybinds.md) - Complete keybind reference
- [Configuration](docs/wiki/Configuration.md) - Config file and GUI settings
- [Item IDs](docs/wiki/ItemIDs.md) - All 1706 datapack item trigger codes
- [Building from Source](docs/wiki/Building.md) - Build instructions for all versions
- [Server Addon](docs/wiki/ServerAddon.md) - Server-side teleport addon setup

## Building from Source

Requires **JDK 21**.

```bash
git clone https://github.com/minerofthesoal/reach.git
cd reach
./gradlew build
```

For other Minecraft versions:
```bash
git checkout mc-1.21.4   # or mc-1.21.1
./gradlew build
```

The built JAR will be at `build/libs/reach-fly-mod-<version>.jar`.

To build the server addon:
```bash
cd server-addon
../gradlew build
```

## Project Structure

```
src/main/java/com/reachfly/
├── ReachFlyClient.java           # Client mod initializer
├── ModConfig.java                # JSON config (load/save)
├── ConfigScreen.java             # In-game GUI (manual scroll, sliders, toggles)
├── KeybindHandler.java           # Keybind registration
├── EventHandler.java             # Tick + render event dispatch
├── HudOverlay.java               # On-screen status display
├── ModMenuIntegration.java       # Mod Menu config button
├── ReachHandler.java             # Reach attribute modifier (client + server)
├── FlyHandler.java               # Survival flight
├── EspRenderer.java              # ESP tracer lines + path trace
├── AutoHitHandler.java           # Auto-attack nearest entity
├── LowHealthKillHandler.java     # Target low-HP entities
├── AutoKillWhenLowHandler.java   # Attack when player HP is low
├── EatingAssistHandler.java      # Auto-eat best food from hotbar
├── JesusHandler.java             # Walk on water/lava
├── NoFallHandler.java            # Fall damage prevention
├── FullbrightHandler.java        # Max gamma
├── SpeedHandler.java             # Ground speed multiplier
├── KnockbackHandler.java         # Knockback attribute modifier
├── XrayHandler.java              # X-Ray block filter
├── TeleportHandler.java          # Teleport (normal + beta modes)
├── TeleportPayload.java          # Custom network packet for teleport
├── FeatureSyncPayload.java       # Feature sync packet (C2S)
├── EspDataPayload.java           # ESP entity data packet (S2C)
├── ServerSyncHandler.java        # Client-server feature sync handler
├── AutoElytraSwapHandler.java    # Auto elytra equip on fall
├── FlyToCoordsHandler.java       # Auto-fly to coordinates
├── WalkToCoordsHandler.java      # Simple walk pathfinding
├── MeteorHandlers.java           # AutoLog, AutoRespawn, BetterSprint, SafeWalk, Step
├── ProHandlers.java              # Pro-tier feature handlers
└── mixin/
    ├── BlockRenderMixin.java     # X-Ray block rendering
    ├── ClientPlayerInteractionManagerMixin.java  # Knockback velocity
    └── EntityGlowMixin.java      # ESP entity glow effect

server-addon/                     # Server addon v2 (full feature support)
├── build.gradle
├── src/main/java/com/reachfly/serveraddon/
│   ├── OspServerAddon.java       # Server initializer + all feature handlers
│   ├── TeleportPayload.java      # Teleport packet (C2S)
│   ├── FeatureSyncPayload.java   # Feature sync packet (C2S)
│   └── EspDataPayload.java       # ESP entity data packet (S2C)
└── src/main/resources/
    └── fabric.mod.json
```

## License

Apache-2.0
