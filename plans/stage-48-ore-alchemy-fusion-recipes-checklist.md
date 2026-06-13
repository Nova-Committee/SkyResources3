# Stage 48 - Ore Alchemy Fusion Recipes

## Scope

- Add generated Fusion process recipes that create the stable vanilla ore alchemical dusts currently consumed by
  Condenser recipes.
- Preserve the old Fusion catalyst drain formula for ore alchemical dusts.
- Keep old ore-dictionary dust variants and modded ore expansion out of this slice.

## Legacy Behavior Notes

- Old automatic ore alchemical dust Fusion recipes used a per-ore component plus a rarity-tier dust.
- Old catalyst drain was `rarity * 0.0008F` for the base component recipe.
- Old secondary `dust<Name>` recipes used ore dictionary entries and a higher `rarity * 0.0021F` drain.

## Implementation Checklist

- [x] Read old `ModCrafting` ore alchemical dust Fusion generation and rarity helper.
- [x] Confirmed current `OreAlchemyDust` preserves legacy rarity metadata.
- [x] Added generated Fusion recipes for iron, gold, and copper ore alchemical dust.
- [x] Recorded the stable ore alchemical dust Fusion convention in recipe guidelines.
- [ ] Add dynamic/tagged modded ore alchemical dust recipes after the compatibility policy is explicit.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] Inspect generated Fusion JSON for iron, gold, and copper ore alchemical dust.
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
