# Stage 50 - Stable Dirty Gem Cleaning Recipes

## Scope

- Generate `cauldronclean` process recipes for Dirty Gem variants that have unambiguous Minecraft vanilla outputs.
- Preserve the old Dirty Gem cleaning parameter `1.0F`.
- Keep modded gem outputs, tag priority resolution, ore alchemical dust cleaning, and JEI/EMI display out of this slice.

## Legacy Behavior Notes

- Old `ModCrafting` iterated `ModItems.gemList`, built `gem<Name>` or `oreOverride`, and emitted a `cauldronclean`
  recipe when the ore dictionary contained that output.
- Old Dirty Gem cleaning used chance parameter `1F` and selected the preferred ore dictionary output.
- Target 1.21.11 data generation can only safely emit concrete vanilla outputs until the compatibility policy is
  explicit.

## Implementation Checklist

- [x] Read old Dirty Gem `cauldronclean` generation in `ModCrafting`.
- [x] Generate vanilla-backed Dirty Gem cleaning recipes for emerald, diamond, quartz, and lapis.
- [x] Record the stable Dirty Gem cleaning convention in recipe guidelines.
- [ ] Add modded/tagged Dirty Gem cleaning recipes after target compatibility policy is explicit.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] Inspect generated `process/cauldronclean` JSON files.
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
