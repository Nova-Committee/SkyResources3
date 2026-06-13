# Client GUI Smoke Execution

## Goal

Attempt the final `runClient` visual/click-through smoke from `plans/client-manual-smoke-runbook.md` in the current desktop environment and record auditable evidence. If native GUI automation is not reliable enough, document the exact blocker without marking the manual checklist complete.

## What I Already Know

- `plans/final-migration-gap-audit.md` says the migrated main mod body is otherwise feature-complete enough for closeout, but visual GUI click-through remains the only must-finish item before declaring migration complete.
- `plans/client-manual-smoke-runbook.md` defines the required guide, JEI, representative menu, and island/team manual flows.
- Previous controlled `runClient` startup evidence reached render/resource loading with SkyResources3, Jade, and JEI.
- `run/saves/` already contains local worlds (`New World`, `New World (1)`, `New World (2)`), so a quickplay/singleplayer path may avoid world creation screens.
- The current toolset can run Gradle and PowerShell scripts. It may be able to launch the client, detect the window, capture screenshots, and send keyboard/mouse input, but this still needs verification.

## Requirements

- Start `./gradlew.bat runClient --no-daemon` in a controlled way and avoid leaving Gradle/Java client processes running after the smoke attempt.
- Prefer an existing local world or quickplay path if available; otherwise interact with the main menu only if screenshots prove the state is clear.
- Capture screenshots/log excerpts for each successfully verified GUI state.
- Verify as much of the runbook as is genuinely observable:
  - guide opens from `G`;
  - guide search/actions and JEI recipe action behavior;
  - representative machine screens open and render without obvious layout overlap;
  - island/team commands execute without disconnects and show readable localized feedback.
- Update final/client smoke documents only for steps actually observed. Do not mark the manual GUI item complete from startup logs alone.

## Acceptance Criteria

- [ ] Client launches and either reaches an existing world or the blocker is recorded with log/screenshot evidence.
- [ ] Any completed runbook steps are recorded with date, profile/world, and evidence path.
- [ ] No intentional Gradle/Java client process remains running after the attempt.
- [ ] Final validation/client smoke docs are updated truthfully.
- [ ] `git diff --check` and `git diff --cached --check` pass if documentation changes are made.
- [ ] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"` passes.

## Definition of Done

- Documentation changes are committed in Chinese if the attempt produces evidence or updates checklist state.
- Trellis task is archived.
- Session journal records whether manual GUI validation was completed, partially completed, or blocked by desktop automation limits.

## Out of Scope

- Do not add gameplay behavior or dependencies just to make the smoke easier.
- Do not claim visual validation is complete without screenshots or equivalent direct evidence.
- Do not push or change branches.
- Do not include `run/logs`, screenshots, or temporary scripts in Git unless explicitly needed as durable evidence; prefer summarized plan docs.

## Technical Approach

Inspect Gradle run capabilities and the local `run/` directory, then launch the client with a watchdog. Use PowerShell window detection, optional screenshot capture, and targeted keyboard/mouse input only when the active window state is visible enough to make the next action safe. Stop and record the blocker if the desktop session cannot provide reliable visual feedback.

## Decision (ADR-lite)

**Context**: The final open item is visual GUI validation, which existing GameTests cannot prove.

**Decision**: Attempt native client GUI smoke with controlled process management and screenshots before deciding whether the goal can be completed in this environment.

**Consequences**: If automation succeeds, the migration can move closer to completion; if not, the project will have precise evidence explaining why a human desktop click-through is still required.

## Technical Notes

- Source requirement: `plans/ask.md`.
- Manual smoke source: `plans/client-manual-smoke-runbook.md`.
- Final audit source: `plans/final-migration-gap-audit.md`.
- Gradle run config source: `build.gradle`.
