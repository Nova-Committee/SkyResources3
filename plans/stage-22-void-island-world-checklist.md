# Stage 22 - Void Island World Checklist

## Scope

- Add a data-pack dimension type and empty flat dimension for `skyresources3:void_island`.
- Add a selectable `skyresources3:void_island` world preset using an empty overworld flat generator.
- Add the world preset to the vanilla `normal` preset tag.
- Create new islands in `skyresources3:void_island` when the dimension is available.
- Move `/island spawn` from the temporary overworld heightmap target to a generated void-island spawn platform.

## Evidence

- `VoidIslandWorld` centralizes the dimension key, island Y level, spawn platform, and fallback behavior.
- `VoidIslandCommands` still stores island/team data in the overworld saved data while using the void dimension for new island placement.
- `src/main/resources/data/skyresources3/dimension/void_island.json` uses an empty `minecraft:flat` generator with no structures.
- `src/main/resources/data/skyresources3/worldgen/world_preset/void_island.json` exposes an empty-world preset for new worlds.

## TODO

- Add command-level GameTests once the project has command execution fixtures.
- Decide whether existing overworld islands should be migrated to the void dimension or left in place.
- Replace the simple spawn grass platform with configurable VoidIslandControl-style spawn settings if needed.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`
