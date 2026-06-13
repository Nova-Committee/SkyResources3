# Stage 10 - Rock Grinder Checklist

- [x] Read old `ItemRockGrinder` and old rock grinder process recipes.
- [x] Map old meta outputs to current standalone items: crushed stone, crushed netherrack, and sawdust.
- [x] Add shared fortune-scaled drop helper for hand processing tools.
- [x] Add `RockGrinderItem` with migrated durability, attack, speed, and enchantability values.
- [x] Add break-event behavior for old rock grinder block-processing recipes.
- [x] Register all three rock grinders with the new behavior while preserving resources and crafting recipes.
- [x] Run build and GameTest server; datagen only if generated data changes.
- [x] Commit this migration slice with a Chinese message.
- [x] Follow-up: runtime drops now come from `skyresources3:process` rock grinder recipes instead of a hardcoded output table.
- [x] Follow-up: stable vanilla Dirty Gem `rockgrinder` recipes are now generated.
