# Combustion Automation Regression Closeout

## Goal

Close the remaining combustion-machine enhancement coverage from `plans/final-migration-gap-audit.md` by adding focused GameTests for Smart Combustion Controller priority behavior and Combustion Collector overflow/blocking behavior. This improves confidence in migrated multiblock automation without expanding gameplay scope.

## What I Already Know

- `plans/final-migration-gap-audit.md` lists additional GameTests for combustion controller priority and collector overflow as enhancement coverage.
- The project already has function-style NeoForge GameTests registered through `ModGameTests`.
- Existing `MachineRuntimeGameTests` covers condenser behavior and provides local patterns for machine runtime assertions.
- The task should stay server-side and avoid client GUI or optional integration work.

## Requirements

- Add a GameTest proving the Smart Combustion Controller respects priority/filter selection when multiple matching combustion recipes are possible.
- Add a GameTest proving combustion automation does not lose output when the collector path is full or blocked.
- Reuse existing machine/runtime helpers and APIs where possible; keep new helpers local to tests unless they are broadly useful.
- Register the new GameTests through `ModGameTests`.
- Update migration/audit/checklist documentation to mark this enhancement coverage as automated.

## Acceptance Criteria

- [x] New GameTest covers combustion controller priority/filter behavior.
- [x] New GameTest covers combustion collector overflow or blocked-output behavior.
- [x] `ModGameTests` registers the new tests.
- [x] Documentation under `plans/` records the new automation coverage and removes this item from the open enhancement wording.
- [x] `./gradlew.bat compileJava` passes.
- [x] `./gradlew.bat runData` passes.
- [x] `./gradlew.bat runGameTestServer` passes.
- [x] `./gradlew.bat build` passes.
- [x] `git diff --check` and `git diff --cached --check` pass.
- [x] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"` passes.

## Definition of Done

- Code and docs are committed in Chinese.
- Trellis task is archived after verification.
- Session journal records the work and validation evidence.

## Out of Scope

- Do not add new combustion recipes or new machine behavior unless a test exposes an existing bug.
- Do not implement RF/fluid heater variants.
- Do not change client screens, JEI/Jade integrations, or optional mod compatibility.
- Do not claim visual GUI coverage.

## Technical Approach

Inspect the current combustion controller, collector, block entity, and recipe code. Add focused GameTests that drive the server-side machine logic directly through existing block entities or public APIs, matching the established function-style GameTest registration pattern.

## Decision (ADR-lite)

**Context**: The migration audit classifies the combustion priority/overflow cases as enhancement coverage, not missing gameplay.

**Decision**: Add narrow server-side regression tests and only patch runtime code if the tests reveal a concrete bug.

**Consequences**: The migration gets stronger machine automation evidence while avoiding speculative feature expansion.

## Technical Notes

- Source requirement: `plans/ask.md`.
- Gap source: `plans/final-migration-gap-audit.md`.
- Existing checklist source: `plans/stage-39-combustion-automation-checklist.md`.
- Relevant specs: `.trellis/spec/backend/recipe-guidelines.md`, `.trellis/spec/backend/quality-guidelines.md`, `.trellis/spec/backend/directory-structure.md`, `.trellis/spec/guides/code-reuse-thinking-guide.md`, `.trellis/spec/guides/cross-layer-thinking-guide.md`.
