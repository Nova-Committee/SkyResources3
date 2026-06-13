# Stage 45 - Condenser

## Scope

- Port the old casing-installed Condenser machine item entry point.
- Reuse the existing Machine Casing installed-machine workflow instead of adding a new block.
- Keep Condenser runtime recipes deferred until the ore alchemical dust matrix and a fluid-aware condenser recipe contract are migrated.

## Legacy Behavior Notes

- Condensers are machine items installed into Machine Casings.
- The old machine uses the casing inventory slot as a catalyst slot.
- It reads the block or source fluid above the casing, consumes catalyst durability, clears the source on completion, and ejects output downward.
- Old automatic recipes depend on ore-specific alchemical dust variants and crystal fluid inputs.

## Implementation Checklist

- [x] Read old `ItemCondenser`, old condenser recipe registration, and current Machine Casing implementation.
- [x] Add `CondenserItem` with variant data and speed/efficiency tooltips.
- [x] Register Condenser items for all current target `MachineVariant` values.
- [x] Allow Machine Casings to install Condensers without changing the existing saved installed-machine key.
- [x] Expose an installed-machine mode to the menu so the screen does not label Condensers as Heat Providers.
- [x] Add datagen recipes, manual item model definitions, texture, and language entries.
- [ ] Add condenser runtime recipes after ore alchemical dusts and a fluid-aware recipe contract are available.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
