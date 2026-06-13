# Stage 43 - Wildlife Attractor

## Scope

- Port the old Wildlife Attractor machine from the 1.12.2 Forge project.
- Preserve the legacy one-slot Plant Matter inventory, water tank, energy buffer, redstone-disable behavior, and random animal attraction loop.
- Use NeoForge 1.21.11 transfer APIs for item, fluid, and energy automation.

## Legacy Behavior Notes

- Inventory: one slot at GUI position `(80,59)`, accepts Plant Matter.
- Tank: water-only, default capacity `4000` mB.
- Energy: default capacity `100000` FE, insert rate `2000` FE/t, no extraction.
- Runtime:
  - Redstone signal pauses the machine.
  - When Plant Matter timer is empty, one Plant Matter item refills it to `wildlifeAttractorMatterTime`.
  - While active, each tick consumes `wildlifeAttractorPowerUsage` FE, `wildlifeAttractorWaterUsage` mB water, and one Plant Matter timer tick.
  - Each active tick has a `1/600` chance to spawn one configured animal above the block.
- Defaults:
  - Power usage: `40` FE/t.
  - Water usage: `20` mB/t.
  - Plant Matter time: `320` ticks.
  - Animals: sheep, cow, chicken, pig, rabbit, squid, horse, parrot.

## Implementation Checklist

- [x] Read old `TileWildlifeAttractor`, block, GUI/container, config, recipe, language, and resources.
- [x] Add `WildlifeAttractorBlock`, `WildlifeAttractorBlockEntity`, `WildlifeAttractorMenu`, and `WildlifeAttractorScreen`.
- [x] Register block, block item, block entity type, menu type, item/fluid/energy capabilities, client screen, and creative-tab entry.
- [x] Add config values for power usage, water usage, matter time, water capacity, and animal ids.
- [x] Add manual blockstate/model/item JSON resources and copy required textures.
- [x] Add datagen recipe, loot table, and mining tag.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
