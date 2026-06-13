# Stage 68 - Island Reset Full Wipe Checklist

> Scope: complete the Stage 20 deferred full island wipe behavior for built-in VoidIslandControl reset commands.

## Goals

- Extend `/island reset ... confirm` from starter-footprint cleanup to island-area cleanup.
- Reuse the existing island protection radius instead of adding another reset-only configuration.
- Keep reset destructive behavior bounded so one island reset cannot clear a neighboring island.

## Migrated Behavior

- `VoidIslandCommands` now clears the reset area with `Config.islandProtectionRadius`, capped by half the island spacing.
- Reset cleanup preserves the existing vertical reset band and then rebuilds the selected `IslandTemplate`.
- Owner-only reset, team-member rejection, dimension-missing failure, type switching, and teleport behavior remain unchanged.
- `IslandCommandGameTests.createInfoAndReset` now verifies protected-radius residue is cleared and an outside sentinel block remains.
- Stage 20 and the island command spec now describe reset as island-area cleanup.

## Deferred Migration

- Saved-name offline visit lookup is completed in Stage 69, and local saved-identity invite/trust is completed in Stage 71; full external profile lookup and rename conflicts remain deferred.
- Old VoidIslandControl leave event hooks and full event broadcast remain deferred.
- Old overworld-island save migration to `skyresources3:void_island` remains a separate decision.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runGameTestServer`
- [x] `./gradlew.bat build`
- [x] `git diff --check`
- [x] `git diff --cached --check`
