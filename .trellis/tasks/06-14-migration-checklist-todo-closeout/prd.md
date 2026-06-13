# Migration Checklist TODO Closeout

## Goal

Synchronize stale unchecked TODOs in `plans/` with the current migration facts so the final `plans/ask.md` completion audit is not polluted by old stage notes that later tasks already resolved.

## What I Already Know

- `plans/ask.md` requires a complete migration and allows optional integrations to be deferred only when no suitable target version/API exists and a TODO is recorded.
- `plans/final-migration-gap-audit.md` still lists checklist cleanup as a must-finish item before calling the migration complete.
- `plans/integration-availability.md` records JEI, Jade, and Integrated Dynamics as implemented; other old integrations remain deferred.
- `plans/gui-menu-guide-automation-closeout-checklist.md` and `plans/client-manual-smoke-runbook.md` keep visual GUI click-through as manual release-prep.
- Several old stage checklists still contain unchecked items that have become stale after later stages, especially JEI integration, combustion controller/collector runtime, and stable cauldron-clean recipe slices.

## Requirements

- Update only plan/checklist documentation unless a real contradiction reveals missing implementation.
- Mark stale checklist items as completed only when later repository evidence proves they are done.
- Keep true deferred items unchecked when they depend on target mod availability, compatibility policy, visual/manual validation, or enhancement scope.
- Make JEI/Jade/Integrated Dynamics status consistent across early stage checklists and the integration availability matrix.
- Make the final migration gap audit explicitly state that stale checklist cleanup was performed and that only true deferrals/manual validation remain.

## Acceptance Criteria

- [x] Early stage checklists no longer claim JEI or guide recipe action work is wholly deferred when JEI integration is already implemented.
- [x] Stage 26 no longer lists combustion controller/collector runtime as waiting for migration after the automation/runtime closeout.
- [x] Stage 13 and Rock Cleaner/Dirty Gem-related checklists distinguish stable `cauldronclean` completion from dynamic/tagged compatibility deferrals.
- [x] Stage 59 no longer leaves the commit/archive step unchecked after the integration audit task was committed and archived.
- [x] `plans/final-migration-gap-audit.md` reflects that stale checklist cleanup has been completed, while the manual GUI click-through remains open.
- [x] `git diff --check` passed.
- [x] `git diff --cached --check` passed.
- [x] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"` passed.

## Definition of Done

- Documentation changes are committed in Chinese.
- Trellis task is archived.
- Session journal records the cleanup scope and remaining open boundary.

## Out of Scope

- Do not implement optional mod integrations without verified Minecraft 1.21.11 NeoForge targets.
- Do not mark manual GUI click-through complete from automated startup logs or GameTests.
- Do not edit archived historical PRDs just because their old acceptance checkboxes were unchecked.
- Do not add gameplay behavior, dependencies, or tests unless the audit reveals a real current defect.

## Technical Approach

Use the final migration audit, integration matrix, and later-stage evidence as the source of truth. Update old stage checklists only where they are stale or ambiguous. Preserve explicit TODOs for dynamic/tagged recipes, unavailable integrations, resource visual variants, old-save migration, and richer team roles.

## Decision (ADR-lite)

**Context**: The repository already contains enough migrated functionality and validation evidence, but old stage checklist TODOs can make the migration look less complete than it is.

**Decision**: Perform a docs-only checklist synchronization. Mark stale completed items as complete with references to the later stage or closeout evidence, and leave true deferrals as TODOs.

**Consequences**: The final completion audit becomes sharper without over-claiming GUI/manual validation or optional compatibility work.

## Technical Notes

- Source requirement: `plans/ask.md`.
- Main audit source: `plans/final-migration-gap-audit.md`.
- Integration evidence: `plans/integration-availability.md`.
- Manual GUI boundary: `plans/client-manual-smoke-runbook.md`, `plans/gui-menu-guide-automation-closeout-checklist.md`.
- Relevant specs: `.trellis/spec/backend/quality-guidelines.md`, `.trellis/spec/backend/integration-guidelines.md`, `.trellis/spec/backend/recipe-guidelines.md`, `.trellis/spec/backend/guide-guidelines.md`.
