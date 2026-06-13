# Final Validation Evidence Refresh

## Scope

- Refresh final validation evidence after the combustion automation regression closeout.
- Keep manual GUI click-through separate from headless proof.
- Avoid changing gameplay or optional integration scope.

## Acceptance Checklist

- [x] `./gradlew.bat runData` passes with generated resources unchanged (`written: 0`).
- [x] `./gradlew.bat runGameTestServer` passes with the current GameTest suite.
- [x] `./gradlew.bat build` passes.
- [x] Legacy Forge networking/API/package scans have no runtime Java/resource hits.
- [x] Runtime/generated resources have no old `skyresources:` namespace hits.
- [x] `git diff --check` and `git diff --cached --check` pass after documentation edits are staged.
- [x] `scripts/check-serena-java.ps1` passes and resolves the project as Java/LSP.

## Manual Boundary

- `runClient` startup evidence exists in `plans/client-smoke-resource-license-closeout-checklist.md`.
- Interactive GUI click-through is still required before a release tag for guide actions, JEI behavior, representative machine menu visuals, and island/team flows.
- Headless Gradle gates and GameTests are valid migration evidence, but they do not prove visual UI clicks.
