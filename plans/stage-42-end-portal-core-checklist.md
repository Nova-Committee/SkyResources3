# Stage 42 - End Portal Core

## Scope

- Port the old End Portal Core machine from the 1.12.2 Forge project.
- Include the Silverfish Disruptor block because the improved End Portal Core multiblock depends on it.
- Preserve the legacy one-slot Ender Eye inventory, basic/improved multiblock checks, redstone-pulse activation, silverfish difficulty config, and End teleport behavior.

## Implementation Checklist

- [x] Read old `TileEndPortalCore`, `BlockEndPortalCore`, `GuiEndPortalCore`, `ContainerEndPortalCore`, `BlockSilverfishDisruptor`, recipes, and resource files.
- [x] Add `EndPortalCoreBlock`, `EndPortalCoreBlockEntity`, `EndPortalCoreMenu`, and `EndPortalCoreScreen`.
- [x] Add `SilverfishDisruptorBlock`.
- [x] Register blocks, block items, block entity type, menu type, item capability, client screen, and creative-tab entries.
- [x] Add config enum `endPortalMode` with `NORMAL`, `EASY`, and `WUSS`.
- [x] Add manual blockstate/model/item JSON resources and copy required textures.
- [x] Add datagen recipes, loot tables, and mining tags.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
