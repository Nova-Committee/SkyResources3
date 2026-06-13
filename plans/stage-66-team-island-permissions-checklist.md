# Stage 66 - Team Island Permissions

> Scope: add the first real island protection boundary so team members can collaborate while visitors cannot modify visited islands.

## Checklist

- [x] Create Trellis task and PRD.
- [x] Read island command, persistence, error handling, quality, and cross-layer specs.
- [x] Verify NeoForge 21.11.42 block break, block place, and right-click event signatures from local jars.
- [x] Add island horizontal-range lookup to `IslandSavedData`.
- [x] Add server-side island protection event handlers.
- [x] Register protection handlers before custom world-mutating interaction handlers.
- [x] Allow island owners and team members to modify protected island areas.
- [x] Deny visitors and unrelated players from breaking, placing, or right-clicking protected island blocks.
- [x] Add localized denial message.
- [x] Update island command spec, migration plan, and deferred checklist notes.
- [x] Run compileJava, runData, build, runGameTestServer.
- [x] Chinese commit and archive Trellis task.

## Stage 67 Follow-up

- Configurable protection radius and trusted visitors are handled by `plans/stage-67-team-island-trust-protection-checklist.md`.
