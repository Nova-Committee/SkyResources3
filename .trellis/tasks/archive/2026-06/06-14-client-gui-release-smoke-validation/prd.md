# Client GUI Release Smoke Validation

## Goal

Finish the remaining release-prep GUI smoke validation required by `plans/ask.md` and
`plans/final-migration-gap-audit.md`: prove, with direct screenshots/logs from a focused client run, whether the migrated
guide, JEI action path, representative machine menus, and island/team command flows work in the Minecraft client.

## What I Already Know

- `plans/ask.md` requires a full SkyResources3 migration to Minecraft 1.21.11 NeoForge, including GUI, commands,
  world generation, built-in VoidIslandControl behavior, team features, and feasible optional integrations.
- The main migration body is already documented as migrated or intentionally deferred in
  `plans/final-migration-gap-audit.md`.
- The only release-blocking migration evidence gap is visual/click-through client GUI smoke validation.
- Previous desktop attempts proved the client can reach a local world and found/fixed the guide default key conflict by
  moving `key.skyresources3.guide` from `G` to `Y`.
- A hidden-launcher retry removed visible terminal focus pollution but produced only white-window captures after
  SkyResources3/Jade/JEI loaded, so it did not close any visual click-through item.

## Requirements

- Use one large focused client validation pass instead of splitting guide, JEI, machine, and island checks into many
  small tasks.
- Start from the existing local dev client profile and existing local worlds; do not create or delete worlds unless the
  client cannot enter any existing throwaway world.
- Prefer keyboard-first interaction and captured screenshots over coordinate-only assumptions.
- Verify directly visible states where possible:
  - Guide opens with configured `key.skyresources3.guide`, default `Y`.
  - Guide search and at least one link/action path visibly changes the screen.
  - JEI-present recipe action either opens/filters JEI or visibly fails safely without crashing.
  - At least one representative machine menu opens without client crash or obvious layout failure.
  - Island/team commands can be entered in a local world and produce readable localized feedback.
- If the client again cannot produce usable visual captures, record the exact startup mode, wait points, screenshots,
  process cleanup, and blocker; do not mark GUI smoke complete.
- Do not add gameplay behavior solely to make smoke testing easier.

## Acceptance Criteria

- [ ] Direct evidence closes as many `plans/client-manual-smoke-runbook.md` items as the run can actually prove.
- [ ] Remaining unproven GUI items stay unchecked and have a precise blocker explanation.
- [ ] No intentional Minecraft/Gradle client process remains running after the attempt.
- [ ] `plans/client-gui-smoke-execution-evidence.md`, `plans/final-migration-gap-audit.md`, and closeout checklists are
  updated to match only observed evidence.
- [ ] `git diff --check` and `git diff --cached --check` pass.
- [ ] `powershell -ExecutionPolicy Bypass -File "scripts/check-serena-java.ps1"` passes.
- [ ] `./gradlew.bat build` passes if source code changes are made.

## Definition of Done

- Work changes are committed with a Chinese message.
- Trellis task is archived.
- Session journal records whether the release GUI smoke completed, partially completed, or remains blocked.

## Technical Approach

Run a fresh client with evidence redirected under this task. Try the most reliable path first: visible/minimized launcher
or normal Gradle launch if hidden launch repeats white-window behavior. Use screenshots and logs after each state
transition. If interaction reaches an existing world, follow `plans/client-manual-smoke-runbook.md` in one pass. If the
client window remains white/unusable, stop cleanly and preserve the blocker evidence.

## Decision (ADR-lite)

**Context**: Logs and GameTests already cover startup, guide data, menu registration, and server behavior, but they cannot
prove user-facing client clicks. Previous automation failed for two different reasons: focus pollution and white client
captures.

**Decision**: Make one release-smoke attempt that optimizes for direct visible proof. Use direct evidence to close items,
and explicitly keep items open when screenshots cannot prove them.

**Consequences**: This can finish the final migration proof if the desktop client becomes usable. If not, the project gets
a precise release blocker without overstating completion.

## Out of Scope

- No custom GUI automation framework.
- No artificial gameplay/test-only shortcuts in production code.
- No branch changes or remote pushes.
- No optional integration implementation unless the smoke run reveals a concrete current regression.

## Technical Notes

- Source requirement: `plans/ask.md`.
- Runbook: `plans/client-manual-smoke-runbook.md`.
- Prior evidence: `plans/client-gui-smoke-execution-evidence.md`.
- Final audit: `plans/final-migration-gap-audit.md`.
