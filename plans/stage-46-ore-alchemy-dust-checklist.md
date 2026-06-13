# Stage 46 - Ore Alchemy Dust Items

## Scope

- Port the old `oreAlchDust` meta item family into standalone 1.21.11 item ids.
- Preserve the old ore list order and metadata that future Condenser/fusion/cleaning recipe slices need.
- Keep Condenser runtime recipes and Dirty Gem migration out of this slice.

## Legacy Behavior Notes

- The old item used metadata values for 25 ore-specific variants.
- The old renderer tinted the shared `orealchdust.png` texture by variant color.
- Old condenser and fusion recipe generation used ore name, rarity, parent block, and automatic flags.

## Implementation Checklist

- [x] Read old `ItemOreAlchDust`, old rendering references, and current component item registration.
- [x] Add a typed `OreAlchemyDust` enum with old ids, display names, rarity, parent block id, and automatic flags.
- [x] Register all 25 ore alchemical dust items as standalone registry ids.
- [x] Add all ore alchemical dust items to the SkyResources3 creative tab.
- [x] Add item definitions, a shared item model, shared texture, and language entries.
- [ ] Add variant tinting or per-ore textures after the client color strategy is selected.
- [x] Add Condenser recipes that consume the stable vanilla iron, gold, and copper dusts.
- [x] Add Fusion recipes that create the stable vanilla iron, gold, and copper dusts.
- [ ] Add cleaning recipes only after a stable output item/tag policy exists; the old cauldron-clean recipes emitted ore-dictionary `dust<Name>` outputs that do not have vanilla-backed 1.21.11 items in this port.
- [ ] Add dynamic/tagged modded ore recipes once the compatibility policy is explicit.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
