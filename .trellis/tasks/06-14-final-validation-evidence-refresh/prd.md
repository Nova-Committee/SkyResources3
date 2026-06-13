# Final Validation Evidence Refresh

## Goal

Refresh the final migration validation evidence after the latest automation closeouts and make the remaining blocker/enhancement classification explicit. This moves the project closer to a defensible "main migration body complete" state without expanding gameplay scope.

## What I Already Know

- `plans/ask.md` requires a complete migration covering blocks/items, networking, menus/GUI, multiblocks, commands, world generation, built-in VoidIslandControl behavior, team features, and best-effort integrations with TODOs for unavailable targets.
- `plans/final-migration-gap-audit.md` now classifies most remaining work as final validation, allowed deferral, or enhancement.
- Combustion controller priority and collector overflow GameTests were added after the earlier final-validation checklist was first written.
- Manual GUI click-through remains the only explicitly open pre-release validation item that cannot be honestly proven by headless Gradle gates.

## Requirements

- Re-run the current final validation gates in the current worktree: `runData`, `runGameTestServer`, `build`, `git diff --check`, `git diff --cached --check`, and the Serena Java probe.
- Re-run targeted legacy/reference scans that support the compatibility closeout.
- Update final validation and migration audit documentation with the refreshed post-combustion evidence.
- Keep optional integrations and manual GUI click-through classified accurately; do not claim visual GUI coverage unless an interactive client session is completed.
- Preserve the existing policy decisions for ARR resources, VoidIslandControl event hooks, old save migration, and optional integrations.

## Acceptance Criteria

- [x] `./gradlew.bat runData` passes in the current state.
- [x] `./gradlew.bat runGameTestServer` passes in the current state.
- [x] `./gradlew.bat build` passes in the current state.
- [x] Legacy network/package/resource scans are recorded and pass.
- [x] `git diff --check` and `git diff --cached --check` pass.
- [x] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"` passes.
- [x] `plans/final-validation-compatibility-closeout-checklist.md` records refreshed evidence and the manual GUI boundary.
- [x] `plans/final-migration-gap-audit.md` no longer lists stale completed automation gaps and clearly separates allowed deferrals from release-prep manual work.

## Definition of Done

- Documentation updates are committed in Chinese.
- Trellis task is archived after verification.
- Session journal records the validation evidence and commit.

## Out of Scope

- Do not implement optional integrations whose compatible 1.21.11 NeoForge artifacts are not already verified.
- Do not add new gameplay behavior.
- Do not implement RF/fluid heater variants.
- Do not claim that manual GUI click-through is complete unless it actually happens.

## Technical Approach

Use the existing Gradle gates and targeted scans as direct evidence. Update only the plan/checklist documents needed to make the current migration state auditable. If a gate fails, fix the concrete defect first and then rerun the affected gate.

## Decision (ADR-lite)

**Context**: The project has accumulated several closeout commits after the original final validation checklist, so the release evidence should reflect the current state rather than earlier snapshots.

**Decision**: Run the final gates again and refresh the final validation documents, while keeping manual visual GUI verification as a separate release-prep item.

**Consequences**: The migration status becomes easier to audit, but interactive GUI validation remains explicitly outside headless proof.

## Technical Notes

- Source requirement: `plans/ask.md`.
- Current audit source: `plans/final-migration-gap-audit.md`.
- Current validation source: `plans/final-validation-compatibility-closeout-checklist.md`.
- Related integration policy: `plans/integration-availability.md`.
