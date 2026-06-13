# Stage 7 - Water Extractor

- [x] Read legacy `ItemWaterExtractor` behavior and crafting recipe.
- [x] Add a 1.21 data component for persisted water content.
- [x] Register a NeoForge fluid item capability for automation and future machines.
- [x] Migrate long-press extraction from snow and tree leaves.
- [x] Migrate water source pickup with 1000 mB transfer.
- [x] Migrate dirt + 200 mB water insertion into clay.
- [x] Migrate shift-right-click water placement.
- [x] Add tooltip water amount, creative tab entry, language, item definition, models, textures, and shaped plank recipe.
- [x] Run datagen.
- [x] Run build and resource reference validation.

Deferred:
- [x] `dryCactus` extract/insert recipe waits for the dry cactus block migration.
- [ ] Standalone JEI Water Extractor category remains deferred until a first-class water-extraction display contract is
  selected. Stage 60 covers the migrated process/crucible/condenser/heat-source JEI categories, but does not add a
  dedicated Water Extractor category.
