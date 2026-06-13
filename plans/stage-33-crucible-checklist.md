# Stage 33 - Crucible

## Scope
- Migrate the legacy Crucible block and block entity.
- Support dropped item ingestion and fluid tank extraction via NeoForge Transfer API.
- Add a dedicated data-driven Crucible recipe type for fluid outputs.
- Register the first legacy recipe: `crystal_shard` to 1000 mB `crystal_fluid`.

## Implemented
- Added `CrucibleRecipe` and `CrucibleRecipes`.
- Added a small `HeatSources` helper with legacy vanilla heat source values.
- Added `CrucibleBlock` shape, ticker, fluid-container interaction, and comparator output.
- Added `CrucibleBlockEntity` with persisted pending input and one fluid tank.
- Registered block, item, block entity, fluid capability, recipe type, serializer, loot, tags, assets, and datagen.

## Verification
- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`

## Deferred
- Crucible Inserter block/menu automation is a separate technology-machine slice.
- Blaze Powder Block and the legacy lava crucible recipe are deferred until that block exists.
- In-world fluid rendering inside the crucible is deferred unless a modern renderer is introduced.
