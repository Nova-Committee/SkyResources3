# 岩浆岛放置液态水晶

## Goal

补齐 Stage 21 magma island 模板剩余的 Crystal Fluid 放置行为：旧 `VICPlugin` 在 magma starter 的 `pos.west().south()` 放置一桶 `srCrystalFluid`，当前 `IslandTemplate.MAGMA` 已有同一套 petrified wood / soul sand / magmafied stone 布局，但缺少该流体源。

## What I already know

* 旧源码 `D:/workspace/minecraft/mods/4Github/SkyResources/src/main/java/com/bartz24/skyresources/plugin/vic/VICPlugin.java` 使用 `FluidUtil.tryPlaceFluid` 在 `pos.west().south()` 放置 `ModFluids.crystalFluid`。
* 当前 `IslandTemplate.MAGMA` 使用 `center` 对应旧源码的 `pos`，并已放置旧源码中的 soul sand、petrified wood、nether wart、magmafied stone。
* `ModBlocks.CRYSTAL_FLUID` / `ModFluids.CRYSTAL_FLUID` 已完成注册，现有 GameTests 也用 `ModBlocks.CRYSTAL_FLUID` 断言流体方块。

## Requirements

* `IslandTemplate.MAGMA.build` 必须在 `center.west().south()` 放置 Crystal Fluid source block。
* 保持现有 magma island 其他方块位置不变。
* 新增 GameTest 通过 `/island create magma` 断言生成后的 Crystal Fluid 位置。
* 不迁移旧 VIC biome 修改、不迁移旧底部平台配置、不新增命令参数。

## Acceptance Criteria

* [x] Magma island 模板包含 Crystal Fluid source block。
* [x] GameTest 覆盖 `/island create magma` 后的 Crystal Fluid 相对位置。
* [x] Stage 21 checklist 不再把 Crystal Fluid placement 标记为 TODO。
* [x] 新增 Stage 70 清单记录行为、来源和验证。
* [x] `./gradlew.bat compileJava`、`./gradlew.bat runGameTestServer`、`./gradlew.bat build` 通过。
* [x] `git diff --check` 和 `git diff --cached --check` 通过。

## Definition of Done

* 实现只复用现有 `ModBlocks.CRYSTAL_FLUID`，不新增注册或配置。
* 旧源码相对坐标在 PRD/清单中记录，便于后续审计。
* 完成后中文提交、归档任务并记录会话。

## Technical Approach

* 在 `IslandTemplate.MAGMA.build` 增加 `crystalFluid` block state 并调用 `set(level, center.west().south(), crystalFluid)`。
* 在 `IslandCommandGameTests` 增加 magma 模板命令测试，并在 `ModGameTests` 注册。
* 同步 `plans/stage-21-island-templates-checklist.md`、新增 `plans/stage-70-magma-island-crystal-fluid-checklist.md`，必要时更新 `island-command-guidelines.md`。

## Out of Scope

* 旧 VIC `changeBiome` 行为。
* 旧 VIC bottom block type / island size 配置。
* Garden of Glass 外部生态集成。

## Technical Notes

* 旧源码证据：`VICPlugin.java` 的 `FluidUtil.tryPlaceFluid(null, world, pos.west().south(), ...)`。
* 目标源码：`IslandTemplate.java`、`IslandCommandGameTests.java`、`ModGameTests.java`。
