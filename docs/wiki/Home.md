# f1sch Client Wiki

Welcome to the f1sch client documentation. This mod is a client-side Fabric mod for Minecraft with 25+ hack features.

**Supported versions:** 1.21.11 | 1.21.4 | 1.21.1

---

## Pages

### Getting Started
- [Building from Source](Building.md) - How to build the mod for all MC versions
- [Configuration](Configuration.md) - Config file location, GUI settings, and options

### Features
- [Features Overview](Features.md) - All features with config options and descriptions
- [Keybinds](Keybinds.md) - Complete keybind reference (keybinds are saved to the mod config and transfer between MC versions)

### Server
- [Server Addon](ServerAddon.md) - Server-side addon setup (Fabric mod or datapack)
- [Item IDs](ItemIDs.md) - All 1706 datapack item trigger codes

---

## Quick Start

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) + [Fabric API](https://modrinth.com/mod/fabric-api)
2. Drop the mod JAR into `.minecraft/mods/`
3. Launch Minecraft and press **Right Shift** to open the config screen

### Default Keybinds

| Key | Feature |
|-----|---------|
| `Right Shift` | Config Screen |
| `R` | Reach |
| `G` | Fly |
| `X` | ESP |
| `H` | HUD Toggle |
| `T` | Teleport |
| `Z` | X-Ray |

See [Keybinds](Keybinds.md) for the full list.

---

## Item Give

The mod includes a built-in item give system that works on any server with the f1sch datapack installed. No operator permissions needed.

- Open the Config Screen (Right Shift) and click **Item Give**
- Search for any item by name
- Default quantity is a full stack (64)
- Supports all 1480 regular items + potions, arrows, and enchanted books (1706 total)
- Uses `/trigger` commands (no `/give` or OP required)

See [Item IDs](ItemIDs.md) for the complete trigger code reference.

---

## Version Branches

| Branch | Minecraft | Fabric API |
|--------|-----------|------------|
| main | 1.21.11 | 0.141.3+1.21.11 |
| `mc-1.21.4` | 1.21.4 | 0.110.5+1.21.4 |
| `mc-1.21.1` | 1.21.1 | 0.102.0+1.21.1 |

---

## Links

- [GitHub Repository](https://github.com/minerofthesoal/reach)
- [Releases](https://github.com/minerofthesoal/reach/releases)
- [CI Builds](https://github.com/minerofthesoal/reach/actions)
