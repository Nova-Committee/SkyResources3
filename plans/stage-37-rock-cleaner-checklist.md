# Stage 37 - Rock Cleaner

## Scope

- Migrate the 1.12.2 Rock Cleaner block, block entity, menu, and screen.
- Preserve the old machine contract: 4 slots, 100000 FE capacity, 2000 FE input, no energy output, 4000 mB water capacity, 250 mB water per operation, redstone pause, and 2x capped cauldron-clean yield.
- Expose item, fluid, and energy automation through NeoForge transfer APIs.
- Share the `cauldronclean` process with manual water-cauldron cleaning.

## Completed

- [x] Registered `rock_cleaner` block, block item, block entity, menu, screen, creative-tab entry, capabilities, language keys, loot, pickaxe tag, and crafting recipe.
- [x] Added `ProcessRecipes.CAULDRON_CLEAN` for data-pack recipes.
- [x] Implemented water-only fluid storage with external extraction disabled.
- [x] Implemented water bucket insertion through `FluidUtil.interactWithFluidHandler`.
- [x] Implemented machine progress, energy use, water use, output buffering, and capped 2x output chance.
- [x] Implemented manual water-cauldron cleaning from the same `cauldronclean` recipes.
- [x] Copied old Rock Cleaner block and GUI textures to snake_case resource paths.
- [x] Follow-up: Dirty Gem item family is now available as standalone 1.21.11 item ids.

## Deferred

- [ ] Add built-in `cauldronclean` generated recipes after clean gem output/tag policy and dynamic ore/alchemical dust
  outputs are explicit. The old recipes depend on those outputs, so this stage intentionally does not invent replacements.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
