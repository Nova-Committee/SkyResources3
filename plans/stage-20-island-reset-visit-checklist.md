# Stage 20 Implementation Checklist: Island reset, visit, and spawn commands

> Scope: `plans/migration-plan.md` stage 7 VoidIslandControl built-in command compatibility.

## Goals

- Restore the minimum `/island reset` flow without adding a new island type system yet.
- Restore `/island visit <player>` for online players and saved offline owner/team names with personal or team islands.
- Restore `/island spawn` as a temporary spawn teleport while custom void spawn/world generation remains pending.

## Migrated Behavior

- Added `/island reset` and `/skyresources3 island reset` as non-destructive prompts.
- Added `/island reset confirm` and `/skyresources3 island reset confirm` to clear the island reset area and rebuild the starter platform.
- Reset is restricted to personal island owners; team members cannot reset the team owner's island.
- Reset clears the configured island protection radius, capped by island spacing so neighboring islands are not wiped, before rebuilding the selected starter template.
- Added `/island visit <player>` and `/skyresources3 island visit <player>` for online player islands and saved offline owner/team names.
- Visit resolves a target player's own island first, then the target's team owner island; offline lookup uses last saved `IslandSavedData` / `TeamSavedData` names.
- Added `/island spawn` and `/skyresources3 island spawn` to teleport to the temporary overworld origin-heightmap spawn placeholder.
- All new user-facing messages use translation keys in `en_us.json`.

## Deferred Migration

- Full player-name history/profile-cache support remains deferred for offline invite/trust and rename conflict handling.
- Custom void-world spawn, vanilla shared-spawn resolution, island type templates, and world presets are deferred to the world generation phase.
- Visit permissions and visitor interaction limits are handled by Stage 67 configurable island protection and trusted visitors.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
