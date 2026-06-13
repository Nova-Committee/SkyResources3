# Stage 54 - Magmafied Stone Crystal Fluid Behavior

> Scope: migrate the legacy `MagmafiedStoneBlock` scheduled interaction with adjacent Crystal Fluid.

## Completed

- [x] Read old `MagmafiedStoneBlock` behavior.
- [x] Confirmed target `magmafied_stone` was still registered as a simple block.
- [x] Added `MagmafiedStoneBlock` using 1.21.11 scheduled block ticks.
- [x] Scheduled checks on placement and neighbor changes, then rescheduled after every tick.
- [x] Checked all six neighboring positions for `crystal_fluid`.
- [x] Preserved the old cobblestone drop chance of `30 / 800` per adjacent Crystal Fluid check.
- [x] Spawned short-lived cobblestone item entities at matching fluid positions.
- [x] Preserved lava-extinguish sound and smoke feedback on successful generation.
- [x] Switched `ModBlocks.MAGMAFIED_STONE` from simple block registration to the dedicated behavior class.

## Deferred

- [ ] Automated GameTests for block scheduled-tick behavior wait for reusable placement/tick helpers.
- [ ] Dirty and molten crystal fluid textures remain resource-only legacy leftovers unless a concrete old runtime registration is found.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`
