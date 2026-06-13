# Stage 44 - Heat Provider

## Scope

- Port the old casing-installed Heat Provider machine item.
- Preserve the casing-installed machine workflow used by the old `TileCasing` and `ItemHeatProvider`.
- Keep the target project's current `MachineVariant` subset instead of adding optional legacy mod-metal variants.

## Legacy Behavior Notes

- Heat Providers are machine items installed into Machine Casings.
- They use the casing inventory slot for fuel when the variant accepts item fuel.
- While fuel remains, the provider exposes a heat-source value equal to the provider variant speed times `10`.
- Furnace-fuel variants use vanilla furnace burn time multiplied by provider/casing efficiency.
- Fixed-fuel variants consume the configured item and run for the variant fuel rate multiplied by provider/casing efficiency.
- Redstone disables the provider in the target migration so the guide promise that providers can be turned off remains true.

## Implementation Checklist

- [x] Read old `ItemHeatProvider`, `ItemMachine`, `TileCasing`, machine variants, HeatSources, and target casing code.
- [x] Add `HeatProviderItem` with variant data and tooltips.
- [x] Allow Machine Casings to install either combustion heaters or heat providers.
- [x] Tick heat providers in `MachineCasingBlockEntity` and expose their heat through `HeatSources`.
- [x] Adjust Machine Casing menu/screen display for provider state versus combustion multiblock state.
- [x] Register heat provider items for all current target `MachineVariant` values.
- [x] Add datagen recipes, manual item model definitions, texture, and language entries.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
