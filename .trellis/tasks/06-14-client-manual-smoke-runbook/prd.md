# Client Manual Smoke Runbook

## Goal

Turn the final open manual GUI validation item into a concrete, repeatable smoke-test package. The package should refresh client startup evidence and give the release tester exact manual steps for guide actions, JEI behavior, representative machine menus, and island/team flows.

## What I Already Know

- `plans/final-migration-gap-audit.md` says automated final gates have been refreshed, but visual GUI click-through remains manual release-prep.
- `plans/final-validation-compatibility-closeout-checklist.md` keeps `runClient` GUI interaction smoke unchecked.
- `plans/client-smoke-resource-license-closeout-checklist.md` already records an earlier controlled `runClient` startup smoke and explicitly keeps click-through as manual.
- Current tools can run Gradle and inspect logs, but cannot reliably click the native Minecraft client window or verify visual layout.

## Requirements

- Run a fresh controlled `./gradlew.bat runClient --no-daemon` startup smoke and capture current log evidence without leaving client/Gradle processes running indefinitely.
- Create a concise manual smoke runbook under `plans/` with exact preparation, commands, and pass/fail checks.
- Update existing final validation/client smoke documents to reference the runbook and refreshed startup evidence.
- Keep the GUI click-through checkbox open unless manual interaction is actually completed.
- Do not add gameplay code, dependencies, or optional integrations for this task.

## Acceptance Criteria

- [x] Controlled `runClient` startup smoke captures current client loading evidence.
- [x] No intentional Gradle/Java client process is left running by the startup smoke.
- [x] Manual smoke runbook covers guide search/actions, JEI present/fallback expectations, representative machine menus, and island/team flows.
- [x] Final validation docs reference the runbook and keep visual click-through manual.
- [x] `git diff --check` passed.
- [x] `git diff --cached --check` passed.
- [x] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"` passed.

## Definition of Done

- Documentation changes are committed in Chinese.
- Trellis task is archived.
- Session journal records the startup evidence and remaining manual boundary.

## Out of Scope

- Do not claim manual GUI validation is complete unless a human actually performs the click-through.
- Do not implement new client automation, UI tests, commands, or gameplay behavior.
- Do not add or remove optional integration dependencies.

## Technical Approach

Use a PowerShell watchdog to run `./gradlew.bat runClient --no-daemon`, wait for current startup evidence in `run/logs/latest.log`, and stop the spawned process tree. Then document a release-tester runbook that narrows manual work to explicit click/observe checks.

## Decision (ADR-lite)

**Context**: Native Minecraft client GUI clicks are outside the reliable scope of the current headless/tool-driven validation, but the release process still needs a concrete path to complete the final manual item.

**Decision**: Refresh startup evidence and produce an auditable manual runbook instead of falsely marking visual validation as automated.

**Consequences**: Migration evidence becomes easier to finish, while the final visual checkbox remains honest until a tester runs it.

## Technical Notes

- Source requirement: `plans/ask.md`.
- Remaining blocker source: `plans/final-migration-gap-audit.md`.
- Manual validation source: `plans/final-validation-compatibility-closeout-checklist.md`.
- Prior startup smoke source: `plans/client-smoke-resource-license-closeout-checklist.md`.
