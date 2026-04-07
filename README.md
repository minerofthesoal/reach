# f1sch client

A client-side Fabric mod for Minecraft 1.21.4 that adds configurable extended reach, survival flight, ESP, combat assists, and more.

## Features

- **Extended Reach** — Configurable block and entity interaction range (3–50 blocks)
- **Survival Fly** — Fly in survival mode with adjustable speed (0.1x–10x)
- **ESP** — See entities through walls
- **Auto Hit / Kill Aura** — Automatic combat
- **Jesus** — Walk on water
- **NoFall** — No fall damage
- **Speed** — Movement speed multiplier
- **Fullbright** — Full brightness
- **Fly to Coords / Walk to Coords** — Auto-navigation
- **Elytra Swap** — Auto equip/unequip elytra
- **Eating Assist** — Auto eat when hungry
- **Item Give** — Give yourself any item via datapack triggers
- **HUD Overlay** — Real-time status display
- **In-Game Config** — Full ClickGUI with sliders and toggles
- **Mod Menu Support** — Config button integration when Mod Menu is installed

## Default Keybinds

| Key | Action |
|-----|--------|
| `R` | Toggle Reach |
| `G` | Toggle Fly |
| `Right Shift` | Open Config Menu |

All keybinds are configurable in Minecraft's Controls menu under "f1sch client".

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 1.21.4
2. Install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download the latest JAR from [Releases](../../releases) or [Actions](../../actions)
4. Place the JAR in your `.minecraft/mods/` folder
5. Launch Minecraft

## Server Addon (Datapack)

Copy the `server-addon/datapack/` folder into your server's `datapacks/` directory. This provides:
- Trigger-based commands for non-OP players (reach, fly, speed, knockback, nofall, teleport, item give)
- Type `/trigger f1sch.help set 1` in-game for a full command list

## Building from Source

Requires **JDK 21**.

```bash
git clone https://github.com/minerofthesoal/reach.git
cd reach
./gradlew build
```

## License

Apache-2.0
