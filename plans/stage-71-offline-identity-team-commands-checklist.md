# Stage 71 - Offline Identity Team Commands Checklist

> Scope: complete the local saved-identity path for offline team invites and island trust commands.

## Goals

- Add a durable local `UUID -> last known name` cache for players the mod has already seen.
- Let team invite and island trust commands resolve offline targets through that cache.
- Keep the feature bounded to local saved identities; do not add Mojang profile lookups or multi-name history.

## Migrated Behavior

- `PlayerIdentitySavedData` stores cached player UUIDs and their most recent known names in Minecraft SavedData.
- `PlayerIdentityEvents` refreshes the cache when a player logs in.
- Island creation, visit, team invite, invite accept, trust, and untrust paths refresh the command sender or target identity when available.
- `/skyresources3 team invite <player>` and `/island invite <player>` now accept offline cached names and persist invitations by cached UUID.
- `/island trust <player>` and `/skyresources3 team trust <player>` now accept offline cached names and persist trusted visitors by cached UUID.
- Online target behavior remains preferred and still sends the target-facing notification message.
- Offline cached names are matched case-insensitively; unresolved names still fail with localized feedback.
- `IslandCommandGameTests.offlineIdentityInviteAndTrust` covers cached offline invite/accept and cached offline trust.

## Deferred Migration

- Mojang profile service lookup for never-seen players remains out of scope.
- Player rename history and duplicate cached-name conflict resolution remain deferred.
- Old VoidIslandControl leave event hooks and full event broadcast remain deferred.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runGameTestServer`
- [x] `./gradlew.bat build`
- [x] `git diff --check`
- [x] `git diff --cached --check`
- [x] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"`
