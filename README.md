# Reach & Fly Mod

A client-side Fabric mod for Minecraft 1.21.11 that adds configurable extended reach and survival flight.

## Features

- **Extended Reach** — Configurable block and entity interaction range (3–50 blocks)
- **Survival Fly** — Fly in survival mode with adjustable speed (0.1x–10x)
- **HUD Overlay** — Real-time status display showing reach/fly state and values
- **In-Game Config** — Full GUI with sliders and toggles (no external dependencies)
- **Mod Menu Support** — Config button integration when Mod Menu is installed
- **Keybinds** — All features togglable via configurable keybinds

## Default Keybinds

| Key | Action |
|-----|--------|
| `R` | Toggle Reach |
| `G` | Toggle Fly |
| `Right Shift` | Open Config Menu |

All keybinds are configurable in Minecraft's Controls menu under "Reach & Fly".

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 1.21.11
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest JAR from [Releases](../../releases) or [Actions](../../actions)
4. Place the JAR in your `.minecraft/mods/` folder
5. Launch Minecraft

## Building from Source

Requires **JDK 21**.

```bash
git clone https://github.com/minerofthesoal/reach.git
cd reach
./gradlew build
```

The built JAR will be at `build/libs/reach-fly-mod-1.0.0.jar`.

## Downloading the JAR

The GitHub Actions workflow automatically builds the mod on every push:
1. Go to the **Actions** tab
2. Click the latest successful build
3. Download the `reach-fly-mod` artifact

## Project Structure

```
src/main/java/com/reachfly/
├── ReachFlyClient.java          # Client mod initializer
├── ModConfig.java               # JSON config system
├── KeybindHandler.java          # Keybind registration
├── EventHandler.java            # Tick and render events
├── ReachHandler.java            # Reach attribute modifier logic
├── FlyHandler.java              # Survival fly logic
├── ConfigScreen.java            # In-game GUI (sliders + toggles)
├── HudOverlay.java              # HUD status display
├── ModMenuIntegration.java      # Mod Menu config button
└── mixin/
    ├── ClientPlayerInteractionManagerMixin.java  # Reach attribute updates
    └── GameRendererMixin.java                     # Crosshair raycast extension
```

## License

MIT
