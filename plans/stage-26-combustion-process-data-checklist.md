# Stage 26 - Combustion Process Data Checklist

> Scope: expand the low-risk vanilla/SkyResources combustion process data without implementing combustion machines yet.

## Completed

- [x] Confirmed the current 1.21 code has no combustion machine runtime consumer yet.
- [x] Read old base `combustionRecipes` registrations from `ModCrafting`.
- [x] Migrated directly mappable vanilla and SkyResources combustion recipes to datagen.
- [x] Kept component/meta mappings limited to already split 1.21 items and blocks.
- [x] Left potion-component and external ore-dictionary priority recipes for focused follow-ups.

## Migrated Recipes

- `coal`
- `blaze_powder`
- `gunpowder`
- `diamond`
- `red_sand`
- `dry_cactus`
- `redstone`
- `wheat_seeds`
- `dirt`
- `slime_ball`
- `poisonous_potato`
- `radioactive_mix`
- `prismarine_shard`
- `prismarine_crystals`
- `netherrack`
- `dark_matter`
- `light_matter`
- `glowstone_dust`
- `end_stone`
- `primus_alchemical_dust`

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`

## Deferred

- [ ] Water bottle -> snowball combustion waits for a component-aware process ingredient convention.
- [ ] Dynamic old ore-dictionary priority variants wait for tag and integration policy decisions.
- [x] Combustion controller/collector runtime was completed by the combustion automation slice and is covered by
  `MachineRuntimeGameTests.combustionControllerUsesFilterPriority` and
  `MachineRuntimeGameTests.combustionCollectorDropsOverflow`.
