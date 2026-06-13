# GUI Menu and Guide Automation Closeout

## Goal

Reduce the remaining GUI/guide migration risk by adding automated integrity checks for guide metadata, guide actions, structure references, translation keys, and menu registration coverage. This task supports the final migration objective from `plans/ask.md` while keeping real visual client click-through as a separate release-prep activity.

## What I Already Know

- `plans/final-migration-gap-audit.md` classifies the remaining Menus and GUI gap as startup-verified but still needing manual visual interaction before release.
- `plans/client-smoke-resource-license-closeout-checklist.md` records that `runClient` reached render/resource loading with SkyResources3, Jade, and JEI loaded, but guide clicks and machine screen visuals remain manual.
- The project uses Java 21, Minecraft 1.21.11, NeoForge 21.11.42, and GameTests through `./gradlew.bat runGameTestServer`.
- The desired next move should be a larger task that reduces repeated tool initialization and moves the migration closer to a verifiable final state.

## Requirements

- Add automated server-side validation for guide page data where feasible:
  - Every guide page has non-empty category, title, and body translation keys.
  - Every guide action has a resolvable display label source and internally consistent payload for its type.
  - Every `LINK` action targets an existing guide page.
  - Every structure action targets an existing guide structure definition.
  - Every inline `{action:n}` marker in translated guide body text refers to an action index present on that page.
  - Every guide page, image-action label, and referenced structure title translation key used by guide data exists in `assets/skyresources3/lang/en_us.json`.
- Add automated validation that the migrated menu type registry entries used by machine/client GUI work are all bound at GameTest runtime.
- Update migration/audit/checklist documentation to distinguish the new automated coverage from still-manual visual click-through.
- Keep the implementation small and direct: no new runtime dependencies, no client-only classes in server GameTests, and no broad refactor.

## Acceptance Criteria

- [x] A new or updated GameTest verifies guide metadata/action/translation integrity.
- [x] A new or updated GameTest verifies all expected menu registry entries resolve at runtime.
- [x] `ModGameTests` registers the new tests.
- [x] Documentation under `plans/` records the new automated coverage and keeps visual GUI testing marked manual.
- [x] `./gradlew.bat compileJava` passes.
- [x] `./gradlew.bat runData` passes.
- [x] `./gradlew.bat runGameTestServer` passes.
- [x] `./gradlew.bat build` passes.
- [x] `git diff --check` and `git diff --cached --check` pass.
- [x] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"` passes.

## Definition of Done

- Code and docs are committed in Chinese according to project rules.
- Trellis task is archived after verification.
- Session journal records the work and validation evidence.

## Out of Scope

- Do not claim full visual GUI coverage.
- Do not automate real client mouse/keyboard interaction in this task.
- Do not implement new machine behavior, JEI behavior, optional REI/EMI support, or old VoidIslandControl event compatibility.
- Do not rework guide rendering or screen layout unless an integrity test reveals a concrete defect.

## Technical Approach

Use GameTests for pure registry/data assertions that can run on the server side. Read language JSON through the classpath instead of client `Language` APIs. Keep menu validation to registry binding/resolution so the test remains server-safe and does not load client screens.

## Decision (ADR-lite)

**Context**: The remaining GUI gap includes both automatable data consistency and non-automatable visual click-through.

**Decision**: Automate guide/menu data integrity now, but preserve visual screen inspection as manual release-prep.

**Consequences**: The migration gets stronger automated evidence without pretending a headless server test proves client rendering or user interaction.

## Technical Notes

- Source requirement: `plans/ask.md`.
- Gap source: `plans/final-migration-gap-audit.md`.
- Recent smoke evidence: `plans/client-smoke-resource-license-closeout-checklist.md`.
- Relevant specs: `.trellis/spec/backend/guide-guidelines.md`, `.trellis/spec/backend/quality-guidelines.md`, `.trellis/spec/backend/directory-structure.md`, `.trellis/spec/guides/code-reuse-thinking-guide.md`, `.trellis/spec/guides/cross-layer-thinking-guide.md`.
