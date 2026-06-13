# Stage 69 - Island Offline Visit Lookup Checklist

> Scope: complete the saved-name offline lookup path for `/island visit <player>` without adding a full profile-cache layer.

## Goals

- Allow `/island visit <player>` to work when the target player is offline but already exists in island/team saved data.
- Preserve the existing online UUID-based behavior and self-visit rejection.
- Avoid adding a new account/profile cache until offline invite/trust or rename conflict handling needs it.

## Migrated Behavior

- `IslandSavedData` can find an island by saved owner name with case-insensitive matching.
- `TeamSavedData` can find a team by saved owner or member name with case-insensitive matching.
- `VoidIslandCommands.visitIsland` now resolves online players by UUID and offline players by saved owner/member names.
- Offline owner-name visit teleports to that owner's island.
- Offline team-member-name visit teleports to that team's owner island.
- `IslandCommandGameTests` covers both saved offline owner and saved offline team-member visit paths.
- The old visit-only "player is not online" translation key was removed because offline saved-name lookup is now supported.

## Deferred Migration

- Local saved-identity support for offline invite/trust is completed in Stage 71.
- Full external profile lookup, player-name history, and duplicate cached-name conflict resolution remain deferred.
- Old VoidIslandControl leave event hooks and full event broadcast remain deferred.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runGameTestServer`
- [x] `./gradlew.bat build`
- [x] `git diff --check`
- [x] `git diff --cached --check`
