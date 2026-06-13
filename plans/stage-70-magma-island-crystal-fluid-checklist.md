# Stage 70 - Magma Island Crystal Fluid Checklist

> Scope: complete the legacy VIC Crystal Fluid placement inside the built-in magma island template.

## Goals

- Restore the old SkyResources/VIC magma starter Crystal Fluid source.
- Keep the existing current `IslandTemplate.MAGMA` shape and only add the missing fluid placement.
- Validate the behavior through the command path that players use.

## Source Evidence

- Old source: `D:/workspace/minecraft/mods/4Github/SkyResources/src/main/java/com/bartz24/skyresources/plugin/vic/VICPlugin.java`.
- Old relative position: `FluidUtil.tryPlaceFluid(..., pos.west().south(), ...)`.
- Target mapping: current `IslandTemplate.MAGMA` uses `center` for the old `pos`, so the migrated position is `center.west().south()`.

## Migrated Behavior

- `IslandTemplate.MAGMA` now places `ModBlocks.CRYSTAL_FLUID` at `center.west().south()`.
- Existing petrified wood, soul sand, nether wart, and magmafied stone placements remain unchanged.
- `IslandCommandGameTests.magmaIslandPlacesCrystalFluid` creates a magma island through `/island create magma` and asserts the Crystal Fluid block.
- `island-command-guidelines.md` records the magma template Crystal Fluid contract.
- Stage 21 no longer lists Crystal Fluid placement as a pending magma island item.

## Deferred Migration

- Garden of Glass still uses a vanilla placeholder until a compatible Botania/Garden of Glass integration is available.
- Old VIC biome mutation and bottom-block configuration remain out of scope.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runGameTestServer`
- [x] `./gradlew.bat build`
- [x] `git diff --check`
- [x] `git diff --cached --check`
