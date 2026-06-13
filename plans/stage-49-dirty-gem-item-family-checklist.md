# Stage 49 - Dirty Gem Item Family

## Scope

- Port the old `dirtyGem` metadata item family into standalone 1.21.11 item ids.
- Preserve the old gem list order and metadata that future Rock Cleaner and cauldron-clean recipe slices need.
- Keep built-in `cauldronclean` recipes, clean gem outputs, and dynamic tag integration out of this slice.

## Legacy Behavior Notes

- Old `DirtyGemItem` used one metadata item and derived variant names from `ModItems.gemList`.
- Old `GemRegisterInfo` stored gem name, tint color, rarity, parent block, and optional ore override.
- Old language names used `Dirty <Gem Name>` and a few special labels such as `Dirty Dark Gem`.

## Implementation Checklist

- [x] Read old `DirtyGemItem`, old `GemRegisterInfo`, and old `ModItems.gemList` initialization.
- [x] Add a typed `DirtyGem` enum with old ids, display names, color, rarity, parent block id, and ore override.
- [x] Register all 44 Dirty Gem items as standalone registry ids.
- [x] Add all Dirty Gem items to the SkyResources3 creative tab.
- [x] Add item definitions, a shared item model, shared texture, and language entries.
- [ ] Add variant tinting or per-gem textures after the client color strategy is selected.
- [x] Add stable vanilla `cauldronclean` recipes in a focused follow-up.
- [ ] Add dynamic/tagged modded `cauldronclean` outputs after compatibility policy is explicit.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
