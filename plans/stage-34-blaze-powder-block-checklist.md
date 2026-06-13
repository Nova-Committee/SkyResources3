# Stage 34 - Blaze Powder Block

## Scope
- Migrate the legacy Blaze Powder Block.
- Preserve its scheduled heat-source behavior: placed above heat, it can turn into source lava.
- Add the legacy 2x2 blaze powder crafting recipe.
- Add the legacy Crucible lava recipe now that the block exists.

## Implemented
- Added `BlazePowderBlock` with scheduled server ticks and legacy heat probability.
- Registered block, item, creative-tab entry, loot, block tags, lang, model, blockstate, and texture.
- Added `blaze_powder_block` crafting data through datagen.
- Added `crucible/lava` recipe outputting 1000 mB lava.

## Verification
- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`

## Deferred
- JEI/guide descriptions are deferred until integration and guide slices.
