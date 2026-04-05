# Configuration

## Config File

Settings are saved to:
```
<minecraft directory>/config/reachfly.json
```

The file is created automatically on first launch and updated whenever settings change.

## In-Game Config Screen

Press **Right Shift** (default) to open the config GUI. The screen has:

- **Toggle buttons** for enabling/disabling features (shows ON/OFF)
- **Sliders** for numeric values (shows current value in real-time)
- **Scrollable layout** with section headers
- **Auto-save** on every change

If [Mod Menu](https://modrinth.com/mod/modmenu) is installed, you can also access the config screen from the Mods list.

## All Config Options

### Combat

| Option | Type | Default | Range | Description |
|--------|------|---------|-------|-------------|
| `reachEnabled` | bool | false | - | Enable extended reach |
| `reachDistance` | float | 6.0 | 3.0 - 50.0 | Block/entity interaction range |
| `autoHitEnabled` | bool | false | - | Auto-attack nearest entity |
| `autoHitRange` | float | 3.0 | 1.0 - 50.0 | Auto hit search radius |
| `autoHitPlayersOnly` | bool | false | - | Only target players |
| `lowHealthKillEnabled` | bool | false | - | Target low-HP entities |
| `lowHealthThreshold` | float | 6.0 | 1.0 - 20.0 | HP threshold for targets |
| `autoKillWhenLowEnabled` | bool | false | - | Attack when your HP is low |
| `autoKillSelfHpThreshold` | float | 6.0 | 1.0 - 20.0 | Your HP threshold to activate |
| `autoKillWhenLowRange` | float | 4.0 | 1.0 - 50.0 | Attack range when low HP |

### Movement

| Option | Type | Default | Range | Description |
|--------|------|---------|-------|-------------|
| `flyEnabled` | bool | false | - | Enable survival flight |
| `flySpeed` | float | 1.0 | 0.1 - 10.0 | Flight speed multiplier |
| `speedEnabled` | bool | false | - | Enable speed boost |
| `speedMultiplier` | float | 2.0 | 1.0 - 10.0 | Ground speed multiplier |
| `jesusEnabled` | bool | false | - | Walk on water/lava |
| `noFallEnabled` | bool | false | - | Prevent fall damage |

### Navigation

| Option | Type | Default | Range | Description |
|--------|------|---------|-------|-------------|
| `flyToCoordsEnabled` | bool | false | - | Auto-fly to coords |
| `flyToX` | float | 0 | -30000 - 30000 | Target X coordinate |
| `flyToY` | float | 100 | -64 - 320 | Target Y coordinate |
| `flyToZ` | float | 0 | -30000 - 30000 | Target Z coordinate |
| `flyToCoordsSpeed` | float | 2.0 | 0.5 - 20.0 | Navigation flight speed |
| `walkToCoordsEnabled` | bool | false | - | Auto-walk to coords |
| `walkToX` | float | 0 | -30000 - 30000 | Walk target X |
| `walkToY` | float | 64 | -64 - 320 | Walk target Y |
| `walkToZ` | float | 0 | -30000 - 30000 | Walk target Z |

### Visual

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `espEnabled` | bool | false | Enable ESP entity highlighting |
| `espPlayers` | bool | true | Show players in ESP |
| `espHostile` | bool | true | Show hostile mobs in ESP |
| `espPassive` | bool | false | Show passive mobs in ESP |
| `espLines` | bool | false | Draw tracer lines to entities |
| `espPathTrace` | bool | false | Draw ground-level path trace |
| `fullbrightEnabled` | bool | false | Max gamma night vision |
| `hudVisible` | bool | true | Show HUD status overlay |

### Utility

| Option | Type | Default | Range | Description |
|--------|------|---------|-------|-------------|
| `eatingAssistEnabled` | bool | false | - | Auto-eat when hungry |
| `eatingHungerThreshold` | int | 14 | 1 - 19 | Hunger level to start eating |
| `autoElytraSwapEnabled` | bool | false | - | Auto elytra equip on fall |

## Example Config File

```json
{
  "reachEnabled": false,
  "reachDistance": 6.0,
  "flyEnabled": false,
  "flySpeed": 1.0,
  "espEnabled": false,
  "espPlayers": true,
  "espHostile": true,
  "espPassive": false,
  "espLines": false,
  "espPathTrace": false,
  "autoHitEnabled": false,
  "autoHitRange": 3.0,
  "autoHitPlayersOnly": false,
  "lowHealthKillEnabled": false,
  "lowHealthThreshold": 6.0,
  "autoKillWhenLowEnabled": false,
  "autoKillSelfHpThreshold": 6.0,
  "autoKillWhenLowRange": 4.0,
  "eatingAssistEnabled": false,
  "eatingHungerThreshold": 14,
  "jesusEnabled": false,
  "noFallEnabled": false,
  "fullbrightEnabled": false,
  "speedEnabled": false,
  "speedMultiplier": 2.0,
  "autoElytraSwapEnabled": false,
  "flyToCoordsEnabled": false,
  "flyToX": 0.0,
  "flyToY": 100.0,
  "flyToZ": 0.0,
  "flyToCoordsSpeed": 2.0,
  "walkToCoordsEnabled": false,
  "walkToX": 0.0,
  "walkToY": 64.0,
  "walkToZ": 0.0,
  "hudVisible": true
}
```
