# Stage 29 - Fusion Table Runtime Checklist

> Scope: migrate the old Alchemical Fusion Table server-side runtime onto the 1.21.11 NeoForge block entity shell. Menu, screen, and packet UX were completed by follow-up slices and are tracked here to keep the Fusion Table status current.

## Completed

- [x] Read old `TileAlchemyFusionTable`, `ContainerFusionTable`, `GuiFusionTable`, `FusionCatalysts`, and `DumpMessage` behavior.
- [x] Kept the legacy slot layout: catalyst slot `0`, input/filter slots `1..9`, output slot `10`.
- [x] Added server ticking to `FusionTableBlock`.
- [x] Added inventory, filter, active output, progress, catalyst-left, catalyst-yield, and fractional-yield persistence.
- [x] Consumes `ProcessRecipes.FUSION` recipes from data packs instead of reintroducing old static recipe lists.
- [x] Preserved old default catalyst yield values:
  - `primus_alchemical_dust` = `0.75`
  - `secundus_alchemical_dust` = `1.75`
  - `tertius_alchemical_dust` = `4.50`
  - `quartus_alchemical_dust` = `32.00`
- [x] Registered a NeoForge `Capabilities.Item.BLOCK` handler for automation.
- [x] Drops real inventory contents when the block is broken; filter ghost stacks and in-progress virtual output are not dropped.
- [x] Follow-up: Fusion Table menu/container implementation is complete.
- [x] Follow-up: Fusion Table client screen and migrated GUI texture wiring are complete.
- [x] Follow-up: Network packet for the old dump button behavior is complete.
- [x] Follow-up: Player-facing filter editing is implemented through the menu input slots.
- [x] Follow-up: Crafting recipe was migrated after adding `stone_alchemy_component` for the old stone `alchComponent`.

## Deferred

- [x] JEI display integration is complete through the general process recipe category in Stage 60; EMI/REI duplicate viewer and scripting integrations remain optional/deferred.

## Verification

- [x] `./gradlew.bat compileJava`
- [x] `./gradlew.bat runData`
- [x] `./gradlew.bat build`
- [x] `./gradlew.bat runGameTestServer`
- [x] `git diff --check`
- [x] `git diff --cached --check`
