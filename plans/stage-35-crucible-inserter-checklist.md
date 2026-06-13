# Stage 35 - Crucible Inserter

## Scope
- Migrate the legacy Crucible Inserter block and single-slot inventory.
- Restore the Crucible behavior that consumes input from an inserter directly above it.
- Add the modern menu/screen wiring for the one-slot GUI.
- Expose item insertion through NeoForge Transfer API while preventing external extraction.

## Implemented
- Added `CrucibleInserterBlock`, `CrucibleInserterBlockEntity`, `CrucibleInserterMenu`, and `CrucibleInserterScreen`.
- Registered block, item, block entity type, item capability, menu type, screen, creative-tab entry, loot, tags, lang, model, and blockstate.
- Added the legacy iron/dropper crafting recipe through datagen.
- Updated `CrucibleBlockEntity` to absorb one valid input from a `crucible_inserter` above it before melting.

## Verification
- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`

## Deferred
- Dedicated JEI/guide documentation remains part of later integration and guide slices.
