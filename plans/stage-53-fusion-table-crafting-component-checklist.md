# Stage 53 - Fusion Table Crafting Component

> Scope: migrate the old stone `alchComponent` mapping needed by the Alchemical Fusion Table crafting recipe.

## Completed

- [x] Confirmed old `ModItems.alchComponent` meta 1 maps to `MachineVariants.STONE`.
- [x] Confirmed the old stone alchemy component recipe uses stone around `alchemyComponent` meta 2.
- [x] Mapped `alchemyComponent` meta 2 to the existing `primus_alchemical_dust`.
- [x] Added `stone_alchemy_component` as a standalone 1.21.11 item.
- [x] Added item definition, item model, migrated texture, language entry, and creative-tab entry.
- [x] Generated the stone alchemy component crafting recipe.
- [x] Generated the Alchemical Fusion Table crafting recipe.
- [x] Updated Stage 28 and Stage 29 Fusion Table checklists so crafting recipe is no longer deferred.

## Deferred

- [ ] Full old `alchComponent` and `heatComponent` machine component families remain deferred until a concrete recipe or target machine requires each variant.
- [ ] External old machine variants remain deferred until the target mod/tag integration policy is explicit.
- [x] JEI recipe viewer integration for Fusion/process recipe categories was completed in Stage 60.
- [ ] EMI and scripting integrations remain deferred until a target version/API policy is selected.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`
