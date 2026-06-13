# Stage 25 - Infusion Process Runtime Checklist

> Scope: move infusion stone and Life Infuser recipes from hardcoded Java records to `skyresources3:process`.

## Completed

- [x] Read current `InfusionStoneItem` and `LifeInfuserBlockEntity` behavior.
- [x] Compared old 1.12.2 `ItemInfusionStone`, `LifeInfuserTile`, and base infusion recipe registrations.
- [x] Added `InfusionRecipes` as the shared runtime resolver for hand and machine infusion.
- [x] Switched `InfusionStoneItem` to resolve server-side process recipes and keep bonemeal fallback behavior.
- [x] Switched `LifeInfuserBlockEntity` to use the same process recipe resolver.
- [x] Added datagen for the migrated vanilla and SkyResources infusion recipes.
- [x] Recorded the infusion input convention in `.trellis/spec/backend/recipe-guidelines.md`.
- [x] Added GameTests for both hand infusion stone runtime and Life Infuser machine runtime.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`

## Deferred

- [x] JEI infusion display restored by the recipe display integration slice; REI/EMI remain intentionally unimplemented.
- [x] Integrated Dynamics infusion recipes restored after the per-mod 1.21.11 availability check.
