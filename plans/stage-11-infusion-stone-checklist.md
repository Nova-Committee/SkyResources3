# Stage 11 - Infusion Stone Checklist

- [x] Read old `ItemInfusionStone` behavior and old infusion process recipes.
- [x] Map old meta inputs and outputs to current 1.21 standalone items and blocks.
- [x] Add shared instant-bonemeal growth helper for plant matter and infusion stones.
- [x] Add `InfusionStoneItem` with health-cost infusion and bonemeal fallback behavior.
- [x] Register all three infusion stones with migrated durability and no-repair behavior.
- [x] Add config and language entries for infusion stone behavior and failure messages.
- [x] Run build and GameTest server; datagen only if generated data changes.
- [x] Commit this migration slice with a Chinese message.

Deferred:
- [ ] Acacia sapling infusion from cactus fruit needle waits for the old cactus fruit needle block migration.
- [ ] JEI infusion recipe category waits for the recipe system/UI migration batch.
