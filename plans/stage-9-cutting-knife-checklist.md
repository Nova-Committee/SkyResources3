# Stage 9 - Cutting Knife Checklist

- [x] Read old `ItemKnife` and old knife process recipes.
- [x] Confirm 1.21.11 `Item#mineBlock`, durability, enchantable, and attribute APIs from local sources.
- [x] Add a focused `CuttingKnifeItem` for old knife block-processing behavior.
- [x] Register all four cutting knives with migrated durability, attack, speed, and enchantability values.
- [x] Preserve existing resources and crafting recipes.
- [x] Run build and GameTest server; no generated data changed, so datagen/resource validation was not needed.
- [x] Commit this migration slice with a Chinese message.

Note: A dedicated GameTest class was not added because the current Gradle source sets do not expose the GameTest API to
`compileJava`; expanding that test setup is left for a focused testing-infrastructure pass.
