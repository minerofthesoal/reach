# Reach & Fly Mod

A client-side Fabric mod for Minecraft with 15+ hack features including extended reach, fly, ESP, auto-combat, Jesus (walk on water), and more.

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
| **Auto Elytra Swap** | Auto-equip elytra when falling | `Y` |
| **Fly to Coords** | Auto-fly to target coordinates | `P` |
| **Walk to Coords** | Simple pathfinding to coordinates | `;` |
| **HUD Toggle** | Show/hide status overlay | `H` |
| **Config Screen** | Full GUI with sliders and toggles | `Right Shift` |

All keybinds are configurable in Minecraft's Controls menu under the "Reach & Fly" category.

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for your Minecraft version
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest JAR from [Releases](../../releases) or [Actions](../../actions)
4. Place the JAR in your `.minecraft/mods/` folder
5. Launch Minecraft

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
- [Building from Source](docs/wiki/Building.md) - Build instructions for all versions

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
├── AutoElytraSwapHandler.java    # Auto elytra equip on fall
├── FlyToCoordsHandler.java       # Auto-fly to coordinates
├── WalkToCoordsHandler.java      # Simple walk pathfinding
└── mixin/
    ├── ClientPlayerInteractionManagerMixin.java
    └── EntityGlowMixin.java      # ESP entity glow effect
```

## License

Apache-2.0
