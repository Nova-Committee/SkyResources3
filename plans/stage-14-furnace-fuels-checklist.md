# Stage 14 - Furnace Fuel DataMap

> 状态：迁移旧 `fuelTime` 行为到 NeoForge furnace fuel data map。

- [x] Add static furnace burn times for `alchemical_coal` = 3000 ticks.
- [x] Add static furnace burn times for `coal_infused_block` = 30000 ticks.
- [x] Add static furnace burn times for `compressed_coal_block` = 128000 ticks.
- [x] Use NeoForge `NeoForgeDataMaps.FURNACE_FUELS` instead of a runtime furnace event for static fuel values.
- [x] Generate and commit the resulting data map JSON.
- [x] Run `runData`, `build`, `runGameTestServer`, and whitespace checks.
