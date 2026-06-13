# Combustion Automation Regression Closeout

## Scope

- Add focused server-side regression coverage for Smart Combustion Controller priority/filter behavior.
- Add focused server-side regression coverage for Combustion Collector overflow and drop fallback.
- Keep runtime behavior unchanged unless tests expose a concrete bug.

## Acceptance Checklist

- [x] Controller priority GameTest registers through `ModGameTests`.
- [x] Collector overflow GameTest registers through `ModGameTests`.
- [x] `./gradlew.bat compileJava` passes.
- [x] `./gradlew.bat runGameTestServer` passes.
- [x] `./gradlew.bat runData` passes.
- [x] `./gradlew.bat build` passes.
- [x] `git diff --check` and `git diff --cached --check` pass.
- [x] `scripts/check-serena-java.ps1` passes.

## Evidence Notes

- `MachineRuntimeGameTests.combustionControllerUsesFilterPriority` builds an iron combustion rig with two possible chamber recipes and verifies the first matching controller filter wins.
- `MachineRuntimeGameTests.combustionCollectorDropsOverflow` blocks collector capacity, crafts red sand, and verifies remaining output drops in the chamber instead of being lost.
