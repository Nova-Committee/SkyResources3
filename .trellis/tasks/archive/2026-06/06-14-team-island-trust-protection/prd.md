# Team Island Trust And Protection Config

## Goal

Advance the built-in VoidIslandControl and team migration by replacing the fixed-only island protection model with configurable range and an owner-managed trusted visitor list. This directly addresses the remaining team-permission TODOs without introducing a full role matrix yet.

## Requirements

- Add a server config value for island protection radius, preserving the current default of `128`.
- Store trusted visitors on each island record so solo island owners can grant access without implicitly creating a team.
- Add `/island trust <player>`, `/island untrust <player>`, and `/island trusted` aliases.
- Add `/skyresources3 team trust <player>`, `/skyresources3 team untrust <player>`, and `/skyresources3 team trusted` aliases for the same island-owner access list.
- Restrict trust management to personal island owners; team members cannot manage the owner's visitor list.
- Keep trust creation online-only because the project does not yet have a player-name history or profile-cache layer.
- Allow untrust by the stored visitor name so owners can remove visitors even when they are offline.
- Allow trusted visitors to break/place/right-click inside the protected island range, matching the explicit trust grant.
- Keep unrelated visitors denied by the same protection event handlers.
- Add localized command and protection messages.
- Update Trellis specs and migration plans to reflect the new permission layer.

## Acceptance Criteria

- [x] Existing island saved data remains readable with no `trusted_visitors` field.
- [x] New island records persist trusted visitors by UUID and last known name.
- [x] `/island trust <online player>` succeeds for an island owner and rejects self, missing target, non-owner team members, and redundant team members.
- [x] `/island untrust <name>` removes a stored visitor by name and reports a localized failure when absent.
- [x] `/island trusted` lists trusted visitors or reports an empty list.
- [x] Protection lookup uses `Config.islandProtectionRadius`.
- [x] Owner, team members, and trusted visitors may modify protected blocks; unrelated visitors are denied.
- [x] `./gradlew.bat compileJava runData build` passes.
- [x] `./gradlew.bat runGameTestServer` passes.
- [x] `git diff --check` passes.

## Definition Of Done

- Source code, generated resources, plans, and Trellis specs are updated together.
- The task is committed with a Chinese message after verification.
- The Trellis task is archived after the implementation commit.

## Technical Approach

Use the existing command and saved-data structure. Extend `IslandSavedData.IslandRecord` with an optional `trusted_visitors` map and small mutation helpers. Keep `TeamSavedData` focused on actual team membership and invitations. Add trust command handlers to `VoidIslandCommands`; update `IslandProtectionEvents` to resolve a single permission predicate: owner, team member, or trusted visitor.

## Decision (ADR-lite)

Context: The migration plan calls out visitor whitelist and configurable protection radius as remaining team-permission work. A full role matrix would be larger and is not needed to satisfy the current migration gap.

Decision: Implement a trusted visitor allowlist on island records, plus a config-backed protection radius. Defer role matrices and shared death-home behavior.

Consequences: Solo owners can trust visitors without creating teams, existing team behavior stays unchanged, and future role work can either extend the island access map or replace it with a richer permission model.

## Out Of Scope

- Offline trust creation from arbitrary names.
- Full role matrix or per-action permissions.
- Shared death-home handling.
- Full island wipe/reset expansion.
- Command-level GameTests; this stage keeps the existing server startup GameTest gate.

## Technical Notes

- Relevant specs: `.trellis/spec/backend/island-command-guidelines.md`, `.trellis/spec/backend/database-guidelines.md`, `.trellis/spec/backend/error-handling.md`, `.trellis/spec/backend/quality-guidelines.md`.
- Relevant code: `VoidIslandCommands`, `IslandSavedData`, `IslandProtectionEvents`, `Config`.
- Existing Stage 66 introduced fixed-radius protection with owner/team-member access.
