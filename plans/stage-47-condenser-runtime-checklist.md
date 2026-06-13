# Stage 47 - Condenser Runtime Recipes

## Scope

- Add a first-class `skyresources3:condenser` recipe type with fluid/block world-source matching.
- Restore Condenser execution inside Machine Casings.
- Generate stable vanilla starter recipes for iron, gold, and copper ore alchemical dusts.

## Legacy Behavior Notes

- The old Condenser used the casing inventory slot as a catalyst slot.
- The block above the casing was interpreted as either a source fluid or a block input.
- Completion cleared the source and ejected the output downward.
- Catalyst durability drained across multiple operations through a stored active catalyst item.

## Implementation Checklist

- [x] Add `CondenserRecipe`, `CondenserRecipeInput`, and `CondenserRecipes`.
- [x] Register `skyresources3:condenser` recipe type and serializer.
- [x] Execute Condenser progress from `MachineCasingBlockEntity`.
- [x] Validate the casing slot against condenser catalysts when a Condenser is installed.
- [x] Route condenser outputs to a below item handler or drop below when no handler exists.
- [x] Add menu/screen data for Condenser progress.
- [x] Generate iron, gold, and copper fluid/block starter recipes.

## Follow-up

- [x] Add fusion recipes that create stable vanilla iron, gold, and copper ore alchemical dusts.
- [ ] Add dynamic/tagged modded ore integration once the compatibility policy is explicit.
- [x] Add focused GameTests for successful Condenser output and blocked-output retry behavior.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
