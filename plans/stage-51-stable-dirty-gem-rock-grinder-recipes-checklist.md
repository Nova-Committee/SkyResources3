# Stage 51 - Stable Dirty Gem Rock Grinder Recipes

## Scope

- Generate `rockgrinder` process recipes for Dirty Gem variants that have unambiguous Minecraft vanilla output chains.
- Preserve old `GemRegisterInfo.rarity` as the Rock Grinder chance parameter.
- Keep modded gem source recipes, tag priority resolution, tinting, and JEI/EMI display out of this slice.

## Legacy Behavior Notes

- Old `ModCrafting` iterated `ModItems.gemList`, checked the matching ore dictionary output, and emitted a
  `rockgrinder` recipe from `GemRegisterInfo.parentBlock` to the corresponding `dirtyGem` metadata.
- Old stable vanilla mappings used `stone` for emerald, diamond, and lapis, and `netherrack` for quartz.

## Implementation Checklist

- [x] Read old Dirty Gem `rockgrinder` generation in `ModCrafting`.
- [x] Generate vanilla-backed Dirty Gem Rock Grinder recipes for emerald, diamond, quartz, and lapis.
- [x] Record the stable Dirty Gem Rock Grinder convention in recipe guidelines.
- [ ] Add modded/tagged Dirty Gem source recipes after target compatibility policy is explicit.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] Inspect generated `process/rockgrinder/*_dirty_gem.json` files.
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
