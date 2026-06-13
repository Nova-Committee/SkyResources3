# Stage 31 - Fluid Dropper

## Scope
- Migrate the 1.12.2 Fluid Dropper as a standalone block entity.
- Preserve the early-game recipe shape and 1000 mB default capacity.
- Use NeoForge Transfer API fluid capabilities, not legacy Forge `IFluidHandler`.

## Implemented
- Registered `fluid_dropper` block, block item, block entity type, creative tab entry, and block fluid capability.
- Added a one-tank block entity that pulls fluid from the top and horizontal neighbors unless powered by redstone.
- Places a bucket of stored fluid below the block when the space is empty, then clears the tank like the legacy machine.
- Added blockstate, model, item model, texture, loot, mineable tag, language entry, and shaped cobblestone recipe.

## Verification
- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`

## Deferred
- GUI and screen work is intentionally not included; the legacy Fluid Dropper had no GUI.
- Fluid-producing machines such as Crucible and Aqueous Concentrator remain separate migration slices.
