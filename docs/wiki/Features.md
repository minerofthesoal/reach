# Features Overview

Complete documentation for all Reach & Fly Mod features.

---

## Reach

Extends the player's block and entity interaction distance beyond the vanilla 4.5/3.0 block limits.

- Works in singleplayer by modifying both client-side and server-side attributes
- Affects block breaking/placing AND entity targeting
- Range: 3.0 - 50.0 blocks

| Config Option | Default | Range |
|---------------|---------|-------|
| `reachEnabled` | `false` | - |
| `reachDistance` | `6.0` | 3.0 - 50.0 |

**Note:** On multiplayer servers, only the client-side crosshair targeting is extended. Server-side validation depends on the server.

---

## Fly

Enables survival/adventure mode flight with adjustable speed.

- Automatically prevents fall damage while flying
- Sends landing warning when disabled mid-air
- Does not override creative/spectator flight

| Config Option | Default | Range |
|---------------|---------|-------|
| `flyEnabled` | `false` | - |
| `flySpeed` | `1.0` | 0.1 - 10.0 |

---

## ESP (Entity Highlighting)

Visual entity tracking with two display modes:

### Glow Effect
When ESP is enabled, entities glow through walls with color-coded outlines:
- **Red:** Players
- **Orange:** Hostile mobs
- **Green:** Passive mobs

### Tracer Lines (espLines)
Draws colored lines from the screen crosshair to each visible entity. Uses world-to-screen projection math.

### Path Trace (espPathTrace)
Draws ground-level waypoint dots from the player to each entity, following terrain elevation. Shows the path you'd walk to reach them (max 200 blocks).

| Config Option | Default | Description |
|---------------|---------|-------------|
| `espEnabled` | `false` | Master ESP toggle |
| `espPlayers` | `true` | Track players |
| `espHostile` | `true` | Track hostile mobs |
| `espPassive` | `false` | Track passive mobs |
| `espLines` | `false` | Draw tracer lines |
| `espPathTrace` | `false` | Draw path trace waypoints |

---

## Auto Hit

Automatically attacks the nearest living entity within range.

- Respects attack cooldown (only swings when fully charged)
- Optional players-only filter
- Performs swing animation

| Config Option | Default | Range |
|---------------|---------|-------|
| `autoHitEnabled` | `false` | - |
| `autoHitRange` | `3.0` | 1.0 - 50.0 |
| `autoHitPlayersOnly` | `false` | - |

---

## Low Health Kill

Prioritizes and attacks entities that are below a health threshold.

- Scans all nearby entities for those below the HP threshold
- Targets the weakest entity first
- Uses reach distance if Reach is enabled, otherwise 4.5 blocks

| Config Option | Default | Range |
|---------------|---------|-------|
| `lowHealthKillEnabled` | `false` | - |
| `lowHealthThreshold` | `6.0` | 1.0 - 20.0 |

---

## Auto Kill When Low HP

Defensive mode: automatically attacks nearby entities when YOUR health drops below a threshold.

- Only activates when the player's own health is low
- Independent from Low Health Kill (that targets low-HP enemies)

| Config Option | Default | Range |
|---------------|---------|-------|
| `autoKillWhenLowEnabled` | `false` | - |
| `autoKillSelfHpThreshold` | `6.0` | 1.0 - 20.0 |
| `autoKillWhenLowRange` | `4.0` | 1.0 - 50.0 |

---

## Eating Assist

Automatically eats food when hunger drops below a threshold.

- Searches hotbar slots 0-8 for food items
- Picks the highest-nutrition food available
- Uses `interactItem()` instead of holding the use key, so it does NOT block other features (auto hit, ESP, etc.)
- Returns to original hotbar slot when done eating
- Safety timeout after 60 ticks

| Config Option | Default | Range |
|---------------|---------|-------|
| `eatingAssistEnabled` | `false` | - |
| `eatingHungerThreshold` | `14` | 1 - 19 |

---

## Jesus (Walk on Water)

Walk on the surface of water and lava.

- Detects liquid at player's feet and prevents sinking
- If submerged, pushes player upward (velocity 0.11)
- If on surface, zeros vertical velocity and sets on-ground state
- **Hold sneak to sink** intentionally
- Does not interfere with regular flying

| Config Option | Default |
|---------------|---------|
| `jesusEnabled` | `false` |

---

## NoFall

Prevents fall damage by spoofing on-ground status to the server.

- Activates when fall distance exceeds 2.0 blocks
- Sends a `PlayerMoveC2SPacket.OnGroundOnly(true)` packet
- Resets fall distance client-side

| Config Option | Default |
|---------------|---------|
| `noFallEnabled` | `false` |

---

## Fullbright

Sets game gamma to maximum (16.0) for full visibility in darkness.

- Saves and restores original gamma when toggled
- No performance impact

| Config Option | Default |
|---------------|---------|
| `fullbrightEnabled` | `false` |

---

## Speed

Multiplies horizontal ground movement speed.

- Only applies while on the ground and actively moving
- Multiplies X and Z velocity components
- Does not affect vertical movement (falling/jumping)

| Config Option | Default | Range |
|---------------|---------|-------|
| `speedEnabled` | `false` | - |
| `speedMultiplier` | `2.0` | 1.0 - 10.0 |

---

## Auto Elytra Swap

Automatically equips elytra when falling and swaps back to chestplate when grounded.

- **Falling:** Searches inventory for elytra and equips it
- **Grounded:** Searches for any chestplate (Netherite > Diamond > Iron > Gold > Chain > Leather) and re-equips it
- 5-tick cooldown between swaps to prevent flickering

| Config Option | Default |
|---------------|---------|
| `autoElytraSwapEnabled` | `false` |

---

## Fly to Coords

Automatically flies the player to target coordinates.

- Enables flight capability and sets velocity toward target
- Rotates player to face the direction of travel
- Arrives within 3 blocks of target, then disables
- Shows actionbar messages for navigation feedback

| Config Option | Default | Range |
|---------------|---------|-------|
| `flyToCoordsEnabled` | `false` | - |
| `flyToX` | `0` | -30000 - 30000 |
| `flyToY` | `100` | -64 - 320 |
| `flyToZ` | `0` | -30000 - 30000 |
| `flyToCoordsSpeed` | `2.0` | 0.5 - 20.0 |

---

## Walk to Coords

Simple Baritone-like automatic walking to target coordinates.

- Simulates W key, sprint, and jump inputs
- Basic obstacle detection: jumps over 1-block-high obstacles
- Auto-jumps when target is above current Y level
- Stops within 2 blocks horizontal / 3 blocks vertical of target
- Sends actionbar progress messages

| Config Option | Default | Range |
|---------------|---------|-------|
| `walkToCoordsEnabled` | `false` | - |
| `walkToX` | `0` | -30000 - 30000 |
| `walkToY` | `64` | -64 - 320 |
| `walkToZ` | `0` | -30000 - 30000 |

---

## HUD Overlay

On-screen status display showing all active features.

- Renders in the top-left corner
- Green = enabled, Red = disabled, Yellow = active navigation
- Shows current config values (distances, speeds, thresholds)
- Hidden when the debug screen (F3) is open

| Config Option | Default |
|---------------|---------|
| `hudVisible` | `true` |
