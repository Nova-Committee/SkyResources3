# Stage 28 - Fusion Table Shell Checklist

> Scope: register the old Alchemical Fusion Table as a placeable 1.21.11 NeoForge block with block entity, resources, loot, and creative-tab visibility. Runtime crafting and GUI are deferred to focused follow-up slices.

## Completed

- [x] Read old `BlockAlchemyFusionTable` registration and activation behavior.
- [x] Confirmed old `fusionTable` maps to new registry id `fusion_table`.
- [x] Added `FusionTableBlock` and `FusionTableBlockEntity` shell classes.
- [x] Registered `ModBlocks.FUSION_TABLE`, `ModItems.FUSION_TABLE`, and `ModBlockEntityTypes.FUSION_TABLE`.
- [x] Added the block item to the SkyResources3 creative tab.
- [x] Added loot and mining tag coverage.
- [x] Added blockstate, block model, item definition, lang entry, and migrated top/side textures.
- [x] Follow-up: Crafting recipe was migrated after adding the old stone `alchComponent` mapping as `stone_alchemy_component`.
- [x] Follow-up: Fusion Table runtime consumes `ProcessRecipes.FUSION` and migrated catalyst values.
- [x] Follow-up: Fusion Table menu, screen, dump network packet, and player-facing filter editing are implemented.
- [x] Follow-up: Automation/filter behavior is implemented through the block item capability and menu filter slots.

## Deferred

- [x] JEI recipe viewer integration for Fusion/process recipe categories was completed in Stage 60.
- [ ] EMI and scripting integrations remain deferred until a target version/API policy is selected.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`
