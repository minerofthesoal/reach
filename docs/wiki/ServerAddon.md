# Server Addon

The f1sch Server Addon is an optional server-side component that extends the client mod with server-authoritative features. It includes a Fabric mod (full features) and a datapack (item give, trigger-based features).

## How It Works

1. A player with the client mod presses **T** (with Normal mode enabled in config)
2. The client sends a custom `reachfly:teleport` packet containing the target X, Y, Z coordinates
3. The server addon receives the packet and teleports the player server-side
4. The player sees a confirmation message in their action bar

**Players without the client mod are completely unaffected.** The server simply never receives the custom packet from them, so nothing happens. The addon has zero impact on vanilla players.

## Installation

### Server Requirements

- Fabric Loader 0.18.0+ for Minecraft 1.21.11
- Fabric API

### Steps

1. Build the addon:
   ```bash
   cd server-addon
   ../gradlew build
   ```
2. Copy `server-addon/build/libs/f1sch-server-addon-1.0.0.jar` to the server's `mods/` folder
3. Restart the server

### Client Setup

In the client mod's config screen (Right Shift):

1. Scroll to the **Teleport** section
2. Set **Normal Mode** to **ON** (this is the default)
3. Set your target X, Y, Z coordinates
4. Press **T** to teleport

## Normal Mode vs Beta Mode

| | Normal Mode | Beta Mode |
|---|---|---|
| **Server addon needed?** | Yes (multiplayer) / No (singleplayer) | No |
| **How it works** | Server teleports the player | Client spoofs position packets |
| **Reliability** | Always works | May rubberband on vanilla servers |
| **Anti-cheat safe?** | Depends on server permissions | Likely flagged |
| **Singleplayer** | Direct server teleport | Client-side position set |

## Security Notes

- The addon teleports ANY player who sends the packet to ANY coordinates
- There is no permission system - any player with the client mod can teleport
- Y coordinate is clamped to -64 to 320 for safety
- All teleports are logged to the server console

If you want to restrict who can teleport, you would need to add a permission check (e.g., check if the player is an operator or has a specific permission node).

## Item Give System (Datapack)

The datapack includes an item give system that lets players give themselves any of 1706 items using trigger commands. No OP required.

```
/trigger f1sch.give set <code>
```

The client mod's Item Give screen handles this automatically -- just search and click. For the full list of codes, see [Item IDs](ItemIDs.md).

---

## Logs

The addon logs all teleport events:
```
[f1sch Server Addon] Teleporting Steve to 100, 64, -200
```

## Troubleshooting

**"Server addon not installed" message:**
- The server doesn't have the addon mod. Either install it or switch to Beta mode in the client config.

**Teleport doesn't work on multiplayer:**
- Verify the addon JAR is in the server's `mods/` folder
- Check that the server has Fabric Loader and Fabric API
- Check server logs for errors

**Works in singleplayer but not multiplayer:**
- Normal mode handles singleplayer directly (no addon needed). For multiplayer, the server must have the addon installed.
