# Stage 32 - Crystal Fluid Registry

## Scope
- Migrate the legacy `srCrystalFluid` registration foundation.
- Provide one source fluid, one flowing fluid, a liquid block, and a bucket item.
- Keep Crucible and Condenser runtime behavior for later slices.

## Implemented
- Added `ModFluidTypes` and `ModFluids` using NeoForge `FluidType` and `BaseFlowingFluid`.
- Registered `crystal_fluid`, `flowing_crystal_fluid`, `crystal_fluid` liquid block, and `crystal_fluid_bucket`.
- Added client fluid texture extensions for still and flowing crystal fluid textures.
- Added language entries, blockstate/model resources, bucket model resources, and migrated animated textures.
- Added no-drop loot handling for the liquid block.

## Verification
- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`

## Deferred
- Crucible recipes and runtime melting remain a separate machine migration slice.
- Condenser recipes that consume crystal fluid remain deferred until its runtime machine exists.
- A bespoke crystal fluid bucket texture can replace the temporary vanilla bucket model later.
