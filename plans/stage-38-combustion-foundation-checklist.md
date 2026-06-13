# Stage 38 - Combustion Machine Foundation Checklist

## Completed

- Added supported machine casing variants: wooden, stone, iron, nether brick, end stone, dark matter, and light matter.
- Added matching combustion heater items with legacy heat capacity, speed, efficiency, and fuel behavior.
- Added a machine casing block entity with a NeoForge transfer fuel slot.
- Added manual redstone-pulse combustion using migrated `skyresources3:process` combustion recipes.
- Added machine casing menu and screen for fuel, heat, heating rate, and multiblock status.
- Added recipes, loot, mining tags, creative tab entries, translations, models, and item definitions for the new casing/heater foundation.

## Deferred

- External metal casing variants from the old mod remain deferred until their materials exist in this port.
- Combustion Collector output routing is deferred to the next combustion slice.
- Combustion Controller filtered automation is deferred to the next combustion slice.
- RF/fluid heater variants are deferred until the corresponding energy/fluid contracts are migrated.

## Verification

- `./gradlew.bat compileJava`
- `./gradlew.bat runData`
- `./gradlew.bat build`
- `./gradlew.bat runGameTestServer`
- `git diff --check`
