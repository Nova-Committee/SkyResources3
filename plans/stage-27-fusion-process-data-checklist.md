# Stage 27 - Fusion Process Data Checklist

> Scope: migrate the directly mappable old fusion process recipes to generated `skyresources3:process` data without implementing the fusion table runtime yet.

## Completed

- [x] Read old `fusionRecipes` registrations from `ModCrafting`.
- [x] Verified old `alchemyComponent`, `baseComponent`, and `techComponent` metadata mappings against already split 1.21 items.
- [x] Confirmed old fusion `parameter` is the per-progress-tick catalyst drain; user-facing full-craft percentage is `parameter * 10000`.
- [x] Generated fixed vanilla/SkyResources fusion recipes from `SkyResources3RecipeProvider`.
- [x] Recorded fusion parameter semantics and dynamic ore-dictionary deferral in `.trellis/spec/backend/recipe-guidelines.md`.
- [x] Follow-up: Fusion Table runtime now consumes `ProcessRecipes.FUSION` and the old catalyst yield values.
- [x] Follow-up: Fusion Table menu/screen/network state is now implemented.

## Migrated Recipes

- `secundus_alchemical_dust`
- `tertius_alchemical_dust`
- `quartus_alchemical_dust`
- `alchemical_coal`
- `alchemical_iron_ingot`
- `alchemical_gold_ingot`
- `alchemical_diamond`
- `dark_oak_sapling`
- `magmafied_stone`
- `alchemical_glass`
- `petrified_wood`
- `dirt_from_soul_sand`
- `crystal_shard_from_glass`
- `crystal_shard_from_alchemical_glass`
- `dirt_from_plant_matter`
- `iron_ore_alchemical_dust`
- `gold_ore_alchemical_dust`
- `copper_ore_alchemical_dust`

## Deferred

- [ ] Dynamic ore-dictionary ore dust recipes beyond the stable vanilla iron, gold, and copper set wait for the target
  mod/tag integration policy.
- [ ] CraftTweaker-style catalyst/recipe mutation is deferred until integration dependencies and scripting support are selected.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`
