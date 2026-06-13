# Client GUI Focused Smoke Closeout

## Goal

Continue the final migration closeout by retrying the remaining visual GUI smoke checks from a cleaner desktop setup:
hide the launcher terminal, keep the Minecraft GLFW window focused, and record only directly observed guide, JEI,
machine GUI, and island/team results. Also fix any stale smoke-test instructions discovered during the attempt.

## What I Already Know

- `plans/ask.md` requires a complete SkyResources migration across blocks/items/network/GUI/structures/commands/worldgen,
  with compatible integrations completed where feasible and TODOs for unavailable target versions.
- `plans/final-migration-gap-audit.md` says the main migration body is otherwise close enough for closeout, but visual
  GUI click-through is still the must-finish item before claiming full completion.
- `plans/client-gui-smoke-execution-evidence.md` records that the client can reach a local world and that the guide
  default key conflict with vanilla `G` was fixed by moving the default guide key to `Y`.
- The previous attempt was polluted by visible Windows Terminal / PowerShell windows and unreliable mouse focus. This
  task should launch helper processes hidden and keep Minecraft as the only visible target when possible.
- `plans/client-manual-smoke-runbook.md` still has at least one stale `G` reference in the JEI-absent fallback section.

## Requirements

- Start `./gradlew.bat runClient --no-daemon` through a hidden launcher, with stdout/stderr written under the task
  evidence directory.
- Do not leave intentional Gradle/Java Minecraft client processes running after the attempt.
- Prefer existing local worlds; avoid creating or deleting worlds during automation.
- Verify as much of the runbook as direct screenshots prove:
  - guide opens with the configured guide key, default `Y`;
  - guide search/actions and JEI recipe action behavior if reliable interaction is possible;
  - representative machine menus if reliable chat/give/place/right-click interaction is possible;
  - island/team commands if reliable chat input is possible.
- Update docs only for directly observed outcomes. Keep remaining manual items unchecked if visual evidence is missing.
- Fix stale runbook instructions that still mention old guide key behavior.

## Acceptance Criteria

- [ ] Either the focused GUI attempt records guide/JEI/machine/island evidence, or the remaining blocker is documented
  with clearer root cause than the previous attempt.
- [ ] `plans/client-manual-smoke-runbook.md` no longer instructs no-JEI fallback users to press old `G`.
- [ ] Any changed docs explain which GUI items are complete, partial, or still manual.
- [ ] No intentional Minecraft client window/process remains running after the attempt.
- [ ] `git diff --check` and `git diff --cached --check` pass.
- [ ] `./gradlew.bat build` passes if code changes are made; docs-only changes may use targeted checks.
- [ ] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"` passes.

## Definition of Done

- Work changes are committed with a Chinese message.
- Trellis task is archived.
- Session journal records whether focused GUI validation completed, partially completed, or remained blocked.

## Technical Approach

Use a hidden PowerShell launcher for `runClient`, then interact only with the Minecraft window. Try keyboard-first paths
where possible because they were more reliable than physical mouse coordinates in the previous attempt. Capture screenshots
after every meaningful visible state and stop if focus/input delivery becomes ambiguous.

## Decision (ADR-lite)

**Context**: The previous GUI attempt reached a world but could not complete guide clicks because desktop window focus was
polluted by visible helper terminals.

**Decision**: Retry once with hidden helper windows and stricter screenshot-driven state checks before deciding whether
the remaining GUI smoke is truly manual-only in this environment.

**Consequences**: If it succeeds, more manual items can be closed with evidence. If it fails, the project gets a more
precise blocker record and still avoids overstating migration completion.

## Out of Scope

- Do not add gameplay behavior solely to ease smoke testing.
- Do not create a custom GUI automation framework.
- Do not claim visual coverage from GameTests, logs, or screenshots that do not show the target state.
- Do not push or change branches.

## Technical Notes

- Source requirement: `plans/ask.md`.
- Runbook source: `plans/client-manual-smoke-runbook.md`.
- Prior focused evidence summary: `plans/client-gui-smoke-execution-evidence.md`.
- Final audit source: `plans/final-migration-gap-audit.md`.
