# Building from Source

## Requirements

- **JDK 21** or later
- **Git**

## Quick Build

```bash
git clone https://github.com/minerofthesoal/reach.git
cd reach
./gradlew build
```

The built JAR is at `build/libs/reach-fly-mod-<version>.jar`.

## Building for Different Minecraft Versions

Each Minecraft version has its own branch with the correct dependencies and API adaptations.

### Minecraft 1.21.11 (default)

```bash
git checkout claude/minecraft-reach-fly-mod-0zKqV
./gradlew build
```

- Fabric Loom: 1.13.6
- Gradle: 8.14
- Fabric API: 0.141.3+1.21.11
- Yarn Mappings: 1.21.11+build.4

### Minecraft 1.21.4

```bash
git checkout mc-1.21.4
./gradlew build
```

- Fabric Loom: 1.9-SNAPSHOT
- Gradle: 8.11
- Fabric API: 0.110.5+1.21.4
- Yarn Mappings: 1.21.4+build.2

### Minecraft 1.21.1

```bash
git checkout mc-1.21.1
./gradlew build
```

- Fabric Loom: 1.7-SNAPSHOT
- Gradle: 8.8
- Fabric API: 0.102.0+1.21.1
- Yarn Mappings: 1.21.1+build.3

## API Differences Between Versions

Key API changes that differ between branches:

| API | 1.21.11 | 1.21.4 | 1.21.1 |
|-----|---------|--------|--------|
| Entity position | `getEntityPos()` | `getPos()` | `getPos()` |
| Lerped position | `getLerpedPos(float)` | `getLerpedPos(float)` | `prevX/Y/Z` interpolation |
| Camera position | `getCameraPos()` | `getPos()` | `getPos()` |
| Tick delta | `getTickProgress(boolean)` | `getTickDelta(boolean)` | `getTickDelta(boolean)` |
| Keybind category | `KeyBinding.Category` record | `String` | `String` |
| Inventory slot | `getSelectedSlot()`/`setSelectedSlot()` | `selectedSlot` field | `selectedSlot` field |
| Reach attribute | `BLOCK_INTERACTION_RANGE` | `BLOCK_INTERACTION_RANGE` | `PLAYER_BLOCK_INTERACTION_RANGE` |
| OnGroundOnly packet | 2 params | 2 params | 1 param |
| World top Y | `getTopYInclusive()` | N/A (use constant) | `getTopY()` |

## CI/CD

GitHub Actions automatically builds on push to all branches. The workflow:

1. Checks out the branch
2. Sets up JDK 21
3. Uses `./gradlew build` (respects each branch's Gradle wrapper version)
4. Uploads the JAR as a build artifact
5. Creates a GitHub Release on push

## Downloading Pre-built JARs

1. Go to the [Actions tab](https://github.com/minerofthesoal/reach/actions)
2. Select the build for your Minecraft version's branch
3. Download the `reach-fly-mod` artifact
